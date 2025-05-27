package org.coffee.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import java.util.Collection;
import java.util.List;

import static org.coffee.constants.Constants.PERSISTENCE_UNIT;

public abstract class BaseDAO<T> {

    @PersistenceContext(unitName = PERSISTENCE_UNIT)
    protected EntityManager em;

    private final Class<T> entityClass;

    protected BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Transactional(Transactional.TxType.MANDATORY)
    public void persist(T entity) {
        em.persist(entity);
    }

    @Transactional(Transactional.TxType.MANDATORY)
    public void removeById(Object primaryKey) {
        T entityToRemove = em.find(entityClass, primaryKey);
        if (entityToRemove != null) {
            em.remove(entityToRemove);
        }
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public T find(Object id) {
        return em.find(entityClass, id);
    }

    @Transactional(Transactional.TxType.MANDATORY)
    public T update(T entity) {
        return em.merge(entity);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<T> findAll() {
        return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
                .getResultList();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public void flush() {
        em.flush();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<T> findByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();

        return em.createQuery(
                        "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e.id IN :ids", entityClass)
                .setParameter("ids", ids)
                .getResultList();
    }



}