package org.coffee.web;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.coffee.persistence.dao.EmployeeDAO;
import org.coffee.persistence.entity.Employee;
import org.coffee.service.EmployeeService;
import org.coffee.util.PasswordUtil;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

@Named
@SessionScoped
@NoArgsConstructor
public class EmployeeEmailChangeBean implements Serializable {

    @Inject
    private EmployeeService employeeService;

    @Inject
    private EmployeeDAO employeeDAO;

    @Getter @Setter
    private String newEmail;

    @Getter @Setter
    private String currentPassword;

    public String submit() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        HttpServletRequest req = (HttpServletRequest) ctx.getExternalContext().getRequest();
        HttpSession session = req.getSession(false);

        Long id = (Long) session.getAttribute("loggedInUserId");
        Employee employee = employeeDAO.find(id);
        if (employee == null) {
            ctx.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Change failed:", "Employee session invalid."));
            return null;
        }

        if (!PasswordUtil.checkPassword(currentPassword, employee.getPassword())) {
            ctx.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Change failed:", "Current password is incorrect."));
            return null;
        }

        try {
            employeeService.changeEmail(id, newEmail);
            ctx.getExternalContext().getFlash().setKeepMessages(true);
            ctx.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Email changed successfully", newEmail));
            return "/admin/dashboard.xhtml?faces-redirect=true";
        } catch (IllegalArgumentException e) {
            ctx.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Change failed:", e.getMessage()));
            return null;
        }
    }
}
