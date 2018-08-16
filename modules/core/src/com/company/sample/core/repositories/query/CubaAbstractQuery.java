package com.company.sample.core.repositories.query;

import com.company.sample.core.repositories.config.CubaView;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FluentLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CubaAbstractQuery implements RepositoryQuery {

    private static final Log log = LogFactory.getLog(CubaDeleteQuery.class.getName());

    protected final Method method;
    protected final RepositoryMetadata metadata;
    protected final ProjectionFactory factory;
    protected JpqlMetadata jpql;

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

    @Override
    public Object execute(Object[] parameters) {
        log.debug(String.format("Query: \"%s\" Parameters: \"%s\"", jpql, Arrays.toString(parameters)));
        Assert.isTrue(parameters.length == jpql.getParameterNames().size(),
                String.format("Parameters list sizes in JPQL and in method are not equal: Method: %s JPQL: %s", Arrays.toString(parameters), jpql.getParameterNames()));
        //Issues with generic types in DataManager, so need to cast it forcefully.
        if (!Entity.class.isAssignableFrom(metadata.getDomainType())){
            throw new IllegalStateException("CUBA can process only entities of class com.haulmont.cuba.core.entity.Entity");
        }
        DataManager dataManager = getDataManager();
        FluentLoader.ByQuery query = dataManager
                .load((Class<? extends Entity>) metadata.getDomainType())
                .query(jpql.getJpql());
        //Cannot set parameters in map because we need implicit conversion disabled.
        for (int i = 0; i < parameters.length; i++){
            query.parameter(jpql.getParameterNames().get(i), parameters[i], false);
        }

        return doExecute(query, parameters);
    }

    protected JpqlMetadata generateQueryMetadata(Method method, String query) {
        List<String> parameters = new ArrayList<>();
        Matcher m = Pattern.compile("(:[a-zA-Z]+)").matcher(query);
        while (m.find()){
            //extracting name from query removing colon
            String name = query.substring(m.start(), m.end()).replaceAll(":", "");
            parameters.add(name);
        }
        JpqlMetadata jpqlMetadata = new JpqlMetadata(query, parameters, true);
        CubaView viewAnnotation = method.getDeclaredAnnotation(CubaView.class);
        if (viewAnnotation != null){
            jpqlMetadata.setView(viewAnnotation.value());
        }
        return jpqlMetadata;
    }

    protected JpqlMetadata generateQueryMetadata(Method method, RepositoryMetadata metadata, PartTree qryTree) {
        JpqlMetadata jpqlMetadata = JpqlQueryGenerator.generateJpqlMetadata(metadata, qryTree);
        CubaView viewAnnotation = method.getDeclaredAnnotation(CubaView.class);
        if (viewAnnotation != null){
            jpqlMetadata.setView(viewAnnotation.value());
        }
        return jpqlMetadata;
    }

    protected abstract Object doExecute(FluentLoader.ByQuery query, Object[] parameters);
}
