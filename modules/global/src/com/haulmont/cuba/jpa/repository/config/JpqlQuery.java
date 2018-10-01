package com.haulmont.cuba.jpa.repository.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify com.haulmont.cuba.jpa.repository.query for CUBA Query methods
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JpqlQuery {

    /**
     * JPA com.haulmont.cuba.jpa.repository.query string
     * @return
     */
    String value();

}
