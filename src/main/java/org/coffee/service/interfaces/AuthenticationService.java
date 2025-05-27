package org.coffee.service.interfaces;

import org.coffee.dto.AuthResult;
import org.coffee.exception.AuthenticationException;

public interface AuthenticationService {

    AuthResult authenticate(String username, String password) throws AuthenticationException;
}
