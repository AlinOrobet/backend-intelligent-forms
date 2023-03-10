package ratustele.pacpac.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ratustele.pacpac.emails.EmailDetails;
import ratustele.pacpac.entities.Entity;
import ratustele.pacpac.models.AuthenticationRequest;
import ratustele.pacpac.models.AuthenticationResponse;
import ratustele.pacpac.models.ResetPasswordRequest;
import ratustele.pacpac.services.AuthenticationService;
import ratustele.pacpac.services.EmailService;
import ratustele.pacpac.services.EntityService;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    private final EntityService entityService;
    private final EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest,
                                HttpServletRequest request) {
        Entity entity = entityService.findUserByEmail(resetPasswordRequest.getEmail());
        String url = "";

        if(entity != null) {
            String token = UUID.randomUUID().toString();
            entityService.createPasswordResetTokenForUser(entity, token);
            url = passwordResetTokenMail(entity, applicationUrl(request), token);
        }

        return url;
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token,
                               @RequestBody ResetPasswordRequest passwordModel) {
        String result = entityService.validatePasswordResetToken(token);
        if(!result.equalsIgnoreCase("valid")) {
            return "Failed to reset password!";
        }

        Optional<Entity> entity = entityService.getUserByPasswordResetToken(token);
        if(entity.isPresent()) {
            entityService.changePassword(entity.get(), passwordModel.getNewPassword());
            return "Password reset successfully";
        } else {
            return "User was not found!";
        }
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://"
                + request.getServerName()
                + ":" + request.getServerPort()
                + request.getContextPath();
    }

    private String passwordResetTokenMail(Entity entity, String applicationUrl, String token) {
        String url = applicationUrl
                + "/authentication/savePassword?token="
                + token;

        // sendVerificationEmail()
        // FIXME: emails do not work at the moment
        String message = "Click this link to reset your password!:\n" + url;
        EmailDetails emailDetails = new EmailDetails(entity.getEmail(), message, "Reset Password");
        emailService.sendSimpleMail(emailDetails);
        log.info("Click this link to reset your password: {}", url);
        return url;
    }
}
