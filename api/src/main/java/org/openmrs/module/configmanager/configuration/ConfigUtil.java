package org.openmrs.module.configmanager.configuration;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import liquibase.util.MD5Util;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.util.OpenmrsConstants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility methods supporting configuration
 */
public class ConfigUtil {

    protected static Log log = LogFactory.getLog(ConfigUtil.class);

    /**
     * @return the full ConfigurationList defined in the main configuration file
     */
    public static List<ConfigurationFile> readConfiguration() {
        List<ConfigurationFile> l = new ArrayList<ConfigurationFile>();
        File config = getConfigFile("config.xml");
        Config m = readConfigurationFromFile(config, Config.class);
        for (ConfigurationFile f : m.getFiles()) {
            if (f.getConfigurations().size() > 0) {
                throw new IllegalArgumentException("config.xml should only contain paths to configuration files, please check your format");
            }
            File configFileAtPath = getConfigFile(f.getPath());
            ConfigurationFile configurationFile = readConfigurationFromFile(configFileAtPath, ConfigurationFile.class);
            configurationFile.setPath(f.getPath());
            l.add(configurationFile);
        }
        return l;
    }

    /**
     * Reads configuration from a file
     */
    public static <T> T readConfigurationFromFile(File file, Class<T> type) {
        try {
            String configuration = FileUtils.readFileToString(file, "UTF-8");
            return readConfigurationFromString(configuration, type);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Unable to load " + type.getSimpleName() + " from file.  Please check that this file is accessible.", e);
        }
    }

    /**
     * Reads configuration from a String
     */
    public static <T> T readConfigurationFromString(String configuration, Class<T> type) {
        try {
            return (T) getSerializer().fromXML(configuration);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Unable to load " + type.getSimpleName() + " from configuration.  Please check the format", e);
        }
    }

    /**
     * Writes a serialized String representing the configuration
     */
    public static String writeConfigurationToString(Object configuration) {
        return getSerializer().toXML(configuration);
    }

    /**
     * @return the serializer instance used to load configuration from file
     */
    public static XStream getSerializer() {
        XStream xs = new XStream(new DomDriver());

        xs.alias("config", Config.class);
        xs.alias("configFile", ConfigurationFile.class);
        xs.alias("addressConfiguration", AddressConfiguration.class);
        xs.alias("addressComponent", AddressComponent.class);
        xs.alias("addressHierarchyFile", AddressHierarchyFile.class);

        xs.useAttributeFor(ConfigurationFile.class, "path");

        xs.addImplicitCollection(ConfigurationFile.class, "configurations");
        xs.addImplicitCollection(Config.class, "files");

        return xs;
    }

    /**
     * @return the configuration file with the passed name
     */
    public static File getConfigFile(String name) {
        StringBuilder path = new StringBuilder();
        path.append(OpenmrsConstants.APPLICATION_DATA_DIRECTORY).append(File.separator);
        path.append("configmanager").append(File.separator).append(name);
        return new File(path.toString());
    }

    /**
     * @return the checksum of the given content
     */
    public static String computeChecksum(String content) {
        return MD5Util.computeMD5(content);
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
     * Copies the classpath resource at the specified location to the specified file
     */
    public static void copyResourceToFile(String resource, File file) throws IOException {
        String contents = IOUtils.toString(OpenmrsClassLoader.getInstance().getResourceAsStream(resource), "UTF-8");
        FileUtils.writeStringToFile(file, contents, "UTF-8");
    }
}
