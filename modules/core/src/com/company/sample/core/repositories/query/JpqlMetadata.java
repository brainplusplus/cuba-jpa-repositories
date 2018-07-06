package com.company.sample.core.repositories.query;

import java.util.Collections;
import java.util.List;

public class JpqlMetadata {

    private String jpql;
    private List<String> parameterNames;
    private boolean ordinal;

    public JpqlMetadata(String jpql, List<String> parameterNames, boolean ordinal) {
        this.jpql = jpql;
        this.parameterNames = parameterNames;
        this.ordinal = ordinal;
    }

    public String getJpql() {
        return jpql;
    }

    public List<String> getParameterNames() {
        return Collections.unmodifiableList(parameterNames);
    }

    public boolean isOrdinal() {
        return ordinal;
    }

    @Override
    public String toString() {
        return "JpqlMetadata{" +
                "jpql='" + jpql + '\'' +
                ", parameterNames=" + parameterNames +
                '}';
    }
}
