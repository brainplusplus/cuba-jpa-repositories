package com.cuba.jpa.repository.query;

import com.haulmont.cuba.core.global.FluentLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;

import java.lang.reflect.Method;

public class CubaJpqlQuery extends CubaAbstractQuery {

    private static final Log log = LogFactory.getLog(CubaJpqlQuery.class.getName());

    public CubaJpqlQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, String query) {
        super(method, metadata, factory);
        jpql = generateQueryMetadata(method, query);
    }

    @Override
    protected Object doExecute(FluentLoader.ByQuery query, Object[] parameters) {
        return query
                .view(jpql.getView())
                .list();
    }
}
