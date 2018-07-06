package com.company.sample.core.repositories.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify query for CUBA Query methods
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JpaQuery {

    /**
     * JPA query string
     * @return
     */
    String value();

}
