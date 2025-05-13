package org.coffee.persistence.dao;

import org.coffee.persistence.entity.PasswordReset;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class PasswordResetDAO extends BaseDAO<PasswordReset> {
    protected PasswordResetDAO() {
        super(PasswordReset.class);
    }

    // tokens are valid for 1 hour
    public PasswordReset findValidResetByToken(String token) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        try{
            return em.createQuery(
                            "SELECT e FROM PasswordReset e " +
                                    "WHERE e.token = :token " +
                                    "AND e.created > :threshold " +
                                    "AND e.redeemed = false",
                            PasswordReset.class)
                    .setParameter("token", token)
                    .setParameter("threshold", oneHourAgo)
                    .getSingleResult();
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
