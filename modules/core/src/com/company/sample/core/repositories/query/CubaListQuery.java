package com.company.sample.core.repositories.query;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.global.AppBeans;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Arrays;

public class CubaListQuery implements RepositoryQuery {

    private static final Log log = LogFactory.getLog(CubaListQuery.class.getName());

    private final Method method;
    private final RepositoryMetadata metadata;
    private final ProjectionFactory factory;
    private final NamedQueries namedQueries;

    private JpqlMetadata jpql;

    public CubaListQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries namedQueries) {
        this.method = method;
        this.metadata = metadata;
        this.factory = factory;
        this.namedQueries = namedQueries;
        jpql = generateQueryMetadata();
    }

    @Override
    public Object execute(Object[] parameters) {
        log.info(String.format("Query: \"%s\" Parameters: \"%s\"", jpql, Arrays.toString(parameters)));
        Query query = getPersistence().getEntityManager().createQuery(jpql.getJpql()); //TODO Cache this query
        Assert.isTrue(parameters.length == jpql.getParameterNames().size(),
                "Parameters list sizes in JPQL and in method are not equal: Method: "+Arrays.toString(parameters)+" JPQL: "+jpql.getParameterNames());
        for (int i = 0; i < parameters.length; i++){
            query.setParameter(jpql.getParameterNames().get(i), parameters[i], false);
        }
        return query.getResultList();
    }

    public JpqlMetadata generateQueryMetadata() {
        PartTree qryTree = new PartTree(method.getName(), metadata.getDomainType());
        return JpqlQueryGenerator.getSqlString(metadata, qryTree);
    }

    @Override
    public QueryMethod getQueryMethod() {
        return new QueryMethod(method, metadata, factory);
    }

    public Persistence getPersistence() {
        return AppBeans.get(Persistence.class);
    }
}
