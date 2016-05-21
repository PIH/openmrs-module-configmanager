package org.openmrs.module.configmanager.handler;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.layout.web.address.AddressTemplate;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.addresshierarchy.util.AddressHierarchyImportUtil;
import org.openmrs.module.configmanager.ConfigUtil;
import org.openmrs.module.configmanager.schema.AddressComponent;
import org.openmrs.module.configmanager.schema.AddressConfiguration;
import org.openmrs.module.configmanager.schema.AddressHierarchyFile;
import org.openmrs.module.configmanager.schema.ConfigParameter;
import org.openmrs.util.OpenmrsConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Responsible for loading the address configuration appropriately
 */
@Handler
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
        AddressConfiguration addressConfiguration = readFromFile(configFile);
        deleteExistingConfiguration();
        installAddressTemplate(addressConfiguration.getAddressTemplate());
        installAddressHierarchyLevels(addressConfiguration.getAddressComponents());
        installAddressHierarchyEntries(addressConfiguration.getAddressHierarchyFile());
    }

    /**
     * Deletes all existing configurations
     */
    public void deleteExistingConfiguration() {
        log.info("Deleting existing address hierarchy entries and levels");
        getService().deleteAllAddressHierarchyEntries();
        for (AddressHierarchyLevel l : getService().getAddressHierarchyLevels()) {
            getService().deleteAddressHierarchyLevel(l);
        }
    }

    /**
     * Installs the configured address template by updating the global property
     */
    public void installAddressTemplate(AddressTemplate template) {
        try {
            log.info("Installing Address Template");
            String xml = Context.getSerializationService().getDefaultSerializer().serialize(template);
            ConfigUtil.updateGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_ADDRESS_TEMPLATE, xml);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Unable to serialize and save address template", e);
        }
    }

    /**
     * Install the configured address hierarchy levels
     */
    public void installAddressHierarchyLevels(List<AddressComponent> addressComponents) {
        log.info("Installing Address Hierarchy Levels");
        AddressHierarchyLevel lastLevel = null;
        for (AddressComponent component : addressComponents) {
            AddressHierarchyLevel level = new AddressHierarchyLevel();
            level.setAddressField(component.getField());
            level.setRequired(component.isRequiredInHierarchy());
            level.setParent(lastLevel);
            getService().saveAddressHierarchyLevel(level);
            lastLevel = level;
        }
    }

    /**
     * Install the address hierarchy entries as defined by the AddressHierarchyFile configuration
     */
    public void installAddressHierarchyEntries(AddressHierarchyFile file) {
        log.info("Installing Address Hierarchy Entries");
        Context.getService(AddressHierarchyService.class).deleteAllAddressHierarchyEntries();
        InputStream is = null;
        try {
            is = new FileInputStream(ConfigUtil.getConfigurationFile(file.getFilename()));
            AddressHierarchyImportUtil.importAddressHierarchyFile(is, file.getEntryDelimiter(), file.getIdentifierDelimiter());
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Unable to import address hierarchy from file", e);
        }
        finally {
            IOUtils.closeQuietly(is);
        }
        log.info("Entries loaded, re-initializing address cache");
        getService().initializeFullAddressCache();
        log.info("Address Hierarchy Loading complete");
    }

    /**
     * Reads from a String representing the address configuration into an AddressConfiguration object
     */
    public static AddressConfiguration readFromFile(File file) {
        try {
            String configuration = FileUtils.readFileToString(file, "UTF-8");
            return readFromString(configuration);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Unable to load address configuration from configuration file.  Please check the format of this file", e);
        }
    }

    /**
     * Reads from a String representing the address configuration into an AddressConfiguration object
     */
    public static AddressConfiguration readFromString(String configuration) {
        try {
            return (AddressConfiguration) getSerializer().fromXML(configuration);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Unable to load address configuration from configuration file.  Please check the format of this file", e);
        }
    }

    /**
     * @return the serializer instance used to load configuration from file
     */
    public static XStream getSerializer() {
        XStream xs = new XStream(new DomDriver());
        xs.alias("addressConfiguration", AddressConfiguration.class);
        xs.alias("addressComponent", AddressComponent.class);
        xs.alias("addressHierarchyFile", AddressHierarchyFile.class);
        return xs;
    }

    public static AddressHierarchyService getService() {
        return Context.getService(AddressHierarchyService.class);
    }
}
