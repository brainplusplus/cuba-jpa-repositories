package com.cuba.jpa.repository.support;

import com.cuba.jpa.repository.config.CubaJpaRepository;
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
    public String getRepositoryFactoryClassName() {
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
