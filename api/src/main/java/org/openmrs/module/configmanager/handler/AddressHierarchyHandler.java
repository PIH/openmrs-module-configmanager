package org.openmrs.module.configmanager.handler;

import org.openmrs.module.configmanager.addresshierarchy.AddressConfiguration;
import org.openmrs.module.configmanager.addresshierarchy.AddressHierarchyUtil;
import org.openmrs.module.configmanager.schema.ConfigParameter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * Responsible for loading the address configuration appropriately
 */
@Component("configmanager-addresshierarchyhandler")
public class AddressHierarchyHandler extends BaseConfigurationHandler {

    /**
     * @return a unique name used to identify this handler in configuration
     */
    public String getName() {
        return "address-hierarchy-handler";
    }

    /**
     * Configures the address template, levels, and hierarchy
     */
    public void handle(File configFile, List<ConfigParameter> parameters) {
        AddressConfiguration addressConfiguration = AddressHierarchyUtil.readAddressConfigurationFromFile(configFile);
        AddressHierarchyUtil.deleteExistingAddressHierarchyEntriesAndLevels();
        AddressHierarchyUtil.installAddressTemplate(addressConfiguration.getAddressTemplate());
        AddressHierarchyUtil.installAddressHierarchyLevels(addressConfiguration.getAddressComponents());
        AddressHierarchyUtil.installAddressHierarchyEntries(addressConfiguration.getAddressHierarchyFile());
    }
}
