package com.cuba.jpa.repository.support;

import com.haulmont.cuba.core.entity.Entity;
import org.springframework.data.repository.core.support.AbstractEntityInformation;

import java.io.Serializable;

public class CubaEntityInformation<T, ID extends Serializable> extends AbstractEntityInformation<T, ID> {

    public CubaEntityInformation(Class<T> domainClass) {
        super(domainClass);
    }

    @Override
    public ID getId(T object) {
        if (object == null) {
            return null;
        }
        if (Entity.class.isAssignableFrom(getJavaType())) {
            Entity entity = (Entity) object;
            return (ID) entity.getId();
        } else {
            throw new IllegalStateException("Wrong entity type");
        }
    }

    @Override
    public Class<ID> getIdType() {
        try {
            return (Class<ID>) getJavaType().getMethod("getId").getReturnType();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Wrong entity type");
        }
    }
}
