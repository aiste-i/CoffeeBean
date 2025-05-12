package org.coffee.web;

import lombok.Getter;
import lombok.Setter;
import org.coffee.persistence.entity.enums.EmployeeRole;
import org.coffee.service.EmployeeService;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.OptimisticLockException;
import javax.security.auth.login.CredentialException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Named
@RequestScoped
public class EmployeeChangeBean {

    @Getter
    @Setter
    private String oldPassword;

    @Getter
    @Setter
    private String newPassword;

    @Inject
    private EmployeeService employeeService;


    public String changePassword() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession session = request.getSession(false);

        if(oldPassword.equals(newPassword)){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Password change failed", "New password cannot be the same as old password"));
            return null;
        }

        if(newPassword.isEmpty()){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Password change failed", "New password cannot be empty"));
            return null;
        }

        if(Enum.valueOf(EmployeeRole.class, session.getAttribute("loggedInUserRole").toString()) != EmployeeRole.ADMIN){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Password change failed", "Password cannot be changed"));
            return null;
        }

        String loggedInUsername = session.getAttribute("loggedInUsername").toString();

        try{
            employeeService.changePassword(oldPassword, newPassword, loggedInUsername);
            session.invalidate();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Password change success", "Password changed successfully. Please login again."));

            return null;
        }catch(CredentialException ignored){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Password change failed", "Password cannot be changed"));
            return null;
        } catch (OptimisticLockException e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Password change failed", "Password cannot be changed. Please try again later."));
            return null;
        }
    }
}
