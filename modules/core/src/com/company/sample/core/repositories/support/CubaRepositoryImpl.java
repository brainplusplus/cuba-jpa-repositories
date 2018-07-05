package com.company.sample.core.repositories.support;

import com.company.sample.core.repositories.CubaRepository;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import org.apache.commons.lang.NotImplementedException;

import java.io.Serializable;

public class CubaRepositoryImpl<T extends Entity<ID>, ID extends Serializable> implements CubaRepository<T, ID> {

    private Persistence persistence;
    private Class<T> domainClass;
    private DataManager dataManager;



    public CubaRepositoryImpl(Class<T> domainClass, Persistence persistence, DataManager dataManager) {
        this.persistence = persistence;
        this.domainClass = domainClass;
        this.dataManager = dataManager;
    }

    @Override
    public T findOne(ID id, String view) {
        return persistence.getEntityManager().find(domainClass, id, view);
    }

    @Override
    public Iterable<T> findAll(String view) {
        return dataManager.load(domainClass).view(view).list();
    }

    @Override
    public Iterable<T> findAll(Iterable<ID> ids, String view) {
        throw new NotImplementedException();
    }

    @Override
    public <S extends T> S save(S entity) {
        return persistence.getEntityManager().merge(entity);
    }

    @Override
    public <S extends T> Iterable<S> save(Iterable<S> entities) {
        throw new NotImplementedException();
    }

    @Override
    public T findOne(ID id) {
        return persistence.getEntityManager().find(domainClass, id, "_local");
    }

    @Override
    public boolean exists(ID id) {
        return dataManager.load(domainClass).id(id).optional().isPresent();
    }

    @Override
    public Iterable<T> findAll() {
        return dataManager.load(domainClass).view("_local").list();
    }

    @Override
    public Iterable<T> findAll(Iterable<ID> ids) {
        throw new NotImplementedException();
    }

    @Override
    public long count() {
        return dataManager.load(domainClass).view("_local").list().size();
    }

    @Override
    public void delete(ID id) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(T entity) {
        persistence.getEntityManager().remove(entity);
    }

    @Override
    public void delete(Iterable<? extends T> entities) {
        entities.forEach(persistence.getEntityManager()::remove);
    }

    @Override
    public void deleteAll() {
        throw new NotImplementedException();
    }
}
