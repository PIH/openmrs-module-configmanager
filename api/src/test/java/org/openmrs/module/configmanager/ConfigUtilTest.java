package org.openmrs.module.configmanager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsClassLoader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigUtilTest extends BaseModuleContextSensitiveTest {

    protected final Log log = LogFactory.getLog(getClass());

    @Test
    public void parseScriptIntoStatements_shouldHandleCommentsAndDelimiters() throws Exception {
        InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream("org/openmrs/module/configmanager/parse-sql-tests.sql");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String[] statements = ConfigUtil.parseScriptIntoStatements(reader);
        Assert.assertEquals(4, statements.length);
        reader.close();
        is.close();
    }
}
