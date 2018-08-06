package com.company.sample.core.repositories.query;

import java.util.Collections;
import java.util.List;

public class JpqlMetadata {

    private String jpql;
    private List<String> parameterNames;
    private boolean ordinal;
    private String view = "_local";

    public JpqlMetadata(String jpql, List<String> parameterNames, boolean ordinal) {
        this.jpql = jpql;
        this.parameterNames = parameterNames;
        this.ordinal = ordinal;
    }

    public JpqlMetadata(String jpql, List<String> parameterNames, boolean ordinal, String view) {
        this(jpql, parameterNames, ordinal);
        this.view = view;
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

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    @Override
    public String toString() {
        return "JpqlMetadata{" +
                "jpql='" + jpql + '\'' +
                ", parameterNames=" + parameterNames +
                ", ordinal=" + ordinal +
                ", view='" + view + '\'' +
                '}';
    }
}
