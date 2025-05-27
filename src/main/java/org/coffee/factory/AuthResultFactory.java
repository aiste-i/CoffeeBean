package org.coffee.factory;

import org.coffee.dto.AuthResult;

public interface AuthResultFactory{

    AuthResult create(boolean success, Object user) throws IllegalArgumentException;
}
