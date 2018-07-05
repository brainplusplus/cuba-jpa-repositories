package com.company.sample.core.repositories;

import com.company.sample.core.repositories.config.CubaJpaRepository;
import com.company.sample.entity.Customer;
import com.company.sample.entity.Order;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends CubaJpaRepository<Order, UUID> {

    List<Order> findByCustomer(Customer customer);
}
