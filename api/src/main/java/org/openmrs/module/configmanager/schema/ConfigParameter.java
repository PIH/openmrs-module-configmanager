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

package org.openmrs.module.configmanager.schema;

import org.openmrs.util.OpenmrsUtil;

/**
 * Represents a single name/value parameter for a configuration element
 */
public class ConfigParameter {

    private String name;
    private String value;

    public ConfigParameter() {}

    public ConfigParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int ret = 17;
        ret = 31 * ret + getName().hashCode();
        ret = 31 * ret + getValue().hashCode();
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConfigParameter)) {
            return false;
        }
        ConfigParameter that = (ConfigParameter)obj;
        boolean ret = true;
        ret = ret && OpenmrsUtil.nullSafeEquals(this.getName(), that.getName());
        ret = ret && OpenmrsUtil.nullSafeEquals(this.getValue(), that.getValue());
        return ret;
    }

    @Override
    public String toString() {
        return name + " = " + value;
    }
}
