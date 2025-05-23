package org.coffee.persistence.dao;

import org.coffee.persistence.entity.User;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
public class UserDAO extends BaseDAO<User> {
    protected UserDAO() {
        super(User.class);
    }

    public User findByUsername(String email) {
        return em.createQuery(
                        "SELECT u FROM User u WHERE u.email = :email",
                        User.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    public User findById(Long id) {
        return em.find(User.class, id);
    }

    public User merge(User user) {
        return em.merge(user);
    }
}
