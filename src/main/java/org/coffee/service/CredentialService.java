package org.coffee.service;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Optional;

@RequestScoped
public class CredentialService implements Serializable {

    private Optional<Object> getSessionAttribute(String attributeName) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null) {

            return Optional.empty();
        }

        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        if (request == null) {
            System.err.println("CredentialService: HttpServletRequest is null.");
            return Optional.empty();
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(session.getAttribute(attributeName));
    }

    public Optional<String> getCurrentUsernameOpt() {
        return getSessionAttribute("loggedInUsername")
                .map(Object::toString);
    }

    public String getCurrentUsername() {
        return getCurrentUsernameOpt().orElse("");
    }


    public Optional<String> getCurrentRoleOpt() {
        return getSessionAttribute("loggedInUserRole")
                .map(Object::toString);
    }

    public String getCurrentRole() {
        return getCurrentRoleOpt().orElse(null);
    }

    public Optional<String> getCurrentActualRole() {
        return getSessionAttribute("loggedInUserRole")
                .map(Object::toString);
    }
}
