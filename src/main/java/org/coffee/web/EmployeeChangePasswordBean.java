package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.entity.Employee;
import org.coffee.service.interfaces.EmployeeService;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Named
@RequestScoped
public class EmployeeChangePasswordBean {

    @Getter
    @Setter
    private String oldPassword;

    @Getter
    @Setter
    private String newPassword;

    @EJB
    private EmployeeService employeeService;

    @Inject
    private UserSessionBean sessionBean;


    public String changePassword() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession session = request.getSession(false);

        Employee employee = (Employee) sessionBean.getLoggedInUser();

        try {
            if(employee == null){
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Password change failed",
                                "Not logged in."));
                return null;
            }

            boolean result = employeeService.changePassword(employee, oldPassword, newPassword);

            if(result){
                session.invalidate();
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Password change success",
                                "Password changed successfully. Please login again."));

                return "/admin/login.xhtml?faces-redirect=true";
            }

            return null;
        }
        catch(Exception e){
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Password change failed",
                            e.getCause().getMessage()));
            return null;
        }
    }
}
