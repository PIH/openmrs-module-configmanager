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

package org.openmrs.module.configmanager.csv;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for encapsulating methods needed for parsing CSV files
 */
public class CsvRow {

    private List<String> columns;
    private List<String> values;

    public CsvRow(List<String> columns, List<String> values) {
        this.columns = columns;
        this.values = values;
    }

    public String getStringValue(String columnName) {
        for (int i = 0; i < getColumns().size(); i++) {
            if (getColumns().get(i).equalsIgnoreCase(columnName)) {
                return getValues().get(i);
            }
        }
        return null;
    }

    public Integer getIntegerValue(String columnName) {
        String stringVal = getStringValue(columnName);
        if (StringUtils.isNotBlank(stringVal)) {
            return Integer.valueOf(stringVal);
        }
        return null;
    }

    public Boolean getBooleanValue(String columnName) {
        String stringVal = getStringValue(columnName);
        if (StringUtils.isNotBlank(stringVal)) {
            return Boolean.valueOf(stringVal);
        }
        return null;
    }

    public List<String> getColumns() {
        if (columns == null) {
            columns = new ArrayList<String>();
        }
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public void addColumn(String column) {
        getColumns().add(column);
    }

    public List<String> getValues() {
        if (values == null) {
            values = new ArrayList<String>();
        }
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public void addValue(String value) {
        getValues().add(value);
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
