package com.haulmont.addons.cuba.jpa.repositories.support;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.data.repository.config.RepositoryBeanDefinitionParser;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

public class CubaRepositoryNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        RepositoryConfigurationExtension extension = new CubaRepositoryConfigurationExtension();
        RepositoryBeanDefinitionParser repositoryBeanDefinitionParser = new RepositoryBeanDefinitionParser(extension);
        registerBeanDefinitionParser("repositories", repositoryBeanDefinitionParser);
    }
}
