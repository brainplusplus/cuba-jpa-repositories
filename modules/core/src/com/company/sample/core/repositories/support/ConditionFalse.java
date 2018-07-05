package com.company.sample.core.repositories.support;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Always false condition.
 * Tells spring context never load bean with such Condition.
 */
public class ConditionFalse implements Condition {
    /**{@inheritDoc}*/
    @Override public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return false;
    }
}
