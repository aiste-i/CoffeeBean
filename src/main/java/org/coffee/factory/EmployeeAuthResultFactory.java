package org.coffee.factory;

import lombok.NoArgsConstructor;
import org.coffee.annotations.EmployeeAuth;
import org.coffee.dto.EmployeeAuthResult;
import org.coffee.persistence.entity.Employee;

import javax.enterprise.context.RequestScoped;

@EmployeeAuth
@RequestScoped
public class EmployeeAuthResultFactory implements AuthResultFactory{

    @Override
    public EmployeeAuthResult create(boolean success, Object user)
            throws IllegalArgumentException {

        return new EmployeeAuthResult(success, (Employee) user);
    }
}
