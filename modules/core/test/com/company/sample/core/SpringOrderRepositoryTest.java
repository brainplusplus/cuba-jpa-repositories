package com.company.sample.core;

import com.company.sample.SampleTestContainer;
import com.company.sample.core.repositories.OrderRepository;
import com.company.sample.entity.Address;
import com.company.sample.entity.Customer;
import com.company.sample.entity.SalesOrder;
import com.haulmont.bali.db.QueryRunner;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SpringOrderRepositoryTest {

    @ClassRule
    public static SampleTestContainer cont = SampleTestContainer.Common.INSTANCE;

    private Persistence persistence;
    private Metadata metadata;
    private OrderRepository orderRepository;

    private Customer customer1, customer2, customer3;
    private SalesOrder order1, order2, order3, order4, order5;

    @Before
    public void setUp() throws Exception {
        persistence = cont.persistence();
        metadata = cont.metadata();
        orderRepository = AppBeans.get(OrderRepository.class);

        customer1 = metadata.create(Customer.class);
        customer1.setName("cust1");
        customer1.setAddress(new Address());
        customer1.getAddress().setCity("Samara");

        customer2 = metadata.create(Customer.class);
        customer2.setName("some cust 2");
        customer2.setAddress(new Address());
        customer2.getAddress().setCity("Springfield");

        customer3 = metadata.create(Customer.class);
        customer3.setName("another cust 3");
        customer3.setAddress(new Address());
        customer3.getAddress().setCity("Springfield");


        order1 = metadata.create(SalesOrder.class);
        order1.setCustomer(customer1);
        order1.setNumber("111");
        order1.setDate(Date.from(LocalDate.parse("2010-01-01").atStartOfDay().toInstant(ZoneOffset.UTC)));


        order2 = metadata.create(SalesOrder.class);
        order2.setCustomer(customer1);
        order2.setNumber("112");
        order2.setDate(Date.from(LocalDate.parse("2010-03-01").atStartOfDay().toInstant(ZoneOffset.UTC)));

        order3 = metadata.create(SalesOrder.class);
        order3.setCustomer(customer2);
        order3.setNumber("113");
        order3.setDate(Date.from(LocalDate.parse("2004-02-29").atStartOfDay().toInstant(ZoneOffset.UTC)));

        order4 = metadata.create(SalesOrder.class);
        order4.setCustomer(customer2);
        order4.setNumber("114");
        order4.setDate(Date.from(LocalDate.parse("2018-08-19").atStartOfDay().toInstant(ZoneOffset.UTC)));

        order5 = metadata.create(SalesOrder.class);
        order5.setCustomer(customer3);
        order5.setNumber(null);
        order5.setDate(Date.from(LocalDate.parse("2017-11-11").atTime(23, 59, 59).toInstant(ZoneOffset.UTC)));

        persistence.runInTransaction(em -> {
            em.persist(customer1);
            em.persist(customer2);
            em.persist(customer3);
            em.persist(order1);
            em.persist(order2);
            em.persist(order3);
            em.persist(order4);
            em.persist(order5);
        });
    }

    @After
    public void tearDown() throws Exception {
        QueryRunner runner = new QueryRunner(persistence.getDataSource());
        runner.update("delete from SAMPLE_SALES_ORDER");
        runner.update("delete from SAMPLE_CUSTOMER");
    }

    @Test
    public void testCountOrdersByCustomer(){
        long first = orderRepository.countSalesOrdersByCustomer(customer1);
        assertEquals(2, first);
        long second = orderRepository.countSalesOrdersByCustomer(customer2);
        assertEquals(2, second);
        long third = orderRepository.countSalesOrdersByCustomer(customer3);
        assertEquals(1, third);
    }

    @Test
    public void testCountOrdersByCustomerCity(){
        long first = orderRepository.countSalesOrdersByCustomerAddressCity("Samara");
        assertEquals(2, first);
        long second = orderRepository.countSalesOrdersByCustomerAddressCity("Springfield");
        assertEquals(3, second);
        long third = orderRepository.countSalesOrdersByCustomerAddressCity("London");
        assertEquals(0, third);
    }

    @Test
    public void testQueryOrderByCustomerWithAndClause(){
        List<SalesOrder> orders = orderRepository.findByCustomerNameAndCustomerAddressCity("some cust 2", "Springfield");
        assertEquals(1, orders.size());
        assertTrue(orders.contains(order3));
    }

    @Test
    public void testFindByAssociationProperty() {
        List<SalesOrder> orders = orderRepository.findByCustomer(customer1);
        assertEquals(2, orders.size());
        assertTrue(orders.contains(order1) && orders.contains(order2));
    }

    @Test
    public void testFindByDateAfter() {
        List<SalesOrder> orders = orderRepository.findSalesOrderByDateAfter(Date.from(LocalDate.parse("2010-05-05").atStartOfDay().toInstant(ZoneOffset.UTC)));
        assertEquals(2, orders.size());
        assertTrue(orders.contains(order4) && orders.contains(order5));
    }

    @Test
    public void testFindByDateBefore() {
        List<SalesOrder> orders = orderRepository.findSalesOrderByDateBefore(Date.from(LocalDate.parse("2010-05-05").atStartOfDay().toInstant(ZoneOffset.UTC)));
        assertEquals(3, orders.size());
        assertTrue(orders.contains(order1) && orders.contains(order2) && orders.contains(order3));
    }


}
