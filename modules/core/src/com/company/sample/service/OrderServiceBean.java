package com.company.sample.service;

import com.company.sample.core.repositories.OrderRepository;
import com.company.sample.entity.Customer;
import com.company.sample.entity.SalesOrder;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@Service(OrderService.NAME)
public class OrderServiceBean implements OrderService {

    @Inject
    private OrderRepository orderRepository;

    @Override
    @Transactional
    public List<SalesOrder> getSalesOrdersForCustomer(Customer customer) {
        return Lists.newArrayList(orderRepository.findByCustomer(customer));
    }
}