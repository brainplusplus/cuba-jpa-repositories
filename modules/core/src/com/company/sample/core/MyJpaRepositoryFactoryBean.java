package com.company.sample.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;

public class MyJpaRepositoryFactoryBean extends JpaRepositoryFactoryBean {

    protected final Log log = LogFactory.getLog(getClass());

    public MyJpaRepositoryFactoryBean(Class repositoryInterface) {
        super(repositoryInterface);
        log.info("\nJPA Bean Init\n");
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        log.info("\nJPA Bean create factory\n");
        return super.createRepositoryFactory(entityManager);
    }

}
