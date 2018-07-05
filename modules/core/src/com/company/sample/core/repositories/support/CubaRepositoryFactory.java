package com.company.sample.core.repositories.support;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import java.io.Serializable;

public class CubaRepositoryFactory extends RepositoryFactorySupport {

    @Override
    public <T, ID extends Serializable> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        return new CubaEntityInformation<>(domainClass);
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation metadata) {
        Object persistence =  AppBeans.get(Persistence.class);
        Object domainClass = metadata.getDomainType();
        Object dataManager = AppBeans.get(DataManager.class);
        return getTargetRepositoryViaReflection(metadata, domainClass, persistence, dataManager);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return CubaRepositoryImpl.class;
    }
}
