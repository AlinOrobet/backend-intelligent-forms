package ratustele.pacpac.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormModel {
    private String formName;
    private String fields;
    private String text;
    private Long entityId;
}
