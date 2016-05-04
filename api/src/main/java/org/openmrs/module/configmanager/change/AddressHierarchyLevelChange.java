package org.openmrs.module.configmanager.change;

import org.openmrs.module.addresshierarchy.AddressField;
import org.openmrs.module.configmanager.configuration.ConfigUtil;
import org.openmrs.module.configmanager.util.SqlRunner;
import org.openmrs.util.OpenmrsUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a unit of change represented by a single statement
 */
public class AddressHierarchyLevelChange extends SqlChange {

    private AddressField field;
    private boolean requiredInHierarchy;
    private AddressField parent;

    public AddressHierarchyLevelChange() {}

    public AddressHierarchyLevelChange(AddressField field, boolean requiredInHierarchy, AddressField parent) {
        this.field = field;
        this.requiredInHierarchy = requiredInHierarchy;
        this.parent = parent;
    }

    @Override
    public String getTable() {
        return "address_hierarchy_level";
    }

    @Override
    public Map<String, Object> getColumnValues() {
        return ConfigUtil.toMap(
                "address_field", getField(),
                "required", isRequiredInHierarchy(),
                "parent_level_id", getParent()
        );
    }

    @Override
    public List<String> getIdentityColumns() {
        return Arrays.asList("address_field");
    }

    @Override
    protected Map<String, Object> getAdditionalValuesForInsert() {
        return ConfigUtil.toMap("uuid", UUID.randomUUID().toString());
    }

    @Override
    public Map<String, Object> getColumnValueConversions() {
        Map<String, Object> m = new HashMap<String, Object>();
        if (getParent() != null) {
            String parentQuery = "select address_hierarchy_level_id from address_hierarchy_level where address_field = ?";
            Number parentId = SqlRunner.querySingleValue(parentQuery, Number.class, getParent());
            m.put("parent_level_id", parentId);
        }
        return m;
     }

    @Override
    public int hashCode() {
        int ret = 17;
        ret = 31 * ret + (field == null ? 0 : field.hashCode());
        ret = 31 * ret + (requiredInHierarchy ? 0 : 1);
        ret = 31 * ret + (parent == null ? 0 : parent.hashCode());
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AddressHierarchyLevelChange)) {
            return false;
        }
        AddressHierarchyLevelChange that = (AddressHierarchyLevelChange)obj;
        boolean ret = true;
        ret = ret && OpenmrsUtil.nullSafeEquals(this.getField(), that.getField());
        ret = ret && OpenmrsUtil.nullSafeEquals(this.isRequiredInHierarchy(), that.isRequiredInHierarchy());
        ret = ret && OpenmrsUtil.nullSafeEquals(this.getParent(), that.getParent());
        return ret;
    }

    @Override
    public String toString() {
        return getField() + ", required = " + isRequiredInHierarchy() + ", parent = " + getParent();
    }

    public AddressField getField() {
        return field;
    }

    public void setField(AddressField field) {
        this.field = field;
    }

    public boolean isRequiredInHierarchy() {
        return requiredInHierarchy;
    }

    public void setRequiredInHierarchy(boolean requiredInHierarchy) {
        this.requiredInHierarchy = requiredInHierarchy;
    }

    public AddressField getParent() {
        return parent;
    }

    public void setParent(AddressField parent) {
        this.parent = parent;
    }
}
