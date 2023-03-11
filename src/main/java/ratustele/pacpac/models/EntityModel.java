package ratustele.pacpac.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ratustele.pacpac.enums.EntityType;
import ratustele.pacpac.enums.Subscription;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityModel {
    private String entityName;
    private String institutionName;
    private String email;
    private String entityAddress;
    private String password;
    private EntityType entityType;
    private Long fiscalCode;
    private Subscription subscription;
}
