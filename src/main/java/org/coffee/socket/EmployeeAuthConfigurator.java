package org.coffee.socket;

import org.coffee.persistence.entity.enums.UserRole;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

public class EmployeeAuthConfigurator extends ServerEndpointConfig.Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        super.modifyHandshake(sec, request, response);
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        if (httpSession != null) {
            Object employeeUsernameObj = httpSession.getAttribute("loggedInUsername");
            Object employeeRoleObj = httpSession.getAttribute("loggedInUserRole");

            if (employeeUsernameObj instanceof String && employeeRoleObj instanceof UserRole) {
                UserRole role = (UserRole) employeeRoleObj;
                if (role == UserRole.EMPLOYEE || role == UserRole.ADMIN) {
                    sec.getUserProperties().put("employeeUsername", employeeUsernameObj);
                    sec.getUserProperties().put("employeeRole", employeeRoleObj);
                    System.out.println("EmployeeAuthConfigurator: Employee " + employeeUsernameObj + " authorized for WebSocket.");
                }
            }
        }
    }
}
