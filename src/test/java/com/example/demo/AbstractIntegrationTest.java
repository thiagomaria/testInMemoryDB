package com.example.demo;

import static org.dbunit.Assertion.assertEqualsByQuery;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.ext.hsqldb.HsqldbConnection;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.xml.sax.InputSource;

import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PROTECTED)
public class AbstractIntegrationTest {

    private static IDatabaseConnection databaseConnection;
    private static Connection connection;

    private boolean isAfterFirstRun;

    private static Logger logger = Logger.getLogger(AbstractIntegrationTest.class);

    @BeforeClass
    public static void createDatabase() throws Exception {
        try {
            final Properties properties = loadProperties();
            final String driver = properties.getProperty("datasource.driver");
            final String url = properties.getProperty("datasource.url");
            final String userName = properties.getProperty("datasource.username");
            final String password = properties.getProperty("datasource.password");
            final String schema = properties.getProperty("datasource.schema");

            Class.forName(driver);
            connection = DriverManager.getConnection(url, userName, password);
            databaseConnection = new HsqldbConnection(connection, schema);
        } catch (final SQLException exception) {
            throw new RuntimeException(exception.getMessage(), exception);
        } catch (final ClassNotFoundException exception) {
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }

    @Before
    public void setDatabaseUp() throws Exception {
        if (!isAfterFirstRun) {
            runSQLCommands(getDatabaseFile());
        }
        runSQLCommands(getClearDatabaseFile());
        isAfterFirstRun = true;
    }

    @AfterClass
    public static void closeConnection() throws Exception {
        connection.close();
        databaseConnection.close();
    }

    protected void runSQLCommands(final String file) throws Exception {
        if (file != null) {
            final InputStream stream = getSQLInputStream(file);

            try (final BufferedReader databaseReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
                int i = 1;
                String sqlStatement = null;
                while ((sqlStatement = databaseReader.readLine()) != null) {
                    if(sqlStatement.startsWith("--")) {
                        i++;
                        continue;
                    }
                    final int index = sqlStatement.lastIndexOf(";");
                    if(index > -1){
                        sqlStatement = sqlStatement.substring(0, index + 1);
                    }
                    if (sqlStatement.trim().length() != 0) {
                        try {
                            connection.createStatement().execute(sqlStatement);
                        } catch (final Exception exception) {
                            logger.error("Error running command on line " + i + " of file " + file + ": " + exception.getMessage());
                            throw new RuntimeException(exception);
                        }
                    }
                    i++;
                }
            }
        }
    }

    protected IDatabaseConnection getConnection() {
        return databaseConnection;
    }

    protected static IDataSet getDataSet(final String dataset) {
        try {
            final InputSource source = new InputSource(AbstractIntegrationTest.class.getResourceAsStream(dataset));
            final FlatXmlProducer producer = new FlatXmlProducer(source, false, true);
            return new FlatXmlDataSet(producer);
        } catch (final Exception exception) {
            throw new RuntimeException("Cannot read the dataset file " + dataset + "!", exception);
        }
    }

    private static Properties loadProperties() throws Exception {
        final InputStream stream = ClassLoader.getSystemResourceAsStream("datasource.properties");
        if (stream == null) {
            throw new FileNotFoundException("File datasource.properties not found. A file named datasource.properties must be present "
                    + "in the src/test/resources folder of the project whose class is being tested.");
        }
        final Properties properties = new Properties();
        properties.load(stream);
        return properties;
    }

    private static InputStream getSQLInputStream(final String fileName) {
        return AbstractIntegrationTest.class.getResourceAsStream(fileName);
    }

    protected String getClearDatabaseFile() {
        return "/clear-database.sql";
    }

    protected String getDatabaseFile() {
        return "/database-setup.sql";
    }

    protected void verifyDatasetForTable(final String fileName, final String tableName, final String[] ignoredColumns) throws Exception {
        assertEqualsByQuery(getDataSet("/datasets/" + fileName), getConnection(), "select * from " + tableName, tableName,
                ignoredColumns);
    }

    protected void verifyDatasetForTable(final String fileName, final String tableName, final String sqlStatement, final String[] ignoredColumns)
            throws Exception {
        printValues(tableName, sqlStatement, getConnection());
        assertEqualsByQuery(getDataSet("/datasets/" + fileName), getConnection(), sqlStatement, tableName, ignoredColumns);
    }
 
    private void printValues(final String tableName, final String query, final IDatabaseConnection conn) throws Exception {
        final ITable table = conn.createQueryTable(tableName, query);
        for (int i = 0; i < table.getRowCount(); i++) {
            System.out.print(i + 1 + " ");
            for (int j = 0; j < table.getTableMetaData().getColumns().length; j++) {
                final String column = table.getTableMetaData().getColumns()[j].getColumnName();
                System.out.print(column + ": " + table.getValue(i, column) + " ");
            }
            System.out.println();
        }
    }
}
