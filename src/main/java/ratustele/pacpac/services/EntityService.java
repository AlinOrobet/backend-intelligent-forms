package ratustele.pacpac.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ratustele.pacpac.entities.Entity;
import ratustele.pacpac.entities.tokens.ResetPasswordToken;
import ratustele.pacpac.entities.tokens.VerificationToken;
import ratustele.pacpac.models.EntityModel;
import ratustele.pacpac.models.RegisterResponse;
import ratustele.pacpac.repositories.EntityRepository;
import ratustele.pacpac.repositories.ResetPasswordRepository;
import ratustele.pacpac.repositories.VerificationTokenRepository;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntityService {
    
    private final EntityRepository entityRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final ResetPasswordRepository resetPasswordRepository;

    public RegisterResponse registerEntity(EntityModel model) {

        Optional<Entity> checkIfEmailIsUsed =
                entityRepository.findByEmail(model.getEmail());

        Optional<Entity> checkIfUsernameExists =
                entityRepository.findByEntityName(model.getEntityName());

        if(checkIfEmailIsUsed.isPresent()) {
            return new RegisterResponse(null, "Email is used!");
        }

        if(checkIfUsernameExists.isPresent()) {
            return new RegisterResponse(null, "Username already exists!");
        }

        Entity entity = new Entity();
        entity.setEntityName(model.getEntityName());
        entity.setEmail(model.getEmail());
        entity.setEntityAddress(model.getEntityAddress());
        entity.setPassword(passwordEncoder.encode(model.getPassword()));
        entity.setEntityType(model.getEntityType());
        entity.setFiscalCode(model.getFiscalCode());
        entity.setSubscription(model.getSubscription());

        entityRepository.save(entity);
        return new RegisterResponse(entity, "Account successfully registered!");
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

    public Entity findUserByToken(String token) {
        Optional<Entity> entity = entityRepository.findById(verificationTokenRepository.findByToken(token).getId());
        return entity.orElse(null);
    }

    public Entity findUserByEmail(String email) {
        Optional<Entity> entity = entityRepository.findByEmail(email);
        return entity.orElse(null);
    }

    public void createPasswordResetTokenForUser(Entity entity, String token) {
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken(entity, token);
        resetPasswordRepository.save(resetPasswordToken);
    }

    public String validatePasswordResetToken(String token) {
        ResetPasswordToken resetPasswordToken = resetPasswordRepository.findByToken(token);
        if(resetPasswordToken == null) {
            return "invalid";
        }

        Calendar calendar = Calendar.getInstance();

        if(resetPasswordToken.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0) {
            resetPasswordRepository.delete(resetPasswordToken);
            return "expired";
        }

        return "valid";
    }

    public Optional<Entity> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(resetPasswordRepository.findByToken(token).getEntity());
    }

    public void changePassword(Entity entity, String newPassword) {
        entity.setPassword(passwordEncoder.encode(newPassword));
        entityRepository.save(entity);
    }
}
