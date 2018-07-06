package com.company.sample.core.repositories.query;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.global.AppBeans;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CubaJpqlQuery implements RepositoryQuery {


    private static final Log log = LogFactory.getLog(CubaJpqlQuery.class.getName());

    private final Method method;
    private final RepositoryMetadata metadata;
    private final ProjectionFactory factory;

    private JpqlMetadata jpql;

    public CubaJpqlQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, String query) {
        this.method = method;
        this.metadata = metadata;
        this.factory = factory;
        List<String> parameters = new ArrayList<>();
        int position = 1;
        Matcher m = Pattern.compile("(\\?[1-9]+[0-9]*)").matcher(query);
        while (m.find()){
            parameters.add(Integer.toString(position));
            position++;
        }
        jpql = new JpqlMetadata(query, parameters, true);
    }

    @Override
    public Object execute(Object[] parameters) {
        Query query = getPersistence().getEntityManager().createQuery(jpql.getJpql());
        Assert.isTrue(parameters.length == jpql.getParameterNames().size(),
                "Parameters list sizes in JPQL and in method are not equal: Method: "+Arrays.toString(parameters)+" JPQL: "+jpql.getParameterNames());
        for (int i = 0; i < parameters.length; i++){
            query.setParameter(Integer.parseInt(jpql.getParameterNames().get(i)), parameters[i], false);
        }
        return query.getResultList();
    }

    @Override
    public QueryMethod getQueryMethod() {
        return new QueryMethod(method, metadata, factory);
    }


    public Persistence getPersistence() {
        return AppBeans.get(Persistence.class);
    }

}
