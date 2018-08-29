package com.company.sample.core.repositories;

import com.cuba.jpa.repository.config.CubaJpaRepository;
import com.cuba.jpa.repository.config.CubaView;
import com.cuba.jpa.repository.config.JpqlQuery;
import com.company.sample.entity.Customer;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends CubaJpaRepository<Customer, UUID> {

    List<Customer> findByName(String name);

    List<Customer> findByAddressCity(String city);

    List<Customer> findByNameIsIn(List<String> names);

    long countCustomersByAddressCity(String city);

    void removeByName(String name); //TODO doesn't work due to DataManager limitations

    @CubaView("_minimal")
    @JpqlQuery("select c from sample$Customer c where c.name like concat(:name, '%')")
    List<Customer> findByNameStartingWith(String name);
}
