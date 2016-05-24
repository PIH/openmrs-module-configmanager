package org.openmrs.module.configmanager.addresshierarchy;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.layout.web.address.AddressTemplate;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.addresshierarchy.util.AddressHierarchyImportUtil;
import org.openmrs.module.configmanager.ConfigUtil;
import org.openmrs.util.OpenmrsConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Utility method for executing against the Address Hierarchy API
 */
public class AddressHierarchyUtil {

    protected static final Log log = LogFactory.getLog(AddressHierarchyUtil.class);

    /**
     * Deletes all existing configurations
     */
    public static void deleteExistingAddressHierarchyEntriesAndLevels() {
        log.info("Deleting existing address hierarchy entries and levels");
        getAddressHierarchyService().deleteAllAddressHierarchyEntries();
        for (AddressHierarchyLevel l : getAddressHierarchyService().getAddressHierarchyLevels()) {
            getAddressHierarchyService().deleteAddressHierarchyLevel(l);
        }
    }

    /**
     * Installs the configured address template by updating the global property
     */
    public static void installAddressTemplate(AddressTemplate template) {
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
    public static void installAddressHierarchyLevels(List<AddressComponent> addressComponents) {
        log.info("Installing Address Hierarchy Levels");
        AddressHierarchyLevel lastLevel = null;
        for (AddressComponent component : addressComponents) {
            AddressHierarchyLevel level = new AddressHierarchyLevel();
            level.setAddressField(component.getField());
            level.setRequired(component.isRequiredInHierarchy());
            level.setParent(lastLevel);
            getAddressHierarchyService().saveAddressHierarchyLevel(level);
            lastLevel = level;
        }
    }

    /**
     * Install the address hierarchy entries as defined by the AddressHierarchyFile configuration
     */
    public static void installAddressHierarchyEntries(AddressHierarchyFile file) {
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
        getAddressHierarchyService().initializeFullAddressCache();
        log.info("Address Hierarchy Loading complete");
    }

    /**
     * Reads from a String representing the address configuration into an AddressConfiguration object
     */
    public static AddressConfiguration readAddressConfigurationFromFile(File file) {
        try {
            String configuration = FileUtils.readFileToString(file, "UTF-8");
            return readAddressConfigurationFromString(configuration);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Unable to load address configuration from configuration file.  Please check the format of this file", e);
        }
    }

    /**
     * Reads from a String representing the address configuration into an AddressConfiguration object
     */
    public static AddressConfiguration readAddressConfigurationFromString(String configuration) {
        try {
            return (AddressConfiguration) getAddressConfigurationSerializer().fromXML(configuration);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Unable to load address configuration from configuration file.  Please check the format of this file", e);
        }
    }

    /**
     * @return the serializer instance used to load configuration from file
     */
    public static XStream getAddressConfigurationSerializer() {
        XStream xs = new XStream(new DomDriver());
        xs.alias("addressConfiguration", AddressConfiguration.class);
        xs.alias("addressComponent", AddressComponent.class);
        xs.alias("addressHierarchyFile", AddressHierarchyFile.class);
        return xs;
    }

    public static AddressHierarchyService getAddressHierarchyService() {
        return Context.getService(AddressHierarchyService.class);
    }
}
