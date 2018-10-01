package com.company.sample.core.repositories;

import com.haulmont.cuba.jpa.repository.config.CubaJpaRepository;
import com.haulmont.cuba.jpa.repository.config.CubaView;
import com.company.sample.entity.Customer;
import com.company.sample.entity.SalesOrder;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends CubaJpaRepository<SalesOrder, UUID> {

    @CubaView("_minimal")
    List<SalesOrder> findByCustomer(Customer customer);

    List<SalesOrder> findByCustomerNameAndCustomerAddressCity (String name, String city);

    long countSalesOrdersByCustomer(Customer customer);

    long countSalesOrdersByCustomerAddressCity (String city);

    List<SalesOrder> findSalesOrderByDateAfter(Date date);

    List<SalesOrder> findSalesOrderByDateBefore(Date date);

}
