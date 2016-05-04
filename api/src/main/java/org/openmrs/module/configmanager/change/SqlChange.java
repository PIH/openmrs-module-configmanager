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

package org.openmrs.module.configmanager.change;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.configmanager.util.SqlRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a unit of Change in configuration to execute
 */
public abstract class SqlChange implements Change {

    protected static Log log = LogFactory.getLog(SqlChange.class);

    /**
     * @return the table for the change
     */
    protected abstract String getTable();

    /**
     * @return the column values for the change
     */
    protected abstract Map<String, Object> getColumnValues();

    /**
     * By default, we use uuid to identify a change
     */
    protected List<String> getIdentityColumns() {
        return Arrays.asList("uuid");
    }

    /**
     * @return the additional column values that are not meaningful to the content of the change, but which need to be present on insert
     * Examples of this might be creator, date_created, etc
     */
    protected Map<String, Object> getAdditionalValuesForInsert() {
        return new HashMap<String, Object>();
    }

    /**
     * @return any value conversions needed between the representations in the change and the DB
     * Examples of this might be to represent relationships to other objects as Strings, and then convert these to their primary keys
     */
    protected Map<String, Object> getColumnValueConversions() {
        return new HashMap<String, Object>();
    }

    /**
     * Execute this Change by inserting or updating a database row
     * @see Change#execute()
     */
    public void execute() {
        log.debug("Executing change");

        List<Object> values = new ArrayList<Object>();

        // First determine if this change requires an insert or an update
        boolean isInsert = !identityExists();

        // Get the column-to-value map that needs to make up the query
        Map<String, Object> columnValues = new HashMap<String, Object>();
        columnValues.putAll(getColumnValues());
        columnValues.putAll(getColumnValueConversions());
        if (isInsert) {
            columnValues.putAll(getAdditionalValuesForInsert());
        }

        // Construct the insert or update query as appropriate
        String query = "";
        if (isInsert) {
            StringBuilder colClause = new StringBuilder();
            StringBuilder valClause = new StringBuilder();
            for (String colName : columnValues.keySet()) {
                colClause.append(colClause.length() > 0 ? ", " : "").append(colName);
                valClause.append(valClause.length() > 0 ? ", " : "").append("?");
                values.add(columnValues.get(colName));
            }
            query = "insert into " + getTable() + " (" + colClause + ") values (" + valClause + ")";
            log.debug("Built insert query: [" + query + "] with values [" + values + "]");
        }
        else {
            StringBuilder updateClause = new StringBuilder();
            updateClause.append("update ").append(getTable());
            String prefix = " set ";
            for (Map.Entry<String, Object> e : columnValues.entrySet()) {
                updateClause.append(prefix).append(e.getKey()).append(" = ? ");
                prefix = ",";
                values.add(e.getValue());
            }
            prefix = " where ";
            for (String idCol : getIdentityColumns()) {
                updateClause.append(prefix).append(idCol).append(" = ?");
                prefix = " and ";
                values.add(columnValues.get(idCol));
            }
            query = updateClause.toString();
            log.debug("Built update query: [" + query + "] with values [" + values + "]");
        }

        SqlRunner.update(query, values.toArray());
        log.debug("Change executed successfully");
    }

    /**
     * @return true if this change has an existing identity in the database
     */
    protected boolean identityExists() {
        StringBuilder sb = new StringBuilder();
        List<Object> values = new ArrayList<Object>();
        sb.append("select count(*) from ").append(getTable()).append(" where ");
        for (int i=0; i<getIdentityColumns().size(); i++) {
            String idCol = getIdentityColumns().get(i);
            sb.append(i > 0 ? " and " : "").append(idCol).append(" = ?");
            values.add(getColumnValues().get(idCol));
        }
        Number numFound = SqlRunner.querySingleValue(sb.toString(), Number.class, values.toArray());
        return numFound.intValue() > 0;
    }
}
