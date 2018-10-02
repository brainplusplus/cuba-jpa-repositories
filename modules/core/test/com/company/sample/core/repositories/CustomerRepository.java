package com.company.sample.core.repositories;

import com.company.sample.entity.Customer;
import com.haulmont.addons.cuba.jpa.repositories.config.CubaJpaRepository;
import com.haulmont.addons.cuba.jpa.repositories.config.CubaView;
import com.haulmont.addons.cuba.jpa.repositories.config.JpqlQuery;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends CubaJpaRepository<Customer, UUID> {

    List<Customer> findByName(String name);

    List<Customer> findByAddressCity(String city);

    List<Customer> findByNameIsIn(List<String> names);

    long countCustomersByAddressCity(String city);

    List<Customer> findByAddressCityIn(List<String> cities);

    void removeByName(String name); //TODO doesn't work due to DataManager limitations

    @CubaView("_minimal")
    @JpqlQuery("select c from sample$Customer c where c.name like concat(:name, '%')")
    List<Customer> findByNameStartingWith(String name);
}
