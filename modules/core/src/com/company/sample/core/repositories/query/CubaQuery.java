package com.company.sample.core.repositories.query;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
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

public class CubaQuery implements RepositoryQuery {

    private static final Log log = LogFactory.getLog(CubaQuery.class.getName());

    private final Method method;
    private final RepositoryMetadata metadata;
    private final ProjectionFactory factory;
    private final NamedQueries namedQueries;

    private JpqlMetadata jpql;

    public CubaQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries namedQueries) {
        this.method = method;
        this.metadata = metadata;
        this.factory = factory;
        this.namedQueries = namedQueries;
        jpql = generateQueryMetadata();
    }

    @Override
    public Object execute(Object[] parameters) {
        log.info(String.format("Query: \"%s\" Parameters: \"%s\"", jpql, Arrays.toString(parameters)));
        Query query = getPersistence().getEntityManager().createQuery(jpql.getJpql());
        Assert.isTrue(parameters.length == jpql.getParameterNames().size(), "Parameters in JPQL and parameters in method are not equal");
        for (int i = 0; i < parameters.length; i++){
            query.setParameter(jpql.getParameterNames().get(i), parameters[i]);
        }
        return query.getResultList();
    }

    public JpqlMetadata generateQueryMetadata() {
        PartTree qryTree = new PartTree(method.getName(), metadata.getDomainType());
        return CubaQueryGenerator.getSqlString(metadata, qryTree);
    }

    @Override
    public QueryMethod getQueryMethod() {
        return new QueryMethod(method, metadata, factory);
    }

    public Persistence getPersistence() {
        return AppBeans.get(Persistence.class);
    }

    public DataManager getDataManager() {
        return AppBeans.get(DataManager.class);
    }


}
