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

package org.openmrs.module.configmanager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.configmanager.schema.ConfigurationList;
import org.openmrs.module.configmanager.service.ConfigManagerService;
import org.openmrs.util.OpenmrsConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility methods for configuration manager
 */
public class ConfigUtil {

    protected static final Log log = LogFactory.getLog(ConfigUtil.class);

    /**
     * Run all of the configured configurations defined within the configurations.xml file
     */
    public static void runConfigurations() {
        File configurationFile = ConfigUtil.getConfigurationFile("configurations.xml");
        ConfigurationList configurationList = ConfigurationList.readFromFile(configurationFile);
        Context.getService(ConfigManagerService.class).runConfigurations(configurationList);
    }

    /**
     * @return the configuration directory
     */
    public static File getConfigurationDirectory() {
        return new File(OpenmrsConstants.APPLICATION_DATA_DIRECTORY, "configuration");
    }

    /**
     * @return a file within the configuration directory at the specified path
     */
    public static File getConfigurationFile(String path) {
        return new File(getConfigurationDirectory(), path);
    }

    public static <T> T parse(String val, Class<T> type) {
        if (Date.class.isAssignableFrom(type)) {
            return (T)parseDate(val);
        }
        else if (Integer.class.isAssignableFrom(type)) {
            return (T)parseInteger(val);
        }
        else if (String.class == type) {
            return (T)val;
        }
        else {
            throw new ConfigurationException("No conversion defined for value: " + val + " to type " + type);
        }
    }

    /**
     * @return the data with the given key as a Date
     */
    public static Date parseDate(String val) {
        Date ret = null;
        if (StringUtils.isNotBlank(val)) {
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                ret = df.parse(val);
            }
            catch (Exception e) {
                throw new ConfigurationException("Invalid date: " + val);
            }
        }
        return ret;
    }

    public static Integer parseInteger(String val) {
        Integer ret = null;
        if (StringUtils.isNotBlank(val)) {
            try {
                ret = Integer.valueOf(val);
            }
            catch (Exception e) {
                throw new ConfigurationException("Invalid Integer: " + val);
            }
        }
        return ret;
    }

    public static Map<String, String> getMap(String key, Map<String, String> vals) {
        Map<String, String> ret = new LinkedHashMap<String, String>();
        if (vals != null) {
            for (String s : vals.keySet()) {
                if (s.startsWith(key + ".")) {
                    String[] split = s.split("\\.");
                    ret.put(split[1], vals.get(s));
                }
            }
        }
        return ret;
    }

    /**
     * Constructs a map out of the passed elements.  Each odd element is expected to be a String key,
     * and each even element is expected to be an Object value
     */
    public static Map<String, Object> toMap(Object... elements) {
        Map<String, Object> ret = new HashMap<String, Object>();
        for (int i=0; i<elements.length; i+=2) {
            ret.put((String)elements[i], elements[i+1]);
        }
        return ret;
    }

    /**
     * Return a String in which the first occurrence of toReplace was replaced with replaceWith
     */
    public static String replaceFirst(String inputString, String toReplace, String replaceWith) {
        return StringUtils.replaceOnce(inputString, toReplace, replaceWith);
    }

    /**
     * Update the global property with the given name to the given value, creating it if it doesn't exist
     */
    public static void updateGlobalProperty(String propertyName, String propertyValue) {
        AdministrationService administrationService = Context.getAdministrationService();
        GlobalProperty gp = administrationService.getGlobalPropertyObject(propertyName);
        if (gp == null) {
            gp = new GlobalProperty(propertyName);
        }
        gp.setPropertyValue(propertyValue);
        administrationService.saveGlobalProperty(gp);
    }

    /**
     * @return the checksum of the given file
     */
    public static String computeChecksum(File file) {
        try {
            long checksum = FileUtils.checksumCRC32(file);
            return Long.toHexString(checksum);
        }
        catch (Exception e) {
            log.warn("Error computing checksum of " + file, e);
        }
        return "";
    }

    /**
     * Parses a SQL script from a file into multiple statements
     */
    public static String[] parseScriptIntoStatements(BufferedReader reader) throws IOException {
        List<String> ret = new ArrayList<String>();
        String delimiter = ";";
        StringBuilder statement = new StringBuilder();
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            if (line != null) {
                line = line.trim();
                if (!(org.apache.commons.lang3.StringUtils.isBlank(line) || line.startsWith("--"))) { // Ignore comments and blank lines
                    if (line.toUpperCase().startsWith("DELIMITER")) {
                        delimiter = line.substring(9).trim();
                    }
                    else {
                        if (line.endsWith(delimiter)) {
                            line = line.substring(0, line.lastIndexOf(delimiter));
                            statement.append(line);
                            ret.add(statement.toString());
                            statement = new StringBuilder();
                        }
                        else {
                            statement.append(line).append("\n");
                        }
                    }

                }
            }
        }
        return ret.toArray(new String[ret.size()]);
    }
}
