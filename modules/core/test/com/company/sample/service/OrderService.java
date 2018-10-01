package com.company.sample.service;


import com.company.sample.entity.Customer;
import com.company.sample.entity.SalesOrder;

import java.util.List;

public interface OrderService {
    String NAME = "sample_OrderService";

    List<SalesOrder> getSalesOrdersForCustomer(Customer customer);
}