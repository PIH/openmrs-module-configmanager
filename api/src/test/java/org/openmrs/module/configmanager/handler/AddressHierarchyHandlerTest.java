package org.openmrs.module.configmanager.handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.layout.web.address.AddressSupport;
import org.openmrs.layout.web.address.AddressTemplate;
import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.addresshierarchy.AddressHierarchyEntry;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.configmanager.ConfigManagerTestUtil;
import org.openmrs.module.configmanager.ConfigUtil;
import org.openmrs.module.configmanager.schema.ConfigElement;
import org.openmrs.module.configmanager.schema.ConfigurationList;
import org.openmrs.module.configmanager.service.ConfigManagerService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.List;

import static org.openmrs.module.addresshierarchy.AddressHierarchyConstants.GLOBAL_PROP_INITIALIZE_ADDRESS_HIERARCHY_CACHE_ON_STARTUP;

public class AddressHierarchyHandlerTest extends BaseModuleContextSensitiveTest {

    @Before
    public void setupConfigurationFiles() throws Exception {
        ConfigManagerTestUtil.setupConfiguration(getConfigurationList());
        ConfigManagerTestUtil.copyResourceToConfigurationFile("handler/address-hierarchy-configuration.xml", "address-hierarchy-configuration.xml");
        ConfigManagerTestUtil.copyResourceToConfigurationFile("handler/address-hierarchy-entries.csv", "address-hierarchy-entries.csv");
        ConfigUtil.updateGlobalProperty(GLOBAL_PROP_INITIALIZE_ADDRESS_HIERARCHY_CACHE_ON_STARTUP, "true");
    }

    @Test
    public void should_configureAddressHierarchy() throws Exception {
        ConfigurationList l = getConfigurationList();

        Context.getService(ConfigManagerService.class).runConfigurations(l);

        // Validate address template
        AddressSupport as = AddressSupport.getInstance();
        AddressTemplate template = as.getDefaultLayoutTemplate();
        Assert.assertEquals("Country", template.getNameMappings().get("country"));
        Assert.assertEquals("Province/Area", template.getNameMappings().get("stateProvince"));
        Assert.assertEquals("District", template.getNameMappings().get("countyDistrict"));
        Assert.assertEquals("Chiefdom", template.getNameMappings().get("cityVillage"));
        Assert.assertEquals("Address", template.getNameMappings().get("address1"));

        Assert.assertEquals("20", template.getSizeMappings().get("country"));
        Assert.assertEquals("30", template.getSizeMappings().get("stateProvince"));
        Assert.assertEquals("40", template.getSizeMappings().get("countyDistrict"));
        Assert.assertEquals("50", template.getSizeMappings().get("cityVillage"));
        Assert.assertEquals("80", template.getSizeMappings().get("address1"));

        Assert.assertEquals(1, template.getElementDefaults().size());
        Assert.assertEquals("Sierra Leone", template.getElementDefaults().get("country"));

        Assert.assertEquals(4, template.getLineByLineFormat().size());
        Assert.assertEquals("address1", template.getLineByLineFormat().get(0));
        Assert.assertEquals("cityVillage", template.getLineByLineFormat().get(1));
        Assert.assertEquals("countyDistrict, stateProvince", template.getLineByLineFormat().get(2));
        Assert.assertEquals("country", template.getLineByLineFormat().get(3));

        // Validate address levels
        List<AddressHierarchyLevel> levels = getService().getAddressHierarchyLevels();
        Assert.assertEquals(5, levels.size());
        validateLevel(levels.get(0), AddressField.COUNTRY, null, true);
        validateLevel(levels.get(1), AddressField.STATE_PROVINCE, AddressField.COUNTRY, true);
        validateLevel(levels.get(2), AddressField.COUNTY_DISTRICT, AddressField.STATE_PROVINCE, false);
        validateLevel(levels.get(3), AddressField.CITY_VILLAGE, AddressField.COUNTY_DISTRICT, false);
        validateLevel(levels.get(4), AddressField.ADDRESS_1, AddressField.CITY_VILLAGE, false);

        // Validate address entries
        Assert.assertEquals(7, getService().getAddressHierarchyEntryCount().intValue());
        validateEntries(levels.get(0), "USA");
        validateEntries(levels.get(1), "MA");
        validateEntries(levels.get(2), "Suffolk", "Plymouth");
        validateEntries(levels.get(3), "Boston", "Scituate", "Duxbury");
    }

    protected void validateLevel(AddressHierarchyLevel level, AddressField field, AddressField parent, boolean required) {
        Assert.assertEquals(field, level.getAddressField());
        if (parent == null) {
            Assert.assertNull(level.getParent());
        }
        else {
            Assert.assertEquals(parent, level.getParent().getAddressField());
        }
        Assert.assertEquals(required, level.getRequired());
    }

    protected void validateEntries(AddressHierarchyLevel level, String... names) {
        List<AddressHierarchyEntry> entries = getService().getAddressHierarchyEntriesByLevel(level);
        Assert.assertEquals(names.length, entries.size());
        for (int i=0; i<entries.size(); i++) {
            Assert.assertEquals(names[i], entries.get(i).getName());
        }
    }

	protected ConfigurationList getConfigurationList() throws Exception {
        ConfigurationList configurations = new ConfigurationList();
        ConfigElement levelConfig = new ConfigElement();
        levelConfig.setFile("address-hierarchy-configuration.xml");
        levelConfig.setHandler("address-hierarchy-handler");
        configurations.getConfigElements().add(levelConfig);
		return configurations;
	}

    public static AddressHierarchyService getService() {
        return Context.getService(AddressHierarchyService.class);
    }
}
