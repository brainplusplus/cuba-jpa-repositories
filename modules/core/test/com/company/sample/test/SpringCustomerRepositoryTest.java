package com.company.sample.test;

import com.company.sample.SampleTestContainer;
import com.company.sample.core.repositories.CustomerRepository;
import com.company.sample.entity.Address;
import com.company.sample.entity.Customer;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SpringCustomerRepositoryTest {

    @ClassRule
    public static SampleTestContainer cont = SampleTestContainer.Common.INSTANCE;

    private Persistence persistence;
    private Metadata metadata;
    private CustomerRepository customerRepository;
    private EntityStates entityStates;

    private Customer customer1, customer2, customer3;

    @Before
    public void setUp() throws Exception {
        persistence = cont.persistence();
        metadata = cont.metadata();
        customerRepository = AppBeans.get(CustomerRepository.class);
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


        persistence.runInTransaction(em -> {
            em.persist(customer1);
            em.persist(customer2);
            em.persist(customer3);
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
    public void testFindCustomersByCityInList(){
        List<Customer> customers = customerRepository.findByAddressCityIn(Arrays.asList("Samara", "Springfield"));
        assertTrue(customers.contains(customer1));
        assertTrue(customers.contains(customer2));
        assertTrue(customers.contains(customer3));
        assertEquals(3, customers.size());
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
    public void testFindCustomerById() {
        Customer customer;
        customer = customerRepository.findOne(customer1.getId());
        assertEquals(customer1, customer);
        assertTrue(entityStates.isDetached(customer));
    }

    @Test
    public void testFindCustomerByName() {
        List<Customer> customers = customerRepository.findByName(customer1.getName());
        assertEquals(1, customers.size());
        assertEquals(customer1, customers.get(0));
    }

    @Test
    public void testFindCustomerByAddressCity() {
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
    public void testQueryWithInClause(){
        List<Customer> customers = customerRepository.findByNameIsIn(Arrays.asList(customer1.getName(), customer2.getName()));
        assertEquals(2, customers.size());
        assertTrue(customers.contains(customer1) && customers.contains(customer2));

        customers = customerRepository.findByNameIsIn(Collections.singletonList("Fake Name Should not be found"));
        assertTrue(CollectionUtils.isEmpty(customers));
    }

}
