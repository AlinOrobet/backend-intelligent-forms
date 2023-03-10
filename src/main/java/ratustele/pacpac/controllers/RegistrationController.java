package ratustele.pacpac.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import ratustele.pacpac.emails.EmailDetails;
import ratustele.pacpac.entities.Entity;
import ratustele.pacpac.entities.tokens.VerificationToken;
import ratustele.pacpac.events.RegistrationCompleteEvent;
import ratustele.pacpac.models.AuthenticationResponse;
import ratustele.pacpac.models.EntityModel;
import ratustele.pacpac.models.RegisterResponse;
import ratustele.pacpac.services.EmailService;
import ratustele.pacpac.services.EntityService;
import ratustele.pacpac.services.JwtService;

@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {

    private final EntityService entityService;
    private final ApplicationEventPublisher publisher;
    private final EmailService emailService;
    private final JwtService jwtService;

    @PostMapping("/createAccount")
    private String registerEntity(@RequestBody EntityModel model, final HttpServletRequest request) {
        RegisterResponse response = entityService.registerEntity(model);
        if(response.getEntity() == null) {
            return response.getResponseMessage();
        } else {
            publisher.publishEvent(new RegistrationCompleteEvent(
                    response.getEntity(),
                    applicationUrl(request)));
            return "Account successfully created!";
        }
    }

//    @GetMapping("/verifyRegistration")
//    public String verifyRegistration(@RequestParam("token") String token) {
//        String result = entityService.validateVerificationToken(token);
//        if(result.equalsIgnoreCase("valid")) {
//            return "Registration verified successfully!";
//        } else {
//            return "Failed to verify registration!";
//        }
//    }

    @GetMapping("/verifyRegistration")
    public AuthenticationResponse verifyRegistration(@RequestParam("token") String token) {
        String result = entityService.validateVerificationToken(token);
        if(result.equalsIgnoreCase("valid")) {
            log.info(entityService.findUserByToken(token).toString());
            var jwtToken = jwtService.generateToken(entityService.findUserByToken(token));
            return AuthenticationResponse.builder().token(jwtToken).build();
        } else {
            return null;
        }
    }

    @GetMapping("/resendVerificationToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken,
                                          HttpServletRequest request) {
        VerificationToken verificationToken = entityService.generateNewVerificationToken(oldToken);
        Entity entity = verificationToken.getEntity();
        resendVerificationTokenMail(entity, applicationUrl(request), verificationToken);
        return "Verification link sent!";
    }

    private void resendVerificationTokenMail(Entity entity,
                                             String applicationUrl,
                                             VerificationToken verificationToken) {
        String url = applicationUrl
                + "/registration/verifyRegistration?token="
                + verificationToken.getToken();

        // sendVerificationEmail()
        // FIXME: emails are not working atm
        String message = "Click this link to verify your account:\n" + url;
        EmailDetails emailDetails = new EmailDetails(entity.getEmail(), message, "Verify Your Account!");
        emailService.sendSimpleMail(emailDetails);
        log.info("Click this link to verify your account: {}", url);
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://"
                + request.getServerName()
                + ":" + request.getServerPort()
                + request.getContextPath();
    }
}
