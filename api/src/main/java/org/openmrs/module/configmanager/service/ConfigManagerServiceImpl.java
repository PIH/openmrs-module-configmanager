/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.configmanager.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.jdbc.Work;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.configmanager.ConfigUtil;
import org.openmrs.module.configmanager.ConfigurationException;
import org.openmrs.module.configmanager.handler.ConfigurationHandler;
import org.openmrs.module.configmanager.schema.ConfigElement;
import org.openmrs.module.configmanager.schema.ConfigurationList;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the config manager service
 */
public class ConfigManagerServiceImpl extends BaseOpenmrsService implements ConfigManagerService {

	protected static final Log log = LogFactory.getLog(ConfigManagerServiceImpl.class);
    private DbSessionFactory sessionFactory;
    private Map<String, ConfigurationHandler> handlerMap = null;

	/**
	 * @see ConfigManagerService#runConfigurations(ConfigurationList)
	 */
	@Override
	public void runConfigurations(ConfigurationList configurationList) {
        if (configurationList != null) {
            for (ConfigElement config : configurationList.getConfigElements()) {
                ConfigurationHandler h = getHandler(config.getHandler());
                File configFile = ConfigUtil.getConfigurationFile(config.getFile());
                log.info("Running configuration in " + configFile + " with " + h.getName());
                h.handle(configFile, config.getParameters());
            }
        }
	}

    /**
     * @see ConfigManagerService#executeSql(String[])
     */
    @Override
    public void executeSql(final String... sqlStatements) {
        try {
            DbSession session = sessionFactory.getCurrentSession();
            session.doWork(new Work() {
                public void execute(Connection connection) throws SQLException {
                    for (String sql : sqlStatements) {
                        PreparedStatement statement = null;
                        try {
                            log.debug("Executing SQL:\n" + sql);
                            statement = connection.prepareStatement(sql);
                            statement.executeUpdate();
                        }
                        catch (Exception e) {
                            throw new ConfigurationException("Unable to execute SQL statement:\n" + sql, e);
                        }
                        finally {
                            try {
                                statement.close();
                            }
                            catch (Exception e) { }
                        }
                    }
                }
            });
        }
        catch (Exception e) {
            throw new ConfigurationException("Unable to execute query", e);
        }
    }

    /**
     * @return the handler with the given key
     */
    protected ConfigurationHandler getHandler(String key) {
        if (handlerMap == null) {
            handlerMap = new HashMap<String, ConfigurationHandler>();
            for (ConfigurationHandler h : Context.getRegisteredComponents(ConfigurationHandler.class)) {
                handlerMap.put(h.getName(), h);
            }
        }
        return handlerMap.get(key);
    }

    public void setSessionFactory(DbSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
