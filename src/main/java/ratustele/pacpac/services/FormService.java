package ratustele.pacpac.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ratustele.pacpac.entities.Entity;
import ratustele.pacpac.entities.Form;
import ratustele.pacpac.enums.Subscription;
import ratustele.pacpac.models.FormModel;
import ratustele.pacpac.repositories.EntityRepository;
import ratustele.pacpac.repositories.FormRepository;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FormService {

    private final FormRepository formRepository;
    private final EntityRepository entityRepository;

    /**
     * Method that adds a form in the database, if all requirements are met.
     * @param formModel The RequestBody.
     * @return Returns a status message.
     */
    public String addForm(FormModel formModel) {
        Long entityId = formModel.getEntityId();
        Form form = new Form();
        Entity entity = entityRepository.findById(entityId).orElseThrow();

        boolean userCanCreateForm =
                checkIfUserIsAllowedToCreateMoreForms(entity);
        if(!userCanCreateForm) {
            return "You have exceeded the maximum number of forms" +
                    " you can create, per your subscription!";
        }

        if(formWithThisNameAlreadyExists(formModel.getFormName(), entity)) {
            return "Form with this name already exists!";
        }

        form.setFormName(formModel.getFormName());
        form.setEntity(entity);
        form.setText(formModel.getText());
        form.setFields(formModel.getFields());

        formRepository.save(form);
        entity.setNumberOfForms(entity.getNumberOfForms() + 1);
        entityRepository.save(entity);

        return "Form created successfully!";
    }

    /**
     * Method that checks if a user has already created a form with given name.
     * @param formName The name to be searched.
     * @param entity The user.
     * @return Returns a boolean value.
     */
    private boolean formWithThisNameAlreadyExists(String formName, Entity entity) {
        Optional<Form> searchForm = formRepository.
                findByFormNameAndEntity(formName, entity);
        return searchForm.isPresent();
    }

    /**
     * Method that checks if a user has not exceeded
     * the number of forms he can create, based on his subscription.
     * @param entity The entity that is trying to create a form.
     * @return A boolean variable.
     */
    private boolean checkIfUserIsAllowedToCreateMoreForms(Entity entity) {
        Subscription subscription = entity.getSubscription();
        int numberOfForms = entity.getNumberOfForms();

        switch (subscription) {
            case FREE -> {
                if(numberOfForms == 3) {
                    return false;
                }
            }
            case BASIC -> {
                if(numberOfForms == 50) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Method that deletes a form from the database.
     * @param id The id f the form that is to be deleted.
     * @return Returns the body of the deleted form.
     */
    public FormModel deleteForm(Long id) {
        Form form = formRepository.findById(id).orElseThrow();
        formRepository.delete(form);

        Entity entity = entityRepository.findById(form.getEntity().getEntityId()).orElseThrow();
        entity.setNumberOfForms(entity.getNumberOfForms() - 1);
        entityRepository.save(entity);

        return FormModel.builder()
                .formName(form.getFormName())
                .fields(form.getFields())
                .text(form.getText())
                .entityId(form.getEntity().getEntityId())
                .build();
    }

    /**
     * Method that edits a form that exists in the database.
     * @param formModel The updated form data.
     * @param id The id of the form to be updated.
     * @return Returns a status message.
     */
    public String editForm(FormModel formModel, Long id) {
        Form form = formRepository.findById(id).orElseThrow();
        form.setFormName(formModel.getFormName());
        form.setText(formModel.getText());
        form.setFields(formModel.getFields());

        if(formWithThisNameAlreadyExists(formModel.getFormName(), form.getEntity())) {
            return "Form with this name already exists!";
        } else {
            formRepository.save(form);
            return "Form updated successfully!";
        }
    }
}
