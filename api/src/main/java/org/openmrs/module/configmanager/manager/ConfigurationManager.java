package org.openmrs.module.configmanager.manager;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.layout.web.address.AddressTemplate;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.addresshierarchy.util.AddressHierarchyImportUtil;
import org.openmrs.module.configmanager.change.Change;
import org.openmrs.module.configmanager.configuration.AddressComponent;
import org.openmrs.module.configmanager.configuration.AddressConfiguration;
import org.openmrs.module.configmanager.configuration.AddressHierarchyFile;
import org.openmrs.module.configmanager.configuration.Configuration;
import org.openmrs.util.OpenmrsConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Represents the contract for a class that manages a particular type of configuration
 * If configuration is complete and no manual changes are made, then getExpected() and getActual() should match
 * The first time this is run, getActual() should be empty
 * If subsequent changes are made in either expected or actual state, these can be inspected and acted upon
 */
public interface ConfigurationManager {

    /**
     * @return the expected Changes produced by this configuration
     */
    List<Change> getExpected(Configuration configuration);

    /**
     * @return the actual state of the system related to this configuration
     */
    List<Change> getActual();

}
