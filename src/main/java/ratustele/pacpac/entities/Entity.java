package ratustele.pacpac.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ratustele.pacpac.enums.EntityType;
import ratustele.pacpac.enums.Subscription;

import java.util.Collection;
import java.util.List;

@jakarta.persistence.Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Entity implements UserDetails {

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

    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    private Long fiscalCode;
    private Subscription subscription;
    private boolean isSubEnabled;
    private int numberOfForms = 0;
    private boolean enabled = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(entityType.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
       // return this.enabled;
        return true;
    }
}
