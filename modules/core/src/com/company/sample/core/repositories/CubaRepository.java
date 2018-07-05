package com.company.sample.core.repositories;

import com.haulmont.cuba.core.entity.Entity;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

public interface CubaRepository<T extends Entity<ID>, ID extends Serializable> extends CrudRepository <T, ID> {

    T findOne(ID id, String view);

    Iterable<T> findAll(String view);

    Iterable<T> findAll(Iterable<ID> ids, String view);

}
