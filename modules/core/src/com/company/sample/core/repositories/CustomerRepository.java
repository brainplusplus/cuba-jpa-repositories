package com.company.sample.core.repositories;

import com.company.sample.core.repositories.config.CubaJpaRepository;
import com.company.sample.core.repositories.config.CubaView;
import com.company.sample.core.repositories.config.JpqlQuery;
import com.company.sample.entity.Customer;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends CubaJpaRepository<Customer, UUID> {

    List<Customer> findByName(String name);

    List<Customer> findByAddressCity(String city);

    @CubaView("_minimal")
    @JpqlQuery("select c from sample$Customer c where c.name like concat(:name, '%')")
    List<Customer> findByNameStartingWith(String name);
}
