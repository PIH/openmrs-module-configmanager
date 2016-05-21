package org.openmrs.module.configmanager.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.configmanager.ConfigUtil;
import org.openmrs.module.configmanager.schema.ConfigParameter;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents the contract for a class that can handle a particular set of configurations
 * Each handler is registered with a name that is used to choose the handler of interest in the configuration
 */
public abstract class BaseConfigurationHandler implements ConfigurationHandler {

    protected final Log log = LogFactory.getLog(getClass());

    /**
     * @return default implementation of checksum
     */
    @Override
    public String computeChecksum(File configFile, List<ConfigParameter> parameters) {
        String configFileChecksum = ConfigUtil.computeChecksum(configFile);
        Map<String, String> vals = new TreeMap<String, String>();
        if (parameters != null) {
            for (ConfigParameter p : parameters) {
                vals.put(p.getName(), p.getValue());
            }
        }
        return configFileChecksum + "|" + vals.toString();
    }
}
