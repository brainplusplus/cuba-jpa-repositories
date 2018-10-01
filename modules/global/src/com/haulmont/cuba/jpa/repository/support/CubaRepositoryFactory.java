package com.haulmont.cuba.jpa.repository.support;

import com.haulmont.cuba.jpa.repository.query.CubaQueryLookupStrategy;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;

import java.io.Serializable;

public class CubaRepositoryFactory extends RepositoryFactorySupport {

    @Override
    public <T, ID extends Serializable> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        return new CubaEntityInformation<>(domainClass);
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation metadata) {
        Object domainClass = metadata.getDomainType();
        return getTargetRepositoryViaReflection(metadata, domainClass);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return CubaJpaRepositoryImpl.class;
    }


    @Override
    protected QueryLookupStrategy getQueryLookupStrategy(QueryLookupStrategy.Key key, EvaluationContextProvider evaluationContextProvider) {
        return new CubaQueryLookupStrategy();
    }
}
