package ratustele.pacpac.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import ratustele.pacpac.entities.Entity;

@Getter
@Setter
public class RegistrationCompleteEvent extends ApplicationEvent {

    private Entity entity;
    private String applicationUrl;

    public RegistrationCompleteEvent(Entity entity, String applicationUrl) {
        super(entity);
        this.entity = entity;
        this.applicationUrl = applicationUrl;
    }
}
