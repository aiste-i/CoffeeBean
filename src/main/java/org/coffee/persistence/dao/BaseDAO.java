package org.coffee.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import static org.coffee.constants.Constants.persistenceUnit;

public abstract class BaseDAO<T> {

    @PersistenceContext(unitName = persistenceUnit)
    protected EntityManager em;

    private final Class<T> entityClass;

    protected BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void persist(T entity) {
        em.persist(entity);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void removeById(Object primaryKey) {
        T entityToRemove = em.find(entityClass, primaryKey);
        if (entityToRemove != null) {
            em.remove(entityToRemove);
        }
    }

    public T find(Object id) {
        return em.find(entityClass, id);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public T update(T entity) {
        return em.merge(entity);
    }
}