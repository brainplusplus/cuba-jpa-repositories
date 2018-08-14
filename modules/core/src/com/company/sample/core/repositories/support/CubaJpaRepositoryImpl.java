package com.company.sample.core.repositories.support;

import com.company.sample.core.repositories.config.CubaJpaRepository;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FluentLoader;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.context.annotation.Conditional;

import java.io.Serializable;

@Conditional(ConditionFalse.class) //@NoRepositoryBean doesn't work
public class CubaJpaRepositoryImpl<T extends Entity<ID>, ID extends Serializable> implements CubaJpaRepository<T, ID> {

    private Class<T> domainClass;

    public CubaJpaRepositoryImpl(Class<T> domainClass) {
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
    public Iterable<T> findAll(Iterable<ID> ids) {//TODO implement find by ID list
        throw new NotImplementedException();
    }

    @Override
    public long count() {
        //return getDataManager().getCount(LoadContext.create(domainClass)); //TODO Effective since 6.10
        FluentLoader<T, ID> fluentLoader = getDataManager().load(domainClass).view("_local");
        return fluentLoader.list().size();
    }

    @Override
    public void delete(ID id) { //TODO Need to add removal by entity ID to entityManager
        EntityManager entityManager = getPersistence().getEntityManager();
        T entity = entityManager.find(domainClass, id, "_local");
        entityManager.remove(entity);
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
    public void deleteAll() {//TODO implement total delete by entity class
        Iterable<T> entities = getDataManager().load(domainClass).list();
        entities.forEach(getPersistence().getEntityManager()::remove);
    }
}
