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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an independent configuration that should be executed
 */
public class ConfigElement {

    private String file;
    private String handler;
    private String tags;
    private List<ConfigParameter> parameters;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<ConfigParameter> getParameters() {
        if (parameters == null) {
            parameters = new ArrayList<ConfigParameter>();
        }
        return parameters;
    }

    public void setParameters(List<ConfigParameter> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(String name, String value) {
        getParameters().add(new ConfigParameter(name, value));
    }

    @Override
    public int hashCode() {
        int ret = 17;
        ret = 31 * ret + getFile().hashCode();
        ret = 31 * ret + getHandler().hashCode();
        ret = 31 * ret + getTags().hashCode();
        ret = 31 * ret + getParameters().hashCode();
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConfigElement)) {
            return false;
        }
        ConfigElement that = (ConfigElement)obj;
        boolean ret = true;
        ret = ret && OpenmrsUtil.nullSafeEquals(this.getFile(), that.getFile());
        ret = ret && OpenmrsUtil.nullSafeEquals(this.getHandler(), that.getHandler());
        ret = ret && OpenmrsUtil.nullSafeEquals(this.getTags(), that.getTags());
        if (ret) {
            if (this.getParameters().size() != that.getParameters().size()) {
                return false;
            }
            for (int i=0; i<this.getParameters().size(); i++) {
                if (!this.getParameters().get(i).equals(that.getParameters().get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return handler + ", file: " + file + ", tags: " + getTags() + ", parameters: " + getParameters();
    }
}
