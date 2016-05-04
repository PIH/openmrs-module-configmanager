package org.openmrs.module.configmanager.manager;

import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.layout.web.address.AddressTemplate;
import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.configmanager.change.AddressHierarchyLevelChange;
import org.openmrs.module.configmanager.change.Change;
import org.openmrs.module.configmanager.change.GlobalPropertyChange;
import org.openmrs.module.configmanager.configuration.AddressComponent;
import org.openmrs.module.configmanager.configuration.AddressConfiguration;
import org.openmrs.module.configmanager.configuration.Configuration;
import org.openmrs.module.configmanager.util.SqlRunner;
import org.openmrs.util.OpenmrsConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsible for loading the address configuration appropriately
 */
@Handler(supports={AddressConfiguration.class})
public class AddressConfigurationManager implements ConfigurationManager {

    @Override
    public List<Change> getExpected(Configuration configuration) {
        List<Change> ret = new ArrayList<Change>();
        AddressConfiguration config = (AddressConfiguration) configuration;
        ret.addAll(getExpectedAddressTemplateChanges(config.getAddressTemplate()));
        ret.addAll(getExpectedAddressHierarchyLevels(config.getAddressComponents()));

        return ret;
    }

    @Override
    public List<Change> getActual() {
        List<Change> ret = new ArrayList<Change>();
        ret.addAll(getActualAddressTemplateChanges());
        ret.addAll(getActualAddressHierarchyLevels());

        return ret;
    }

    /**
     * Gets the expected changes for installing the address template
     */
    protected List<Change> getExpectedAddressTemplateChanges(AddressTemplate template) {
        List<Change> ret = new ArrayList<Change>();
        try {
            String xml = Context.getSerializationService().getDefaultSerializer().serialize(template);
            ret.add(new GlobalPropertyChange(OpenmrsConstants.GLOBAL_PROPERTY_ADDRESS_TEMPLATE, xml));
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Unable to serialize address template", e);
        }
        return ret;
    }

    /**
     * Gets the actual changes installed for the address template
     */
    protected List<Change> getActualAddressTemplateChanges() {
        List<Change> ret = new ArrayList<Change>();
        String query = "select property_value from global_property where property = ?";
        String xml = SqlRunner.querySingleValue(query, String.class, OpenmrsConstants.GLOBAL_PROPERTY_ADDRESS_TEMPLATE);
        if (xml != null) {
            ret.add(new GlobalPropertyChange(OpenmrsConstants.GLOBAL_PROPERTY_ADDRESS_TEMPLATE, xml));
        }
        return ret;
    }

    /**
     * Gets the changes for installing address levels
     */
    protected List<Change> getExpectedAddressHierarchyLevels(List<AddressComponent> addressComponents) {
        List<Change> ret = new ArrayList<Change>();
        AddressField parent = null;
        for (AddressComponent c : addressComponents) {
            ret.add(new AddressHierarchyLevelChange(c.getField(), c.isRequiredInHierarchy(), parent));
            parent = c.getField();
        }
        return ret;
    }

    /**
     * Gets the actual changes installed for the address template
     */
    protected List<Change> getActualAddressHierarchyLevels() {
        List<Change> ret = new ArrayList<Change>();
        List<Object[]> existingLevels = SqlRunner.query("select address_hierarchy_level_id, parent_level_id, address_field, required from address_hierarchy_level", new ArrayListHandler());
        Map<Integer, String> keysToFields = new HashMap<Integer, String>();
        for (Object[] row : existingLevels) {
            keysToFields.put((Integer)row[0], (String)row[2]);
        }
        for (Object[] row : existingLevels) {
            AddressHierarchyLevelChange change = new AddressHierarchyLevelChange();
            String parentLevel = keysToFields.get((Integer)row[1]);
            if (parentLevel != null) {
                change.setParent(AddressField.valueOf(parentLevel));
            }
            change.setField(AddressField.valueOf((String)row[2]));
            change.setRequiredInHierarchy((Boolean)row[3]);
            ret.add(change);
        }
        return ret;
    }
}
