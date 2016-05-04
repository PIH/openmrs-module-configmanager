package org.openmrs.module.configmanager.configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a configuration file that contains a List of configurations to run
 */
public class ConfigurationFile {

    private String path;
    private List<Configuration> configurations;

    public ConfigurationFile() {}

    public ConfigurationFile(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<Configuration> getConfigurations() {
        if (configurations == null) {
            configurations = new ArrayList<Configuration>();
        }
        return configurations;
    }

    public void addConfiguration(Configuration configuration) {
        getConfigurations().add(configuration);
    }

    public void addConfigurations(List<Configuration> configurations) {
        if (configurations != null) {
            getConfigurations().addAll(configurations);
        }
    }

    public void setConfigurations(List<Configuration> configurations) {
        this.configurations = configurations;
    }
}
