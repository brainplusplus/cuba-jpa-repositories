package com.company.sample.core.repositories;

import com.company.sample.entity.Customer;
import com.company.sample.entity.SalesOrder;
import com.haulmont.addons.cuba.jpa.repositories.config.CubaJpaRepository;
import com.haulmont.addons.cuba.jpa.repositories.config.CubaView;

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
