package ratustele.pacpac.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ratustele.pacpac.enums.EntityType;
import ratustele.pacpac.enums.Subscription;

@jakarta.persistence.Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Entity {

    @Id
    @SequenceGenerator(
            name = "entity_sequence",
            sequenceName = "entity_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "entity_sequence"
    )
    private Long entityId;
    private String entityName;
    private String email;
    private String password;
    private String entityAddress;
    private EntityType entityType;
    private Long fiscalCode;
    private Subscription subscription;
    private boolean isSubEnabled;
    private int numberOfForms = 0;
    private boolean enabled = false;
}
