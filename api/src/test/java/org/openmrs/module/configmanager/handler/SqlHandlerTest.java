package org.openmrs.module.configmanager.handler;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.configmanager.ConfigManagerTestUtil;
import org.openmrs.module.configmanager.ConfigUtil;
import org.openmrs.module.configmanager.schema.ConfigElement;
import org.openmrs.module.configmanager.schema.ConfigurationList;
import org.openmrs.module.configmanager.service.ConfigManagerService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class SqlHandlerTest extends BaseModuleContextSensitiveTest {

    protected final Log log = LogFactory.getLog(getClass());

    @Test
    public void should_handleSqlFiles() throws Exception {
        int startNum = Context.getEncounterService().getAllEncounterTypes().size();
        setupSqlConfiguration("handler/sql-configuration.sql");
        int endNum = Context.getEncounterService().getAllEncounterTypes().size();
        Assert.assertEquals(2, endNum-startNum);
        EncounterType et1 = Context.getEncounterService().getEncounterTypeByUuid("3cb2f1df-2035-11e6-8979-e82aea237783");
        Assert.assertNotNull(et1);
        Assert.assertEquals("ET1", et1.getName());
        Assert.assertEquals("Description 1", et1.getDescription());
        EncounterType et2 = Context.getEncounterService().getEncounterTypeByUuid("3cb2f1df-2035-11e6-8979-e82aea237784");
        Assert.assertNotNull(et2);
        Assert.assertEquals("ET2", et2.getName());
        Assert.assertEquals("Description 2", et2.getDescription());
    }

	protected void setupSqlConfiguration(String resourceName) throws Exception {
        ConfigurationList configurations = new ConfigurationList();

        ConfigElement levelConfig = new ConfigElement();
        levelConfig.setFile(resourceName);
        levelConfig.setHandler("sql-handler");
        configurations.getConfigElements().add(levelConfig);

        String c = ConfigurationList.writeToString(configurations);
        FileUtils.writeStringToFile(ConfigUtil.getConfigurationFile("configurations.xml"), c);

        ConfigManagerTestUtil.copyResourceToConfigurationFile(resourceName, resourceName);
        Context.getService(ConfigManagerService.class).runConfigurations(configurations);
	}
}
