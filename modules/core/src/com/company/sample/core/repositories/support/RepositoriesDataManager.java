package com.company.sample.core.repositories.support;

import com.haulmont.cuba.core.app.DataManagerBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RepositoriesDataManager extends DataManagerBean {

    private static final Log log = LogFactory.getLog(RepositoriesDataManager.class.getName());

    public RepositoriesDataManager() {
        log.debug("Using DataManager with batch delete support");
    }
}
