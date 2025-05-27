package org.coffee.persistence.dao;

import org.coffee.persistence.entity.User;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.NoResultException;
import java.util.Optional;

@ApplicationScoped
public class UserDAO extends BaseDAO<User> {
    protected UserDAO() {
        super(User.class);
    }

    public Optional<User> findUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        try {
            User user = em.createQuery(
                            "SELECT e FROM User e WHERE e.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(user);
        }
        catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
