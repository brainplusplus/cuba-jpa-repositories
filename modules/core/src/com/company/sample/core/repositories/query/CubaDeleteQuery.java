package com.company.sample.core.repositories.query;

import com.haulmont.cuba.core.global.FluentLoader;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.parser.PartTree;

import java.lang.reflect.Method;
import java.util.Arrays;

public class CubaDeleteQuery extends CubaAbstractQuery {

    private static final Log log = LogFactory.getLog(CubaDeleteQuery.class.getName());

    //TODO need to implement batch delete in dataManager
    public CubaDeleteQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, PartTree qryTree) {
        super(method, metadata, factory);
        jpql = generateQueryMetadata(method, metadata, qryTree);
    }

    @Override
    public Object execute(Object[] parameters) {
        log.debug(String.format("Query: \"%s\" Parameters: \"%s\"", jpql, Arrays.toString(parameters)));
        throw new NotImplementedException("Batch delete is not supported yet");
    }

    @Override
    protected Object doExecute(FluentLoader.ByQuery query, Object[] parameters) {
        throw new NotImplementedException("Batch delete is not supported yet");
    }
}
