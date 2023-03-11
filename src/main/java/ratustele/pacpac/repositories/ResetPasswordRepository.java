package ratustele.pacpac.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ratustele.pacpac.entities.tokens.ResetPasswordToken;

@Repository
public interface ResetPasswordRepository extends JpaRepository<ResetPasswordToken, Long> {
    ResetPasswordToken findByToken(String token);
}
