package com.company.sample.core;

import com.company.sample.SampleTestContainer;
import com.company.sample.core.repositories.CustomerRepository;
import com.company.sample.core.repositories.OrderRepository;
import com.company.sample.entity.Address;
import com.company.sample.entity.Customer;
import com.company.sample.entity.SalesOrder;
import com.haulmont.bali.db.MapHandler;
import com.haulmont.bali.db.QueryRunner;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.EntityStates;
import com.haulmont.cuba.core.global.Metadata;
import org.apache.commons.lang.NotImplementedException;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SpringDataRepositoryTest {

    @ClassRule
    public static SampleTestContainer cont = SampleTestContainer.Common.INSTANCE;

    private Persistence persistence;
    private Metadata metadata;
    private CustomerRepository customerRepository;
    private OrderRepository orderRepository;
    private EntityStates entityStates;

    private Customer customer1, customer2, customer3;
    private SalesOrder order1, order2, order3;

    @Before
    public void setUp() throws Exception {
        persistence = cont.persistence();
        metadata = cont.metadata();
        customerRepository = AppBeans.get(CustomerRepository.class);
        orderRepository = AppBeans.get(OrderRepository.class);
        entityStates = AppBeans.get(EntityStates.class);

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
        order1.setDate(new Date());


        order2 = metadata.create(SalesOrder.class);
        order2.setCustomer(customer1);
        order2.setNumber("112");
        order2.setDate(new Date());

        order3 = metadata.create(SalesOrder.class);
        order3.setCustomer(customer2);
        order3.setNumber("113");
        order3.setDate(new Date());

        persistence.runInTransaction(em -> {
            em.persist(customer1);
            em.persist(customer2);
            em.persist(customer3);
            em.persist(order1);
            em.persist(order2);
            em.persist(order3);
        });
    }

    @After
    public void tearDown() throws Exception {
        QueryRunner runner = new QueryRunner(persistence.getDataSource());
        runner.update("delete from SAMPLE_SALES_ORDER");
        runner.update("delete from SAMPLE_CUSTOMER");
    }

    @Test
    public void testSave() throws SQLException {
        Customer customer = metadata.create(Customer.class);
        customer.setName("customer");
        try (Transaction tx = persistence.getTransaction()) {
            customerRepository.save(customer);
            tx.commit();
        }

        QueryRunner runner = new QueryRunner(persistence.getDataSource());
        Map<String, Object> row = runner.query("select * from SAMPLE_CUSTOMER where ID = '" + customer.getId() + "'",
                new MapHandler());
        assertNotNull(row);
        assertNotNull(row.get("CREATE_TS"));
        assertNotNull(row.get("CREATED_BY"));
    }


    @Test
    public void testBasicCount() throws SQLException {
        long cnt = customerRepository.count();
        assertEquals(3, cnt);
    }


    @Test
    public void testCountCustomersByCity(){
        long first = customerRepository.countCustomersByAddressCity("Samara");
        assertEquals(1, first);
        long second = customerRepository.countCustomersByAddressCity("Springfield");
        assertEquals(2, second);
        long third = customerRepository.countCustomersByAddressCity("London");
        assertEquals(0, third);
    }

    @Test
    public void testCountOrdersByCustomer(){
        long first = orderRepository.countSalesOrdersByCustomer(customer1);
        assertEquals(2, first);
        long second = orderRepository.countSalesOrdersByCustomer(customer2);
        assertEquals(1, second);
        long third = orderRepository.countSalesOrdersByCustomer(customer3);
        assertEquals(0, third);
    }

    @Test
    public void testCountOrdersByCustomerCity(){
        long first = orderRepository.countSalesOrdersByCustomerAddressCity("Samara");
        assertEquals(2, first);
        long second = orderRepository.countSalesOrdersByCustomerAddressCity("Springfield");
        assertEquals(1, second);
        long third = orderRepository.countSalesOrdersByCustomerAddressCity("London");
        assertEquals(0, third);
    }


    @Test
    public void testDeleteCustomer() throws SQLException {
        try (Transaction tx = persistence.getTransaction()) {
            persistence.setSoftDeletion(false);
            Customer customer = customerRepository.findOne(customer3.getId());
            customerRepository.delete(customer);
            tx.commit();
        }

        QueryRunner runner = new QueryRunner(persistence.getDataSource());
        Map<String, Object> row = runner.query("select * from SAMPLE_CUSTOMER where ID = '" + customer3.getId() + "'",
                new MapHandler());
        assertNull(row);
    }


    @Test
    public void testDeleteCustomerById() throws SQLException {
        try (Transaction tx = persistence.getTransaction()) {
            persistence.setSoftDeletion(false);
            customerRepository.delete(customer3.getId());
            tx.commit();
        }

        QueryRunner runner = new QueryRunner(persistence.getDataSource());
        Map<String, Object> row = runner.query("select * from SAMPLE_CUSTOMER where ID = '" + customer3.getId() + "'",
                new MapHandler());
        assertNull(row);
    }


    @Test (expected = NotImplementedException.class) //TODO Unless we implement batch delete in DataManager
    public void testDeleteCustomerByName() throws SQLException {
        try (Transaction tx = persistence.getTransaction()) {
            persistence.setSoftDeletion(false);
            customerRepository.removeByName(customer2.getName());
            tx.commit();
        }
    }


    @Test
    public void testFindById() {
        Customer customer;
        customer = customerRepository.findOne(customer1.getId());
        assertEquals(customer1, customer);
        assertTrue(entityStates.isDetached(customer));
    }

    @Test
    public void testFindByProperty() {
        List<Customer> customers = customerRepository.findByName(customer1.getName());
        assertEquals(1, customers.size());
        assertEquals(customer1, customers.get(0));
    }

    @Test
    public void testFindByPropertyOfEmbedded() {
        List<Customer> customers = customerRepository.findByAddressCity(customer1.getAddress().getCity());
        assertEquals(1, customers.size());
        assertEquals(customer1, customers.get(0));
    }

    @Test
    public void testQueryWithAdvancedLike() {
        List<Customer> customers = customerRepository.findByNameStartingWith("cust");
        assertFalse(CollectionUtils.isEmpty(customers));
        assertEquals(1, customers.size());
        assertEquals(customer1, customers.get(0));
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
}
