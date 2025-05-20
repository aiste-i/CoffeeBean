package org.coffee.validator;

import org.coffee.persistence.dao.EmployeeDAO;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;

@Named
@ApplicationScoped
public class UsernameExistenceValidator implements Validator<String>  {

    @Inject
    private EmployeeDAO employeeDAO;

    @Override
    public void validate(FacesContext context, UIComponent component, String username) throws ValidatorException {
        if (username == null || username.trim().isEmpty() || employeeDAO == null) {
            return;
        }
        try {
            boolean employeeExists = employeeDAO.findByUsername(username).isPresent();
            if(employeeExists)
            {
                throw new ValidatorException(
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Username already exists.",
                                "Please choose a different username.")
                );
            }

        }
        catch (NoResultException ignored){}

    }
}
