package org.openmrs.module.configmanager.configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the main configuration file to load, which simply references other configuration files that contain the actual configuration lists
 */
public class Config {

    private List<ConfigurationFile> files;

    public Config() {}

    public List<ConfigurationFile> getFiles() {
        if (files == null) {
            files = new ArrayList<ConfigurationFile>();
        }
        return files;
    }

    public void setFiles(List<ConfigurationFile> files) {
        this.files = files;
    }

    public void addFile(ConfigurationFile file) {
        getFiles().add(file);
    }
}
