package com.haulmont.addons.cuba.jpa.repositories.support;

import com.haulmont.addons.cuba.jpa.repositories.config.CubaJpaRepository;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;

import java.util.Collection;
import java.util.Collections;

public class CubaRepositoryConfigurationExtension extends RepositoryConfigurationExtensionSupport {
    /**
     * {@inheritDoc}
     */
    @Override
    public String getModuleName() {
        return "CUBA Platform";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getModulePrefix() {
        return "CUBA";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRepositoryFactoryBeanClassName() {
        return CubaRepositoryFactoryBean.class.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<Class<?>> getIdentifyingTypes() {
        return Collections.singleton(CubaJpaRepository.class);
    }
}
