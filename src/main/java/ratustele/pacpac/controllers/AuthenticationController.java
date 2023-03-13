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
@CrossOrigin("localhost:3000")
public class AuthenticationController {

    private final AuthenticationService service;
    private final EntityService entityService;
    private final EmailService emailService;

    /**
     * API for authenticating.
     * @param request Request body: server needs to receive valid email and password for success.
     * @return Returns a JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    /**
     * API for requesting a password change.
     * @param resetPasswordRequest Request body: server needs to receive an email for success.
     * @param request The HttpServletRequest
     * @return Returns the link that will be sent in an email.
     */
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

    /**
     * API for saving a password after it has been changed.
     * @param token Reset password token stored in the database for the user.
     * @param passwordModel Request body: server needs to receive an email for success.
     * @return Returns a status message.
     */
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

    /**
     * Method that creates a link based on the parameters of a request.
     * @param request The HttpServletRequest.
     * @return Returns a link.
     */
    private String applicationUrl(HttpServletRequest request) {
        return "http://"
                + request.getServerName()
                + ":" + request.getServerPort()
                + request.getContextPath();
    }

    /**
     * Method sends reset password email to user.
     * @param entity The User.
     * @param applicationUrl Reset password link.
     * @param token Reset password token stored in the database for the user.
     * @return Returns the reset password link.
     */
    private String passwordResetTokenMail(Entity entity, String applicationUrl, String token) {
        String url = applicationUrl
                + "/authentication/resetPassword?token="
                + token;
        String message = "Click this link to reset your password!\n" + url;
        EmailDetails emailDetails = new EmailDetails(entity.getEmail(), message, "Reset Password");
        emailService.sendSimpleMail(emailDetails);
        return url;
    }
}
