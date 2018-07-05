package com.company.sample.service;

import com.company.sample.core.repositories.OrderRepository;
import com.company.sample.entity.Customer;
import com.company.sample.entity.Order;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service(OrderService.NAME)
public class OrderServiceBean implements OrderService {

    @Inject
    private OrderRepository orderRepository;

    @Override
    @Transactional
    public List<Order> getOrdersForCustomer(Customer customer) {
        Iterable<Order> orderIterable = orderRepository.findAll();
        List<Order> orders = new ArrayList<>();
        orderIterable.forEach(orders::add);
        return orders;
    }
}