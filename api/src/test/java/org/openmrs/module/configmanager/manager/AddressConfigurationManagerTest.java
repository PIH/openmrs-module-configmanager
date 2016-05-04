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
package org.openmrs.module.configmanager.manager;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.configmanager.change.AddressHierarchyLevelChange;
import org.openmrs.module.configmanager.change.Change;
import org.openmrs.module.configmanager.change.GlobalPropertyChange;
import org.openmrs.module.configmanager.configuration.AddressComponent;
import org.openmrs.module.configmanager.configuration.AddressConfiguration;
import org.openmrs.module.configmanager.configuration.AddressHierarchyFile;
import org.openmrs.module.configmanager.util.SqlRunner;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.List;

/**
 * Tests for the SqlRunner
 */
public class AddressConfigurationManagerTest extends BaseModuleContextSensitiveTest {

	@Test
	public void getColumns_shouldReturnTheConfiguredColumns() throws Exception {
        AddressConfigurationManager mgr = new AddressConfigurationManager();

        // Test nothing configured to start
        List<Change> actual = mgr.getActual();
        Assert.assertEquals(0, actual.size());

        // Execute the configurations
        List<Change> expected = mgr.getExpected(getAddressConfiguration());
        for (Change c : expected) {
            c.execute();
        }

        // Test that all changes were run and actual matches expected
        actual = mgr.getActual();
        Assert.assertEquals(6, actual.size());
        Assert.assertEquals(expected, actual);

        SqlRunner.update("update address_hierarchy_level set required = ? where address_field = ?", Boolean.FALSE, AddressField.COUNTRY);

        // Test that actual and expected now differ
        actual = mgr.getActual();
        Assert.assertEquals(expected.size(), actual.size());
        for (int i=0; i<actual.size(); i++) {
            Change a = actual.get(i);
            Change e = expected.get(i);
            if (a instanceof GlobalPropertyChange) {
                Assert.assertEquals(e, a);
            }
            else if (a instanceof AddressHierarchyLevelChange) {
                AddressHierarchyLevelChange ahlca = (AddressHierarchyLevelChange)a;
                if (ahlca.getField() == AddressField.COUNTRY) {
                    Assert.assertNotEquals(e, a);
                }
                else {
                    Assert.assertEquals(e, a);
                }
            }
        }

        Assert.assertNotEquals(expected, actual);
    }

    protected AddressConfiguration getAddressConfiguration() {
        AddressConfiguration configuration = new AddressConfiguration();
        configuration.addAddressComponent(new AddressComponent(AddressField.COUNTRY, "Country", 40, "Sierra Leone", true));
        configuration.addAddressComponent(new AddressComponent(AddressField.STATE_PROVINCE, "Province/Area", 40, null, true));
        configuration.addAddressComponent(new AddressComponent(AddressField.COUNTY_DISTRICT, "District", 40, null, false));
        configuration.addAddressComponent(new AddressComponent(AddressField.CITY_VILLAGE, "Chiefdom", 40, null, false));
        configuration.addAddressComponent(new AddressComponent(AddressField.ADDRESS_1, "Address", 80, null, false));
        configuration.addLineByLineFormat("address1");
        configuration.addLineByLineFormat("cityVillage");
        configuration.addLineByLineFormat("countyDistrict, stateProvince");
        configuration.addLineByLineFormat("country");
        AddressHierarchyFile file = new AddressHierarchyFile();
        file.setFilename("address-hierarchy-entries.csv");
        file.setEntryDelimiter("|");
        file.setIdentifierDelimiter("^");
        configuration.setAddressHierarchyFile(file);
        return configuration;
    }
}
