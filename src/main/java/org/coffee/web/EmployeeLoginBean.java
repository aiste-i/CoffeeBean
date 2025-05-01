package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.dao.EmployeeDAO;
import org.coffee.persistence.entity.Employee;
import org.coffee.util.PasswordUtil;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Named
@RequestScoped
public class EmployeeLoginBean {

    @Inject
    private EmployeeDAO employeeDAO; // Your DAO for Employee data

    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String password;


    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        try {
            Employee employee = employeeDAO.findByUsername(username); // Find by username

            if (employee != null && PasswordUtil.checkPassword(password, employee.getPassword())) { // Check hashed password!

                // --- Login Successful ---
                HttpSession session = request.getSession(); // Get or create session
                session.setAttribute("loggedInUserType", "Employee"); // Mark type
                session.setAttribute("loggedInUserId", employee.getId()); // Store ID
                 session.setAttribute("loggedInUserRole", employee.getRole());
                session.setAttribute("loggedInUsername", employee.getUsername()); // For display etc.

                // Redirect to the employee/admin area
                return "/admin/dashboard.xhtml?faces-redirect=true";

            } else {
                // --- Login Failed ---
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login Failed", "Invalid username or password."));
                return null; // Stay on the same page
            }
        } catch (Exception e) {
            // Log the exception properly
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login Failed", "Invalid username or password."));
            return null; // Stay on the same page
        }
    }

    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession session = request.getSession(false); // Get session only if it exists
        if (session != null) {
            session.invalidate(); // Invalidate session
        }
        return "/index.xhtml?faces-redirect=true";
    }

    public boolean isLoggedIn() {
        return FacesContext.getCurrentInstance().getExternalContext().getRemoteUser() != null;
    }

    // Optional: Get username (relies on container security)
    public String getUsername() {
        return FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    }
}
