package ratustele.pacpac.entities;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Form {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long formId;
    private String fields;
    private String formName;

    @Column(length = 100000)
    private String text;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "entityId",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_ENTITY_FORM"))
    private ratustele.pacpac.entities.Entity entity;
}
