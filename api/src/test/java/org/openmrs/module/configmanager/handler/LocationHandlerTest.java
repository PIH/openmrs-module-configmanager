package org.openmrs.module.configmanager.handler;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.configmanager.ConfigUtil;
import org.openmrs.module.configmanager.schema.ConfigElement;
import org.openmrs.module.configmanager.schema.ConfigurationList;
import org.openmrs.module.configmanager.service.ConfigManagerService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class LocationHandlerTest extends BaseModuleContextSensitiveTest {

    protected final Log log = LogFactory.getLog(getClass());

    @Test
    public void should_configureLocations() throws Exception {
        int numStart = Context.getLocationService().getAllLocations().size();
        Context.getService(ConfigManagerService.class).runConfigurations(getConfigurationList());
        int numEnd = Context.getLocationService().getAllLocations().size();
        Assert.assertEquals(2, numEnd - numStart);
        for (int i = 1; i < getLocationsToConfigure().size(); i++) {
            String[] locationConfig = getLocationsToConfigure().get(i);
            Location l = Context.getLocationService().getLocationByUuid(locationConfig[0]);
            Assert.assertEquals(locationConfig[1], l.getName());
            Assert.assertEquals(locationConfig[2], l.getDescription());
        }
    }

    protected List<String[]> getLocationsToConfigure() {
        List<String[]> locations = new ArrayList<String[]>();
        locations.add(new String[] {"uuid","name","description"});
        locations.add(new String[] {"88ce74ab-e0f9-11e5-be03-e82aea237700","Boston","A city in MA"});
        locations.add(new String[] {"88ce74ab-e0f9-11e5-be03-e82aea237710","Indianapolis","A city in IN"});
        return locations;
    }

	protected ConfigurationList getConfigurationList() throws Exception {
        ConfigurationList configurations = new ConfigurationList();

        ConfigElement levelConfig = new ConfigElement();
        levelConfig.setFile("locations.csv");
        levelConfig.setHandler("location-handler");
        configurations.getConfigElements().add(levelConfig);

        String c = ConfigurationList.writeToString(configurations);
        FileUtils.writeStringToFile(ConfigUtil.getConfigurationFile("configurations.xml"), c);

        CSVWriter writer = new CSVWriter(new FileWriter(ConfigUtil.getConfigurationFile("locations.csv")));
        writer.writeAll(getLocationsToConfigure());
        writer.close();

		return configurations;
	}
}
