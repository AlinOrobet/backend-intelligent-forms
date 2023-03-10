package ratustele.pacpac.events.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import ratustele.pacpac.emails.EmailDetails;
import ratustele.pacpac.entities.Entity;
import ratustele.pacpac.events.RegistrationCompleteEvent;
import ratustele.pacpac.services.EmailService;
import ratustele.pacpac.services.EntityService;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
public class RegistrationCompleteListener
        implements ApplicationListener<RegistrationCompleteEvent> {

    private final EntityService entityService;
    private final EmailService emailService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // Create VerificationToken
        Entity entity = event.getEntity();
        String token = UUID.randomUUID().toString();
        entityService.saveVerificationTokenForEntity(token, entity);

        // Create VerifyRegistration link
        String url = event.getApplicationUrl()
                + "/registration/verifyRegistration?token="
                + token;

        // FIXME: emails do not work at the moment
        String message = "Click this link to verify your account:\n" + url;
        EmailDetails emailDetails = new EmailDetails(entity.getEmail(), message, "Verify Your Account!");
        emailService.sendSimpleMail(emailDetails);

        // Send the email (mock at the moment)
        log.info("Click the following link to verify you account: {}", url);
    }
}
