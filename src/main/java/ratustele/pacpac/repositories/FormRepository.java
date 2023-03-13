package ratustele.pacpac.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ratustele.pacpac.entities.Entity;
import ratustele.pacpac.entities.Form;

import java.util.Optional;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {

    Optional<Form> findByFormName(String formName);

    Optional<Form> findByFormNameAndEntity(String formName, Entity entity);
}
