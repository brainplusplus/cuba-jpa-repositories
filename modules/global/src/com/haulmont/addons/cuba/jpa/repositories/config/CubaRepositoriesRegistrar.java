package com.haulmont.addons.cuba.jpa.repositories.config;

import com.haulmont.addons.cuba.jpa.repositories.support.CubaRepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

public class CubaRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport {

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableCubaRepositories.class;
    }

    @Override
    protected RepositoryConfigurationExtension getExtension() {
        return new CubaRepositoryConfigurationExtension();
    }
}
