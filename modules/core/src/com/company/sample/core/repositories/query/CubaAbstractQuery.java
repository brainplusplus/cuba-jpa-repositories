package com.company.sample.core.repositories.query;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;

import java.lang.reflect.Method;

public abstract class CubaAbstractQuery implements RepositoryQuery {

    protected final Method method;
    protected final RepositoryMetadata metadata;
    protected final ProjectionFactory factory;

    public CubaAbstractQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory) {
        this.method = method;
        this.metadata = metadata;
        this.factory = factory;
    }

    @Override
    public QueryMethod getQueryMethod() {
        return new QueryMethod(method, metadata, factory);
    }

    public DataManager getDataManager(){
        return AppBeans.get(DataManager.class);
    }
}
