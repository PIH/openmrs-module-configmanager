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
package org.openmrs.module.configmanager.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.configmanager.configuration.AddressComponent;
import org.openmrs.module.configmanager.configuration.AddressConfiguration;
import org.openmrs.module.configmanager.configuration.Config;
import org.openmrs.module.configmanager.configuration.ConfigUtil;
import org.openmrs.module.configmanager.configuration.ConfigurationFile;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsClassLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Tests for the SqlRunner
 */
public class ConfigUtilTest extends BaseModuleContextSensitiveTest {

    @Before
    public void setupConfigFiles() throws Exception {
        Config m = new Config();
        m.addFile(new ConfigurationFile("addressConfiguration.xml"));

        File configManagerFile = ConfigUtil.getConfigFile("config.xml");
        FileUtils.writeStringToFile(configManagerFile, ConfigUtil.writeConfigurationToString(m), "UTF-8");

        ConfigurationFile addressConfigFile = new ConfigurationFile();
        addressConfigFile.addConfiguration(getAddressConfiguration());

        File addressConfigurationFile = ConfigUtil.getConfigFile("addressConfiguration.xml");
        FileUtils.writeStringToFile(addressConfigurationFile, ConfigUtil.writeConfigurationToString(addressConfigFile), "UTF-8");
    }

    @Test
    public void shouldLoadConfigurationFile() throws Exception {
        List<ConfigurationFile> configFiles = ConfigUtil.readConfiguration();
        Assert.assertEquals(1, configFiles.size());
        ConfigurationFile configFile = configFiles.get(0);
        Assert.assertEquals("addressConfiguration.xml", configFile.getPath());
        Assert.assertEquals(1, configFile.getConfigurations().size());
        Assert.assertEquals(AddressConfiguration.class, configFile.getConfigurations().get(0).getClass());
        AddressConfiguration ac = (AddressConfiguration) configFile.getConfigurations().get(0);
        Assert.assertEquals(getAddressConfiguration(), ac);
    }

    protected AddressConfiguration getAddressConfiguration() {
        AddressConfiguration addressConfig = new AddressConfiguration();
        addressConfig.addAddressComponent(new AddressComponent(AddressField.COUNTRY, "Country", 40, "Sierra Leone", true));
        addressConfig.addAddressComponent(new AddressComponent(AddressField.STATE_PROVINCE, "Province/Area", 40, null, true));
        addressConfig.addAddressComponent(new AddressComponent(AddressField.COUNTY_DISTRICT, "District", 40, null, false));
        addressConfig.addAddressComponent(new AddressComponent(AddressField.CITY_VILLAGE, "Chiefdom", 40, null, false));
        addressConfig.addAddressComponent(new AddressComponent(AddressField.ADDRESS_1, "Address", 80, null, false));
        addressConfig.addLineByLineFormat("address1");
        addressConfig.addLineByLineFormat("cityVillage");
        addressConfig.addLineByLineFormat("countyDistrict, stateProvince");
        addressConfig.addLineByLineFormat("country");
        return addressConfig;
    }


}
