package com.company.sample.core.repositories.query;

import com.company.sample.core.repositories.config.CubaView;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FluentLoader;
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
        jpql = generateQueryMetadata(method, metadata);
    }

    @Override
    public Object execute(Object[] parameters) {
        log.info(String.format("Query: \"%s\" Parameters: \"%s\"", jpql, Arrays.toString(parameters)));
        Assert.isTrue(parameters.length == jpql.getParameterNames().size(),
                String.format("Parameters list sizes in JPQL and in method are not equal: Method: %s JPQL: %s", Arrays.toString(parameters), jpql.getParameterNames()));

        //Issues with generic types in DataManager, so need to cast it forcefully.
        if (!Entity.class.isAssignableFrom(metadata.getDomainType())){
            throw new IllegalStateException("CUBA can process only entities of class com.haulmont.cuba.core.entity.Entity");
        }
        FluentLoader.ByQuery query = getDataManager()
                .load((Class<? extends Entity>) metadata.getDomainType())
                .query(jpql.getJpql());
        //Cannot set parameters in map because we need implicit conversion disabled.
        for (int i = 0; i < parameters.length; i++){
            query.parameter(jpql.getParameterNames().get(i), parameters[i], false);
        }

        return query
                .view(jpql.getView())
                .list();
    }

    public JpqlMetadata generateQueryMetadata(Method method, RepositoryMetadata metadata) {
        PartTree qryTree = new PartTree(method.getName(), metadata.getDomainType());
        JpqlMetadata jpqlMetadata = JpqlQueryGenerator.generateJpqlMetadata(metadata, qryTree);
        CubaView viewAnnotation = method.getDeclaredAnnotation(CubaView.class);
        if (viewAnnotation != null){
            jpqlMetadata.setView(viewAnnotation.value());
        }
        return jpqlMetadata;
    }

    @Override
    public QueryMethod getQueryMethod() {
        return new QueryMethod(method, metadata, factory);
    }

    public DataManager getDataManager(){
        return AppBeans.get(DataManager.class);
    }
}
