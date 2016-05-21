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

import au.com.bytecode.opencsv.CSVReader;
import org.openmrs.module.configmanager.ConfigurationException;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

/**
 * Wrapper for encapsulating methods needed for parsing CSV files
 */
public class CsvParser {

    private CSVReader csvReader;
    private List<String> headers;
    int currentDataRowNum = 0;

    private CsvParser() {}

    /**
     * Opens a new CSV Parser.  Should be explicitly closed once finished
     */
    public static CsvParser open(File csvFile) {
        CsvParser parser = new CsvParser();
        try {
            parser.setCsvReader(new CSVReader(new FileReader(csvFile)));
        }
        catch (Exception e) {
            throw new ConfigurationException("Error opening csv file: " + csvFile);
        }
        String[] headerRow = parser.readRowInternal();
        parser.setHeaders(Arrays.asList(headerRow));
        return parser;
    }

    /**
     * @return the next row in the CSV file, or null if no more rows
     */
    public CsvRow readNext() {
        String[] values = readRowInternal();
        return (values == null ? null : new CsvRow(headers, Arrays.asList(values)));
    }

    /**
     * Closes the CSV Parser
     */
    public void close() {
        try {
            csvReader.close();
        }
        catch (Exception e) {}
    }

    /**
     * Internal method to read the next row, in order to maintain a count of rows processed
     */
    private String[] readRowInternal() {
        try {
            currentDataRowNum++;
            return getCsvReader().readNext();
        }
        catch (Exception e) {
            throw new ConfigurationException("Unable to load row number " + currentDataRowNum + " from CSV file", e);
        }
    }

    //***** PROPERTY ACCESS *****

    public CSVReader getCsvReader() {
        return csvReader;
    }

    public void setCsvReader(CSVReader csvReader) {
        this.csvReader = csvReader;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }
}
