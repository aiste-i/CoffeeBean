package org.coffee.service.interfaces;

import org.coffee.dto.EmployeeAuthResult;
import org.coffee.dto.UserAuthResult;
import org.coffee.exception.AuthenticationException;

public interface AuthenticationServiceInterface {

    UserAuthResult authenticateUser(String email, String password) throws AuthenticationException;

    EmployeeAuthResult authenticateEmployee(String username, String password) throws AuthenticationException;
}
