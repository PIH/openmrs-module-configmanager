package org.openmrs.module.configmanager.handler;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.configmanager.csv.CsvParser;
import org.openmrs.module.configmanager.csv.CsvRow;
import org.openmrs.module.configmanager.schema.ConfigParameter;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configures a row of Location information
 */
@Component("configmanager-locationhandler")
public class LocationHandler extends BaseConfigurationHandler {

    /**
     * @return a unique name used to identify this handler in configuration
     */
    public String getName() {
        return "location-handler";
    }

    /**
     * Handles a single configuration item that is passed into it in order to process it
     */
    public void handle(File configFile, List<ConfigParameter> parameters) {

        // Retrieve all of the locations in the system and cache them
        Map<String, Location> locationMap = new HashMap<String, Location>();
        for (Location l : Context.getLocationService().getAllLocations(true)) {
            locationMap.put(l.getUuid(), l);
        }

        CsvParser parser = CsvParser.open(configFile);
        for (CsvRow row = parser.readNext(); row != null; row = parser.readNext()) {
            String uuid = row.getStringValue("uuid");
            String name = row.getStringValue("name");
            String description = row.getStringValue("description");
            Location l = locationMap.get(uuid);
            if (l == null) {
                l = new Location();
                l.setUuid(uuid);
                l.setName(name);
                l.setDescription(description);
                Context.getLocationService().saveLocation(l);
            }
            else {
                boolean updateRequired = false;
                if (!OpenmrsUtil.nullSafeEquals(name, l.getName())) {
                    l.setName(name);
                    updateRequired = true;
                }
                if (!OpenmrsUtil.nullSafeEquals(description, l.getDescription())) {
                    l.setDescription(description);
                    updateRequired = true;
                }
                if (updateRequired) {
                    Context.getLocationService().saveLocation(l);
                }
            }
        }
    }
}
