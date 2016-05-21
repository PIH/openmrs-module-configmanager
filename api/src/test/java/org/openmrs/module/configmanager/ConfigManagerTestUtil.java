package org.openmrs.module.configmanager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.openmrs.module.configmanager.schema.ConfigurationList;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class ConfigManagerTestUtil extends BaseModuleContextSensitiveTest {

    protected final Log log = LogFactory.getLog(getClass());

    public static void setupConfiguration(ConfigurationList configurations) throws Exception {
        String c = ConfigurationList.writeToString(configurations);
        FileUtils.writeStringToFile(ConfigUtil.getConfigurationFile("configurations.xml"), c);
    }

    public static void copyResourceToConfigurationFile(String fromPath, String toPath) throws Exception {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = OpenmrsClassLoader.getInstance().getResourceAsStream("org/openmrs/module/configmanager/" + fromPath);
            out = new FileOutputStream(ConfigUtil.getConfigurationFile(toPath));
            IOUtils.copy(in, out);
        }
        finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
        }
	}

    public static boolean stringMatchesResource(String str, String path)  throws Exception {
        String actual = IOUtils.toString(OpenmrsClassLoader.getInstance().getResourceAsStream("org/openmrs/module/configmanager/" + path), "UTF-8");
        return OpenmrsUtil.nullSafeEquals(str, actual);
    }

    public static boolean stringMatchesConfigurationFile(String str, String path)  throws Exception {
        String actual = FileUtils.readFileToString(ConfigUtil.getConfigurationFile(path), "UTF-8");
        return OpenmrsUtil.nullSafeEquals(str, actual);
    }

    public static void mapContains(Map<Object, Object> toCheck, Object...keysAndValues)  throws Exception {
        Assert.assertEquals(keysAndValues.length/2, toCheck.size());
        for (int i=0; i<keysAndValues.length; i+=2) {
            Object key = keysAndValues[i];
            Object value = keysAndValues[i+1];
            Assert.assertEquals(value, toCheck.get(key));
        }
    }
}
