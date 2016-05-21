package org.openmrs.module.configmanager.schema;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.util.OpenmrsClassLoader;

public class ConfigurationListTest {

	protected final Log log = LogFactory.getLog(getClass());

	public static final String CONFIG_RESOURCE = "org/openmrs/module/configmanager/configurations.xml";

	@Test
	public void should_writeToString() throws Exception {
		ConfigurationList config = getConfigurationList();
		String expected = IOUtils.toString(OpenmrsClassLoader.getInstance().getResourceAsStream(CONFIG_RESOURCE), "UTF-8");
		String actual = ConfigurationList.writeToString(config);
		Assert.assertEquals(StringUtils.deleteWhitespace(expected), StringUtils.deleteWhitespace(actual));
	}

	@Test
	public void should_readFromString() throws Exception {
        ConfigurationList start = getConfigurationList();
		String serialized = IOUtils.toString(OpenmrsClassLoader.getInstance().getResourceAsStream(CONFIG_RESOURCE), "UTF-8");
        ConfigurationList end = ConfigurationList.readFromString(serialized);
		Assert.assertEquals(start, end);
	}

	protected ConfigurationList getConfigurationList() {
        ConfigurationList configurations = new ConfigurationList();

        ConfigElement levelConfig = new ConfigElement();
        levelConfig.setFile("addresshierarchy/address-levels.csv");
        levelConfig.setHandler("address-level-handler");
        configurations.getConfigElements().add(levelConfig);

        ConfigElement hierarchyConfig = new ConfigElement();
        hierarchyConfig.setFile("addresshierarchy/address-entries.csv");
        hierarchyConfig.setHandler("address-hierarchy-handler");
        hierarchyConfig.addParameter("entrySeparator", "\\|");
        hierarchyConfig.addParameter("identifierSeparator", "\\^");
        configurations.getConfigElements().add(hierarchyConfig);

		return configurations;
	}
}
