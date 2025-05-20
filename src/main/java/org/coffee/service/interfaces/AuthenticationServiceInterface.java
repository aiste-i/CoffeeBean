package org.coffee.service.interfaces;

import org.coffee.service.dto.EmployeeAuthResult;
import org.coffee.service.dto.UserAuthResult;
import org.coffee.service.exceptions.AuthenticationException;

public interface AuthenticationServiceInterface {

    UserAuthResult authenticateUser(String email, String password) throws AuthenticationException;

    EmployeeAuthResult authenticateEmployee(String username, String password) throws AuthenticationException;
}
