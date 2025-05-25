package org.coffee.validator;

import org.coffee.persistence.dao.EmployeeDAO;
import org.coffee.persistence.entity.Employee;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

@Named
@ApplicationScoped
public class UsernameExistenceValidator implements Validator<String>  {

    @Inject
    private EmployeeDAO employeeDAO;

    @Override
    public void validate(FacesContext context, UIComponent component, String username) throws ValidatorException {
        if (username == null || username.trim().isEmpty()) {
            return;
        }
        Optional<Employee> employee = employeeDAO.findByUsername(username);
        if(employee.isPresent()) {
            throw new ValidatorException(
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Username already exists", "Please choose a different username.")
            );
        }
    }
}
