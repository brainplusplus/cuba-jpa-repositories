package com.company.sample.core.repositories.support;

import com.company.sample.core.repositories.CubaRepository;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.context.annotation.Conditional;

import java.io.Serializable;

@Conditional(ConditionFalse.class)
public class CubaRepositoryImpl<T extends Entity<ID>, ID extends Serializable> implements CubaRepository<T, ID> {

    private Class<T> domainClass;

    public CubaRepositoryImpl(Class<T> domainClass) {
        this.domainClass = domainClass;
    }

    public Persistence getPersistence() {
        return AppBeans.get(Persistence.class);
    }

    public DataManager getDataManager() {
        return AppBeans.get(DataManager.class);
    }

    @Override
    public T findOne(ID id, String view) {
        return getPersistence().getEntityManager().find(domainClass, id, view);
    }

    @Override
    public Iterable<T> findAll(String view) {
        return getDataManager().load(domainClass).view(view).list();
    }

    @Override
    public Iterable<T> findAll(Iterable<ID> ids, String view) {
        return getDataManager().load(domainClass).view(view).list();
    }

    @Override
    public <S extends T> S save(S entity) {
        return getPersistence().getEntityManager().merge(entity);
    }

    @Override
    public <S extends T> Iterable<S> save(Iterable<S> entities) {
        throw new NotImplementedException();
    }

    @Override
    public T findOne(ID id) {
        return getPersistence().getEntityManager().find(domainClass, id, "_local");
    }

    @Override
    public boolean exists(ID id) {
        return getDataManager().load(domainClass).id(id).optional().isPresent();
    }

    @Override
    public Iterable<T> findAll() {
        return getDataManager().load(domainClass).view("_local").list();
    }

    @Override
    public Iterable<T> findAll(Iterable<ID> ids) {
        throw new NotImplementedException();
    }

    @Override
    public long count() {
        return getDataManager().load(domainClass).view("_local").list().size();
    }

    @Override
    public void delete(ID id) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(T entity) {
        getPersistence().getEntityManager().remove(entity);
    }

    @Override
    public void delete(Iterable<? extends T> entities) {
        entities.forEach(getPersistence().getEntityManager()::remove);
    }

    @Override
    public void deleteAll() {
        throw new NotImplementedException();
    }
}
