package ratustele.pacpac.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ratustele.pacpac.entities.Entity;

import java.util.Optional;

@Repository
public interface EntityRepository extends JpaRepository<Entity, Long> {
    Optional<Entity> findByEmail(String email);
    Optional<Entity> findByEntityName(String entityName);
}
