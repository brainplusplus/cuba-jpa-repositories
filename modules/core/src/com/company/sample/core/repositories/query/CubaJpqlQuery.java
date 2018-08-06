package com.company.sample.core.repositories.query;

import com.company.sample.core.repositories.config.CubaView;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.FluentLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CubaJpqlQuery extends CubaAbstractQuery {


    private static final Log log = LogFactory.getLog(CubaJpqlQuery.class.getName());

    private JpqlMetadata jpql;

    public CubaJpqlQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, String query) {
        super(method, metadata, factory);
        jpql = generateQueryMetadata(method, query);
    }

    public JpqlMetadata generateQueryMetadata(Method method, String query) {
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


    @Override
    public Object execute(Object[] parameters) {
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


}
