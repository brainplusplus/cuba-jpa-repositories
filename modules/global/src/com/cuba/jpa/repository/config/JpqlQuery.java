package com.cuba.jpa.repository.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify com.cuba.jpa.repository.query for CUBA Query methods
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JpqlQuery {

    /**
     * JPA com.cuba.jpa.repository.query string
     * @return
     */
    String value();

}
