package ratustele.pacpac.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ratustele.pacpac.models.FormModel;
import ratustele.pacpac.services.FormService;

@RestController
@RequestMapping("/forms")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("localhost:3000")
public class FormController {

    private final FormService formService;

    /**
     * API for adding a new form in the database.
     * @param formModel The request body: all the data necessary to save the new form in the database.
     * @return Returns a status message.
     */
    @PostMapping("/createForm")
    public String postForm(@RequestBody FormModel formModel) {
        return formService.addForm(formModel);
    }

    /**
     * API for deleting a form
     * @param id The id of the form to be deleted.
     * @return Returns the body of the form that was deleted.
     */
    @DeleteMapping(value = "/deleteForm/{id}")
    public ResponseEntity<FormModel> deleteForm(@PathVariable Long id) {
        return ResponseEntity.ok().body(formService.deleteForm(id));
    }

    /**
     * API for editing a form.
     * @param id The id of the form to be edited.
     * @param formModel The request body: all the data necessary to edit the form.
     * @return Returns a status message.
     */
    @PutMapping("/editForm/{id}")
    public String editForm(@PathVariable Long id, @RequestBody FormModel formModel) {
        return formService.editForm(formModel, id);
    }
}
