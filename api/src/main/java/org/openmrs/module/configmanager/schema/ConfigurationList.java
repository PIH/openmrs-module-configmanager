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

package org.openmrs.module.configmanager.schema;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the overall set of Configurations to execute
 */
public class ConfigurationList {

    private List<ConfigElement> configElements;

    public List<ConfigElement> getConfigElements() {
        if (configElements == null) {
            configElements = new ArrayList<ConfigElement>();
        }
        return configElements;
    }

    public void setConfigElements(List<ConfigElement> configElements) {
        this.configElements = configElements;
    }

    //***** STATIC METHODS FOR LOADING/WRITING FROM XML

    /**
     * Reads from a String representing the address configuration into an AddressConfiguration object
     */
    public static ConfigurationList readFromFile(File file) {
        try {
            String configurations = FileUtils.readFileToString(file, "UTF-8");
            return readFromString(configurations);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Unable to load configurations from file.  Please check that it is available", e);
        }
    }

    /**
     * Reads from a String representing the address configuration into an AddressConfiguration object
     */
    public static ConfigurationList readFromString(String configurations) {
        try {
            return (ConfigurationList) getSerializer().fromXML(configurations);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Unable to load configurations from xml.  Please check the format.", e);
        }
    }

    /**
     * Writes a serialized String representing the address configuration from an AddressConfiguration object
     */
    public static String writeToString(ConfigurationList configurations) {
        return getSerializer().toXML(configurations);
    }

    /**
     * @return the serializer instance used to load configuration from file
     */
    public static XStream getSerializer() {
        XStream xs = new XStream(new DomDriver());
        xs.alias("configurations", ConfigurationList.class);
        xs.alias("config", ConfigElement.class);
        xs.alias("parameter", ConfigParameter.class);
        xs.addImplicitCollection(ConfigurationList.class, "configElements");
        xs.useAttributeFor(ConfigElement.class, "file");
        xs.useAttributeFor(ConfigElement.class, "handler");
        xs.addImplicitCollection(ConfigElement.class, "parameters");
        xs.useAttributeFor(ConfigParameter.class, "name");
        xs.useAttributeFor(ConfigParameter.class, "value");
        return xs;
    }

    // Overrides

    @Override
    public int hashCode() {
        int ret = 17;
        ret = 31 * ret + getConfigElements().hashCode();
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConfigurationList)) {
            return false;
        }
        ConfigurationList that = (ConfigurationList)obj;
        if (this.getConfigElements().size() != that.getConfigElements().size()) {
            return false;
        }
        for (int i=0; i<this.getConfigElements().size(); i++) {
            if (!this.getConfigElements().get(i).equals(that.getConfigElements().get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return getConfigElements().toString();
    }
}
