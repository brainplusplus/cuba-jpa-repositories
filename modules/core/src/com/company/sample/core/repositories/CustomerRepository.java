package com.company.sample.core.repositories;

import com.company.sample.entity.Customer;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends CubaRepository<Customer, UUID> {

    Customer findByName(String name);

    List<Customer> findByAddressCity(String city);

    @Query("select c from #{#entityName} c where c.name like ?1%")
    List<Customer> findByNameStartingWith(String name);
}
