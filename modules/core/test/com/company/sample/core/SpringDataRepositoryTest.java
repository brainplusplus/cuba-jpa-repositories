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

    private Customer customer1, customer2;
    private SalesOrder order1;

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

        order1 = metadata.create(SalesOrder.class);
        order1.setCustomer(customer1);
        order1.setNumber("111");
        order1.setDate(new Date());

        try (Transaction tx = persistence.getTransaction()) {
            customerRepository.save(customer1);
            customerRepository.save(customer2);
            orderRepository.save(order1);
            tx.commit();
        };
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
    public void testCount() throws SQLException {
        long cnt = customerRepository.count();
        assertEquals(2, cnt);
    }


    @Test
    public void testDeleteCustomer() throws SQLException {
        try (Transaction tx = persistence.getTransaction()) {
            persistence.setSoftDeletion(false);
            Customer customer = customerRepository.findOne(customer2.getId());
            customerRepository.delete(customer);
            tx.commit();
        }

        QueryRunner runner = new QueryRunner(persistence.getDataSource());
        Map<String, Object> row = runner.query("select * from SAMPLE_CUSTOMER where ID = '" + customer2.getId() + "'",
                new MapHandler());
        assertNull(row);
    }


    @Test
    public void testDeleteCustomerById() throws SQLException {
        try (Transaction tx = persistence.getTransaction()) {
            persistence.setSoftDeletion(false);
            customerRepository.delete(customer2.getId());
            tx.commit();
        }

        QueryRunner runner = new QueryRunner(persistence.getDataSource());
        Map<String, Object> row = runner.query("select * from SAMPLE_CUSTOMER where ID = '" + customer2.getId() + "'",
                new MapHandler());
        assertNull(row);
    }


    @Test (expected = NotImplementedException.class) //Unless we implement batch delete in data manager
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
    public void testFindByAssociationProperty() {
        List<SalesOrder> orders = orderRepository.findByCustomer(customer1);
        assertEquals(1, orders.size());
        assertEquals(order1, orders.get(0));
    }
}
