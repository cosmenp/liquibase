package liquibase.database;

import liquibase.DatabaseChangeLogLock;
import liquibase.database.sql.SqlStatement;
import liquibase.database.structure.DatabaseObject;
import liquibase.exception.JDBCException;
import liquibase.exception.LockException;
import liquibase.migrator.Migrator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

public interface Database extends DatabaseObject {
    /**
     * Is this AbstractDatabase subclass the correct one to use for the given connection.
     */
    boolean isCorrectDatabaseImplementation(Connection conn) throws JDBCException;

    /**
     * If this database understands the given url, return the default driver class name.  Otherwise return null.
     */
    String getDefaultDriver(String url);

    DatabaseConnection getConnection();

    void setConnection(Connection conn);
    
    void setConnection(DatabaseConnection conn);
    
    /**
     * Auto-commit mode to run in
     */
    public boolean getAutoCommitMode();

    /**
     * Determines if the database supports DDL within a transaction or not.
     * 
     * @return True if the database supports DDL within a transaction, otherwise false.
     */
    boolean supportsDDLInTransaction();

    String getDatabaseProductName();

    String getDatabaseProductVersion() throws JDBCException;

    /**
     * Returns the full database product name.  May be different than what the JDBC connection reports (getDatabaseProductName())
     */
    String getProductName();

    /**
     * Returns an all-lower-case short name of the product.  Used for end-user selecting of database type
     * such as the DBMS precondition.
     */
    String getTypeName();

    String getDriverName() throws JDBCException;

    String getConnectionURL() throws JDBCException;

    String getConnectionUsername() throws JDBCException;

    String getCatalogName() throws JDBCException;

    String getSchemaName() throws JDBCException;

    /**
     * Returns whether this database support initially deferrable columns.
     */
    boolean supportsInitiallyDeferrableColumns();

    public boolean supportsSequences();

    public boolean supportsAutoIncrement();

    String getColumnType(String columnType, Boolean autoIncrement);

    String getFalseBooleanValue();

    String getTrueBooleanValue();

    String getDateLiteral(String isoDate);

    /**
     * Returns database-specific function for generating the current date/time.
     */
    String getCurrentDateTimeFunction();

    void setCurrentDateTimeFunction(String function);


    String getLineComment();

    String getAutoIncrementClause();

    String getDatabaseChangeLogTableName();

    String getDatabaseChangeLogLockTableName();

    /**
     * Returns SQL to concat the passed values.
     */
    String getConcatSql(String ... values);

    boolean acquireLock(Migrator migrator) throws LockException;

    void releaseLock() throws LockException;

    DatabaseChangeLogLock[] listLocks() throws LockException;

    boolean doesChangeLogTableExist();

    boolean doesChangeLogLockTableExist();

    void checkDatabaseChangeLogTable(Migrator migrator) throws JDBCException, IOException;

    void checkDatabaseChangeLogLockTable(Migrator migrator) throws JDBCException, IOException;

    void dropDatabaseObjects(String schema) throws JDBCException;

    void tag(String tagString) throws JDBCException;

    boolean doesTagExist(String tag) throws JDBCException;

    boolean isSystemTable(String catalogName, String schemaName, String tableName);

    boolean isLiquibaseTable(String tableName);

    SqlStatement createFindSequencesSQL(String schema) throws JDBCException;

    boolean shouldQuoteValue(String value);

    boolean supportsTablespaces();

    String getViewDefinition(String schemaName, String name) throws JDBCException;

    int getDatabaseType(int type);

    String getDatabaseProductName(Connection conn) throws JDBCException;

    /**
     * Returns the actual database-specific data type to use a "boolean" column.
     */
    String getBooleanType();

    /**
     * Returns the actual database-specific data type to use a "currency" column.
     */
    String getCurrencyType();

    /**
     * Returns the actual database-specific data type to use a "UUID" column.
     */
    String getUUIDType();

    /**
     * Returns the actual database-specific data type to use a "CLOB" column.
     */
    String getClobType();

    /**
     * Returns the actual database-specific data type to use a "BLOB" column.
     */
    String getBlobType();

    String getDateType();

    /**
     * Returns the actual database-specific data type to use a "datetime" column.
     */
    String getDateTimeType();

    String getTimeType();

    Object convertDatabaseValueToJavaObject(Object defaultValue, int dataType, int columnSize, int decimalDigits) throws ParseException;

    String convertJavaObjectToString(Object value);

    boolean isSystemView(String catalogName, String schemaName, String name);

    String getDateLiteral(java.sql.Date date);

    String getDateLiteral(java.sql.Time time);

    String getDateLiteral(java.sql.Timestamp timeStamp);

    String getDateLiteral(Date defaultDateValue);

    /**
     * Escapes the table name in a database-dependent manner so reserved words can be used as a table name (i.e. "order").
     * Currently only escapes MS-SQL because other DBMSs store table names case-sensitively when escaping is used which
     * could confuse end-users.  Pass null to schemaName to use the default schema
     */
    String escapeTableName(String schemaName, String tableName);

//    Set<UniqueConstraint> findUniqueConstraints(String schema) throws JDBCException;

    String convertRequestedSchemaToSchema(String requestedSchema) throws JDBCException;

    String convertRequestedSchemaToCatalog(String requestedSchema) throws JDBCException;

    boolean supportsSchemas();

    String generatePrimaryKeyName(String tableName);

    String escapeSequenceName(String schemaName, String sequenceName);

    String escapeViewName(String schemaName, String viewName);

    boolean isColumnAutoIncrement(String schemaName, String tableName, String columnName) throws SQLException;
}
