package org.coffee.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import java.util.List;

import static org.coffee.constants.Constants.PERSISTENCE_UNIT;

public abstract class BaseDAO<T> {

    @PersistenceContext(unitName = PERSISTENCE_UNIT)
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

    public List<T> findAll() {
        return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
                .getResultList();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public void flush() {
        em.flush();
    }

}