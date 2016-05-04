package org.openmrs.module.configmanager.change;

import org.openmrs.module.configmanager.configuration.ConfigUtil;
import org.openmrs.util.OpenmrsUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a unit of change represented by a single statement
 */
public class GlobalPropertyChange extends SqlChange {

    private String propertyName;
    private String propertyValue;

    public GlobalPropertyChange() {}

    public GlobalPropertyChange(String propertyName, String propertyValue) {
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    @Override
    public String getTable() {
        return "global_property";
    }

    @Override
    public Map<String, Object> getColumnValues() {
        return ConfigUtil.toMap("property", propertyName, "property_value", propertyValue);
    }

    @Override
    protected Map<String, Object> getAdditionalValuesForInsert() {
        return ConfigUtil.toMap("uuid", UUID.randomUUID().toString());
    }

    @Override
    public List<String> getIdentityColumns() {
        return Arrays.asList("property");
    }

    @Override
    public int hashCode() {
        int ret = 17;
        ret = 31 * ret + (propertyName == null ? 0 : propertyName.hashCode());
        ret = 31 * ret + (propertyValue == null ? 0 : propertyValue.hashCode());
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GlobalPropertyChange)) {
            return false;
        }
        GlobalPropertyChange that = (GlobalPropertyChange)obj;
        boolean ret = true;
        ret = ret && OpenmrsUtil.nullSafeEquals(this.getPropertyName(), that.getPropertyName());
        ret = ret && OpenmrsUtil.nullSafeEquals(this.getPropertyValue(), that.getPropertyValue());
        return ret;
    }

    @Override
    public String toString() {
        return propertyName + "=" + propertyValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }
}
