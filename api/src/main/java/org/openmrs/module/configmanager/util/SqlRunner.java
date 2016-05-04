package org.openmrs.module.configmanager.util;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.util.DatabaseUpdater;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for building and executing SQL
 */
public class SqlRunner {

	protected static Log log = LogFactory.getLog(SqlRunner.class);

	//***** INSTANCE METHODS *****

    /**
     * @return the results of the passed query using the passed handler
     */
    public static <T> T query(String query, ResultSetHandler<T> handler, Object...params) {
        try {
            log.debug("Executing query: " + query);
            StopWatch sw = new StopWatch();
            sw.start();
            QueryRunner qr = new QueryRunner();
            T results = qr.query(DatabaseUpdater.getConnection(), query, handler, normalize(params));
            sw.stop();
            log.debug("Query executed successfully.  Time elapsed: " + sw.toString());
            return results;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Unable to execute query: " + query, e);
        }
    }

    /**
     * @return the results of the passed query as a single value of the passed type
     */
    public static <T> T querySingleValue(String query, Class<T> type, Object...params) {
        return query(query, new ScalarHandler<T>(), normalize(params));
    }

    /**
     * @return the results of the passed query as a Map.  The first two columns selected will be the key and value, respectively
     */
    public static <K, V> Map<K, V> queryAsMap(String query, Class<K> keyType, Class<V> valueType, Object...params) {
        return query(query, new ResultSetHandler<Map<K, V>>() {
            public Map<K, V> handle(ResultSet rs) throws SQLException {
                Map<K, V> ret = new HashMap<K, V>();
                while (rs.next()) {
                    ret.put((K)rs.getObject(1), (V)rs.getObject(2));
                }
                return ret;
            }
        }, normalize(params));
    }

    /**
     * Executes the following query as a database insert, update, or delete
     */
    public static void update(String query, Object... params) {
        try {
            QueryRunner qr = new QueryRunner();
            qr.update(DatabaseUpdater.getConnection(), query, normalize(params));
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Unable to execute update: " + query, e);
        }
    }

    /**
     * @return an array of parameter values normalized for use by SQL
     */
    protected static Object[] normalize(Object... values) {
        if (values != null) {
            Object[] newValues = new Object[values.length];
            for (int i=0; i<values.length; i++) {
                Object o = values[i];
                if (o != null) {
                    if (o.getClass().isEnum()) {
                        o = ((Enum)o).name();
                    }
                }
                newValues[i] = o;
            }
            return newValues;
        }
        return null;
    }
}
