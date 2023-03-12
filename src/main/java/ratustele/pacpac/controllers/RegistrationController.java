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
@CrossOrigin("localhost:3000")
public class RegistrationController {

    private final EntityService entityService;
    private final ApplicationEventPublisher publisher;
    private final EmailService emailService;
    private final JwtService jwtService;

    /**
     * API for creating an account.
     * @param model The request body: all the data needed to create an account.
     * @param request The HttpServletRequest.
     * @return Returns a status message.
     */
    @PostMapping("/createAccount")
    private String registerEntity(@RequestBody EntityModel model, HttpServletRequest request) {
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

    /**
     * API for verifying an account.
     * @param token The validation token stored in the database for the user.
     * @return Returns a JWT (after verification, user will be logged in directly).
     */
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

    /**
     * API for requesting a resending of a Verify Account email.
     * @param oldToken The old verification token that is stored in the database for the user.
     * @param request The HttpServletRequest
     * @return Returns a message.
     */
    @GetMapping("/resendVerificationToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken,
                                          HttpServletRequest request) {
        VerificationToken verificationToken = entityService.generateNewVerificationToken(oldToken);
        Entity entity = verificationToken.getEntity();
        resendVerificationTokenMail(entity, applicationUrl(request), verificationToken);
        return "Verification link sent!";
    }

    /**
     * Method used to resend a Verify Account email.
     * @param entity The User.
     * @param applicationUrl The link of the web application.
     * @param verificationToken The verification token stored in the database for a user.
     */
    private void resendVerificationTokenMail(Entity entity,
                                             String applicationUrl,
                                             VerificationToken verificationToken) {
        String url = applicationUrl
                + "/registration/verifyRegistration?token="
                + verificationToken.getToken();

        String message = "Click this link to verify your account:\n" + url;
        EmailDetails emailDetails = new EmailDetails(entity.getEmail(), message, "Verify Your Account!");
        emailService.sendSimpleMail(emailDetails);
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
}
