package org.openmrs.module.configmanager.handler;

import org.openmrs.module.configmanager.schema.ConfigParameter;

import java.io.File;
import java.util.List;

/**
 * Represents the contract for a class that can handle a particular set of configurations
 * Each handler is registered with a name that is used to choose the handler of interest in the configuration
 */
public interface ConfigurationHandler {

    /**
     * @return a unique name used to identify this handler in configuration
     */
    String getName();

    /**
     * Handles a particular configuration file format, which can be varied with the given parameter values
     */
    void handle(File configFile, List<ConfigParameter> parameters);

    /**
     * @return the checksum for the configuration, to determine if the configuration has changed
     */
    String computeChecksum(File configFile, List<ConfigParameter> parameters);
}
