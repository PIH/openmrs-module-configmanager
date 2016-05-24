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

package org.openmrs.module.configmanager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.configmanager.schema.ConfigurationList;
import org.openmrs.module.configmanager.service.ConfigManagerService;

import java.io.File;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class ConfigManagerActivator extends BaseModuleActivator {

	protected static final Log log = LogFactory.getLog(ConfigManagerActivator.class);

	/**
	 * @see BaseModuleActivator#started()
	 */
	public void started() {
		log.info("Config Manager module started");
        File configurationFile = ConfigUtil.getConfigurationFile("configurations.xml");
        ConfigurationList configurationList = ConfigurationList.readFromFile(configurationFile);
        Context.getService(ConfigManagerService.class).runConfigurations(configurationList);
    }

	/**
	 * @see BaseModuleActivator#stopped()
	 */
	public void stopped() {
		log.info("Config Manager module stopped");
	}
}
