package ratustele.pacpac.entities.tokens;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class VerificationToken {

    private static final int EXPIRATION_TIME = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private Date expirationTime;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "entityId",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_ENTITY_VERIFY_TOKEN"))
    private ratustele.pacpac.entities.Entity entity;

    public VerificationToken(ratustele.pacpac.entities.Entity entity, String token) {
        super();
        this.entity = entity;
        this.token = token;
        this.expirationTime = calculateExpirationTime();
    }

    public VerificationToken(String token) {
        super();
        this.token = token;
        this.expirationTime = calculateExpirationTime();
    }

    private Date calculateExpirationTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new java.util.Date().getTime());
        calendar.add(Calendar.MINUTE, VerificationToken.EXPIRATION_TIME);
        return new java.util.Date(calendar.getTime().getTime());
    }
}
