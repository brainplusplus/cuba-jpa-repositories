package com.company.sample.core.repositories.support;

import com.company.sample.core.repositories.config.CubaJpaRepository;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public class CubaJpaRepositoryImpl<T extends Entity<ID>, ID extends Serializable> implements CubaJpaRepository<T, ID> {

    private String defaultViewName = "_local";

    private Class<T> domainClass;

    public CubaJpaRepositoryImpl(Class<T> domainClass) {
        this.domainClass = domainClass;
    }

    protected DataManager getDataManager() {
        return AppBeans.get(DataManager.class);
    }

    @Override
    public T findOne(ID id, String view) {
        return getDataManager().load(domainClass).id(id).view(defaultViewName).one();
    }

    @Override
    public Iterable<T> findAll(String view) {
        return getDataManager().load(domainClass).view(view).list();
    }

    @Override
    public Iterable<T> findAll(Iterable<ID> ids, String view) { //TODO implement search by IDs
        throw new NotImplementedException();
    }

    @Override
    public <S extends T> S save(S entity) {
        return getDataManager().commit(entity);
    }

    @Override
    public <S extends T> Iterable<S> save(Iterable<S> entities) {
        throw new NotImplementedException();
    }

    @Override
    public T findOne(ID id) {
        return getDataManager().load(domainClass).id(id).view(defaultViewName).one();
    }

    @Override
    public boolean exists(ID id) {
        return getDataManager().load(domainClass).id(id).optional().isPresent();
    }

    @Override
    public Iterable<T> findAll() {
        return getDataManager().load(domainClass).view(defaultViewName).list();
    }

    @Override
    public Iterable<T> findAll(Iterable<ID> ids) {//TODO implement find by ID list
        throw new NotImplementedException();
    }

    @Override
    public long count() {
        //return getDataManager().getCount(LoadContext.create(domainClass)); //TODO Effective since 6.10
        return getDataManager().load(domainClass).view(defaultViewName).list().size();
    }

    @Override
    public void delete(ID id) { //TODO Need to add removal by entity ID to DataManager
        DataManager dataManager = getDataManager();
        T entity = dataManager.load(domainClass).id(id).view(defaultViewName).one();
        dataManager.remove(entity);
    }

    @Override
    public void delete(T entity) {
        getDataManager().remove(entity);
    }

    @Override
    public void delete(Iterable<? extends T> entities) {
        entities.forEach(getDataManager()::remove);
    }

    @Override
    public void deleteAll() {//TODO implement total delete by entity class in DataManager
        Iterable<T> entities = getDataManager().load(domainClass).list();
        entities.forEach(getDataManager()::remove);
    }

    @Override
    public String getDefaultViewName() {
        return defaultViewName;
    }

    @Override
    public void setDefaultViewName(String defaultViewName) {
        this.defaultViewName = defaultViewName;
    }

    public Class<T> getDomainClass() {
        return domainClass;
    }

    public void setDomainClass(Class<T> domainClass) {
        this.domainClass = domainClass;
    }
}
