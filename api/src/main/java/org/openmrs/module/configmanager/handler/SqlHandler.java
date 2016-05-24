package org.openmrs.module.configmanager.handler;

import org.openmrs.api.context.Context;
import org.openmrs.module.configmanager.ConfigUtil;
import org.openmrs.module.configmanager.ConfigurationException;
import org.openmrs.module.configmanager.schema.ConfigParameter;
import org.openmrs.module.configmanager.service.ConfigManagerService;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

/**
 * Configures a row of Location information
 */
@Component("configmanager-sqlhandler")
public class SqlHandler extends BaseConfigurationHandler {

    /**
     * @return a unique name used to identify this handler in configuration
     */
    public String getName() {
        return "sql-handler";
    }

    /**
     * Handles a single configuration item that is passed into it in order to process it
     */
    public void handle(File configFile, List<ConfigParameter> parameters) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(configFile));
            String[] statements = ConfigUtil.parseScriptIntoStatements(reader);
            Context.getService(ConfigManagerService.class).executeSql(statements);
        }
        catch (Exception e) {
            throw new ConfigurationException("Unable to execute SQL file at " + configFile, e);
        }
        finally {
            try {
                reader.close();
            }
            catch (Exception e) {}
        }
    }
}
