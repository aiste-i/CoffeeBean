package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.dao.EmployeeDAO;
import org.coffee.persistence.entity.Employee;
import org.coffee.persistence.entity.enums.EmployeeRole;
import org.coffee.service.BusinessService;
import org.coffee.util.PasswordUtil;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Named
@RequestScoped
public class EmployeeSignUpBean {

    @Inject
    private BusinessService businessService;

    @Inject
    private EmployeeDAO employeeDAO;

    @Getter
    @Setter
    private String plainPassword;

    @Getter
    @Setter
    private Employee newEmployee = new Employee();

    @Transactional
    public String signUp() {
        FacesContext context = FacesContext.getCurrentInstance();
        // HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest(); // Needed only for manual session/login

        // --- Password Match Validation (Client-side with PrimeFaces `match` is good, but server-side is safer) ---
        // If not relying solely on PrimeFaces `match`, add manual check here if confirmPassword field exists.

        try {
            // Delegate creation AND hashing to the service
            // The service already handles linking to the business
            newEmployee.setPassword(PasswordUtil.hashPassword(plainPassword));
            newEmployee.setBusiness(businessService.getActiveBusiness());
            newEmployee.setRole(EmployeeRole.ADMIN);
            employeeDAO.persist(newEmployee);

            // --- !!! WARNING: Manual Login via Session is NOT Recommended !!! ---
            // This bypasses container security. Use request.login() or Jakarta Security API
            // instead, AFTER the user provides credentials on a LOGIN form, not signup.
            /*
            HttpSession session = request.getSession(); // Get or create session
            session.setAttribute("loggedInUserType", "Employee"); // Mark type
            session.setAttribute("loggedInUserId", newEmployee.getId()); // Store ID
            session.setAttribute("loggedInUserRole", newEmployee.getRole());
            session.setAttribute("loggedInUsername", newEmployee.getUsername());
            */
            // --- End Warning ---

            // Instead of logging in, show success and maybe redirect to LOGIN page
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sign Up Successful",
                    "Employee '" + newEmployee.getUsername() + "' created. Please log in.")); // Changed message

            // Clear the form state (since it's RequestScoped, it's implicitly cleared, but good practice)
            newEmployee = new Employee();
            plainPassword = null;

            // Redirect to the admin login page after successful signup
            // Use faces-redirect=true for a clean GET request after POST
            return "/admin/login.xhtml?faces-redirect=true";

            // Original code redirecting to dashboard after manual login (NOT RECOMMENDED):
            // return "/admin/dashboard.xhtml?faces-redirect=true";
        } catch (Exception e) {
            // Log the exception e properly using a Logger
            System.err.println("Error during sign up: " + e.getMessage()); // Replace with Logger
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Sign Up Failed", "An unexpected error occurred."));
            return null; // Stay on the same page
        }
    }

    // Helper method needed by the JSF page's f:selectItems
    public List<EmployeeRole> getAvailableRoles() {
        // Potentially restrict roles shown based on current user's role
        // if (!FacesContext.getCurrentInstance().getExternalContext().isUserInRole("ADMIN")) { ... }
        return Arrays.asList(EmployeeRole.values());
    }
}
