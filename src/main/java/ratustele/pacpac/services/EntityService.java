package ratustele.pacpac.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ratustele.pacpac.entities.Entity;
import ratustele.pacpac.entities.tokens.VerificationToken;
import ratustele.pacpac.models.EntityModel;
import ratustele.pacpac.repositories.EntityRepository;
import ratustele.pacpac.repositories.VerificationTokenRepository;

import java.util.Calendar;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EntityService {

    private final EntityRepository entityRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;

    public Entity registerEntity(EntityModel model) {
        Entity entity = new Entity();
        entity.setEntityName(model.getEntityName());
        entity.setEmail(model.getEmail());
        entity.setEntityAddress(model.getEntityAddress());
        entity.setPassword(passwordEncoder.encode(model.getPassword()));
        entity.setEntityType(model.getEntityType());
        entity.setFiscalCode(model.getFiscalCode());
        entity.setSubscription(model.getSubscription());

        entityRepository.save(entity);
        return entity;
    }

    public void saveVerificationTokenForEntity(String token, Entity entity) {
        VerificationToken verificationToken = new VerificationToken(entity, token);
        verificationTokenRepository.save(verificationToken);
    }

    public String validateVerificationToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if(verificationToken == null) {
            return "invalid";
        }

        Entity entity = verificationToken.getEntity();
        Calendar calendar = Calendar.getInstance();

        if(verificationToken.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0) {
            verificationTokenRepository.delete(verificationToken);
            return "expired";
        }

        entity.setEnabled(true);
        entityRepository.save(entity);
        return "valid";
    }

    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }
}
