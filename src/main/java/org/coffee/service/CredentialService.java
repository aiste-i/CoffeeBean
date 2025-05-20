package org.coffee.service;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Optional;

@RequestScoped
public class CredentialService implements Serializable {

    // Helper method to reduce duplication and add null safety
    private Optional<Object> getSessionAttribute(String attributeName) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null) {
            // Log error or handle appropriately if this service can be called outside JSF context
            System.err.println("CredentialService: FacesContext is null. Cannot access session attributes.");
            return Optional.empty();
        }

        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        if (request == null) {
            System.err.println("CredentialService: HttpServletRequest is null.");
            return Optional.empty();
        }

        HttpSession session = request.getSession(false); // Pass 'false' to NOT create a new session if none exists
        if (session == null) {
            // User is not logged in or session has expired/invalidated
            return Optional.empty();
        }
        return Optional.ofNullable(session.getAttribute(attributeName));
    }

    public Optional<String> getCurrentUsernameOpt() {
        return getSessionAttribute("loggedInUsername")
                .map(Object::toString); // Convert to String if present
    }

    public String getCurrentUsername() {
        return getCurrentUsernameOpt().orElse(""); // Or .orElse("") if you must return String
    }


    public Optional<String> getCurrentRoleOpt() {
        // Assuming role is stored as an Enum or String that can be .toString()'d
        return getSessionAttribute("loggedInUserRole")
                .map(Object::toString);
    }

    public String getCurrentRole() {
        return getCurrentRoleOpt().orElse(null); // Or .orElse("")
    }

    // Example for getting the actual Role Enum if stored as such
    public Optional<String> getCurrentActualRole() { // Assuming EmployeeRole is your enum
        return getSessionAttribute("loggedInUserRole")
                .map(Object::toString);
    }
}
