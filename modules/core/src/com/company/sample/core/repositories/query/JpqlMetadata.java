package com.company.sample.core.repositories.query;

import java.util.Collections;
import java.util.List;

public class JpqlMetadata {

    private String jpql;
    private List<String> parameterNames;

    public JpqlMetadata(String jpql, List<String> parameterNames) {
        this.jpql = jpql;
        this.parameterNames = parameterNames;
    }

    public String getJpql() {
        return jpql;
    }

    public List<String> getParameterNames() {
        return Collections.unmodifiableList(parameterNames);
    }

    @Override
    public String toString() {
        return "JpqlMetadata{" +
                "jpql='" + jpql + '\'' +
                ", parameterNames=" + parameterNames +
                '}';
    }
}
