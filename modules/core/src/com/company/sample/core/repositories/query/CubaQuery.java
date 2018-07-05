package com.company.sample.core.repositories.query;

import com.haulmont.cuba.core.Persistence;
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

import java.lang.reflect.Method;

public class CubaQuery implements RepositoryQuery {

    private static final Log log = LogFactory.getLog(CubaQuery.class.getName());

    private final Method method;
    private final RepositoryMetadata metadata;
    private final ProjectionFactory factory;
    private final NamedQueries namedQueries;

    private PartTree qryTree;

    public CubaQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries namedQueries) {
        this.method = method;
        this.metadata = metadata;
        this.factory = factory;
        this.namedQueries = namedQueries;
        qryTree = new PartTree(method.getName(), metadata.getDomainType());
    }

    @Override
    public Object execute(Object[] parameters) {
        String sqlString = CubaQueryGenerator.getSqlString(metadata, qryTree);
        log.info(sqlString);
        return getPersistence().getEntityManager().createQuery(sqlString).setParameter(0, parameters[0]).getResultList();
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
