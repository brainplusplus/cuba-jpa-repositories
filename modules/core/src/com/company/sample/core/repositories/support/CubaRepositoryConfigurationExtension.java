package com.company.sample.core.repositories.support;

import com.company.sample.core.repositories.config.CubaJpaRepository;
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
        return CubaRepositoryFactory.class.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<Class<?>> getIdentifyingTypes() {
        return Collections.<Class<?>>singleton(CubaJpaRepository.class);
    }
}
