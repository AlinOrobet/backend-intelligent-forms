package ratustele.pacpac.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ratustele.pacpac.entities.Entity;

@Repository
public interface EntityRepository extends JpaRepository<Entity, Long> {
}
