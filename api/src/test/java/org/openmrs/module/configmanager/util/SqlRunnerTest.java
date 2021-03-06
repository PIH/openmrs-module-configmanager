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
package org.openmrs.module.configmanager.util;

import org.apache.commons.dbutils.handlers.MapListHandler;
import org.junit.Test;
import org.openmrs.module.configmanager.util.SqlRunner;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests for the SqlRunner
 */
public class SqlRunnerTest extends BaseModuleContextSensitiveTest {

	@Test
	public void queryShouldReturnExpectedValues() throws Exception {
		String query = "select person_id, given_name, family_name from person_name";
        System.out.println(SqlRunner.query(query, new MapListHandler()));
	}
}
