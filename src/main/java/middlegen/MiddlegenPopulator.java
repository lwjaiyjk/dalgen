/*
 * Copyright (c) 2001, Aslak Hellesøy, BEKK Consulting
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * - Neither the name of BEKK Consulting nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */
package middlegen;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import middlegen.javax.Sql2Java;
import middlegen.plugins.iwallet.config.IWalletConfigException;
import middlegen.plugins.iwallet.util.DalUtil;

import org.apache.commons.lang.StringUtils;

import com.atom.dalgen.utils.CfgUtils;
import com.atom.dalgen.utils.DBUtils;
import com.atom.dalgen.utils.LogUtils;

/**
 * @author <a href="mailto:aslak.hellesoy@bekk.no">Aslak Helles</a>
 * @created 3. oktober 2001
 */
public class MiddlegenPopulator {

    /**
     * @todo-javadoc Describe the column
     */
    private Middlegen middlegen;

    /**
     * @todo-javadoc Describe the column
     */
    private String    _schema;

    /**
     * @todo-javadoc Describe the column
     */
    private String    _catalog;

    /**
     * @todo-javadoc Describe the column
     */
    private String[]  _types = null;

    /**
     * @todo-javadoc Describe the field
     */
    private String    _sortColumns;

    private Connection getConnection() {
        return DBUtils.fetchConnection();
    }

    private DatabaseMetaData getMetaData() {
        return DBUtils.fetchMetaData();
    }

    /**
     * Describe what the SchemaFactory constructor does
     */
    public MiddlegenPopulator(Middlegen middlegen) throws MiddlegenException {
        this.middlegen = middlegen;

        _schema = StringUtils.trimToEmpty(CfgUtils.getSchema());
        _catalog = CfgUtils.getCatalog();
        _sortColumns = StringUtils.lowerCase(CfgUtils.getSortColumns());

        try {
            tune();
        } catch (SQLException e) {
            throw new MiddlegenException("Couldn't tune database:" + e.getMessage());
        }
    }

    /**
     * Adds regular tables to middlegen's list of tables to process.
     */
    public void addRegularTableElements() throws MiddlegenException {
        ResultSet tableRs = null;
        try {
            tableRs = this.getMetaData().getTables(_catalog, _schema, null, _types);

            while (tableRs.next()) {
                String tableName = tableRs.getString("TABLE_NAME");
                String tableType = tableRs.getString("TABLE_TYPE");
                String schemaName = tableRs.getString("TABLE_SCHEM");
                String ownerSinonimo = null;
                if ("TABLE".equals(tableType) || "VIEW".equals(tableType)
                    || ("SYNONYM".equals(tableType) && isOracle())) {
                    // it's a regular table or a synonym
                    LogUtils.log("schema:" + _schema + "," + schemaName);
                    LogUtils.log("table:" + tableName);

                    TableElement tableElement = new TableElement();
                    tableElement.setName(tableName);
                    if ("SYNONYM".equals(tableType) && isOracle()) {
                        ownerSinonimo = getSynonymOwner(tableName);
                        if (ownerSinonimo != null) {
                            tableElement.setOwnerSynonymName(ownerSinonimo);
                        }
                    }
                    middlegen.addTableElement(tableElement);
                } else {
                    LogUtils.log("Ignoring table " + tableName + " of type " + tableType);
                }
            }
            if (middlegen.getTableElements().isEmpty()) {
                String databaseStructure = getDatabaseStructure();
                throw new MiddlegenException(
                    "Middlegen successfully connected to the database, but "
                            + "couldn't find any tables. Perhaps the specified schema or catalog is wrong? -Or maybe "
                            + "there aren't any tables in the database at all?" + databaseStructure);
            }
        } catch (SQLException e) {
            // schemaRs and catalogRs are only used for error reporting if we get an exception
            String databaseStructure = getDatabaseStructure();
            LogUtils.log(e.getMessage(), e);
            throw new MiddlegenException(
                "Couldn't get list of tables from database. Probably a JDBC driver problem."
                        + databaseStructure);
        } finally {
            try {
                tableRs.close();
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * <p>add by zhaoxu 20060918</p>
     */
    public void populate(Map<String, TableElement> wantedTables, String tabs)
                                                                             throws MiddlegenException {
        try {
            addTables(wantedTables);

            for (DbTable table : middlegen.getTables()) {
                if (DalUtil.inTabs(table.getName())) {
                    addColumns(table);
                }
            }

            markFksToUnwantedTables();

            if (_sortColumns != null) {
                Comparator<Column> comparator = new ColumnComparator(_sortColumns);

                for (DbTable table : middlegen.getTables()) {
                    table.sortColumns(comparator);
                }
            }
        } catch (SQLException e) {
            LogUtils.log(e.getMessage(), e);
            throw new MiddlegenException("Database problem:" + e.getMessage());
        } catch (IWalletConfigException e) {
            LogUtils.log(e.getMessage());
            throw new MiddlegenException(e.getMessage());
        }
    }

    /**
     * Gets the DatabaseStructure attribute of the MiddlegenPopulator object
     *
     * @todo-javadoc Write javadocs for exception
     * @return The DatabaseStructure value
     * @exception MiddlegenException Describe the exception
     */
    private String getDatabaseStructure() throws MiddlegenException {
        ResultSet schemaRs = null;
        ResultSet catalogRs = null;
        String nl = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer(nl);
        // Let's give the user some feedback. The exception
        // is probably related to incorrect schema configuration.
        sb.append("Configured schema:").append(_schema).append(nl);
        sb.append("Configured catalog:").append(_catalog).append(nl);

        try {
            schemaRs = this.getMetaData().getSchemas();
            sb.append("Available schemas:").append(nl);
            while (schemaRs.next()) {
                sb.append("  ").append(schemaRs.getString("TABLE_SCHEM")).append(nl);
            }
        } catch (SQLException e2) {
            LogUtils.log("Couldn't get schemas", e2);
            sb.append("  ?? Couldn't get schemas ??").append(nl);
        } finally {
            try {
                schemaRs.close();
            } catch (Exception ignore) {
            }
        }

        try {
            catalogRs = this.getMetaData().getCatalogs();
            sb.append("Available catalogs:").append(nl);
            while (catalogRs.next()) {
                sb.append("  ").append(catalogRs.getString("TABLE_CAT")).append(nl);
            }
        } catch (SQLException e2) {
            LogUtils.log("Couldn't get catalogs", e2);
            sb.append("  ?? Couldn't get catalogs ??").append(nl);
        } finally {
            try {
                catalogRs.close();
            } catch (Exception ignore) {
            }
        }
        return sb.toString();
    }

    /**
     * @todo-javadoc Write javadocs for exception
     * @return a list of tables found in the database
     * @throws MiddlegenException Describe the exception
     */
    private String getDatabaseTables() throws MiddlegenException {
        String nl = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer(nl);
        sb.append("Found the following tables:");
        sb.append(nl);

        ResultSet tableRs = null;
        try {
            tableRs = this.getMetaData().getTables(_catalog, _schema, null, _types);
            while (tableRs.next()) {
                String realTableName = tableRs.getString("TABLE_NAME");
                sb.append(realTableName);
                sb.append(" ");
            }
        } catch (SQLException e2) {
            LogUtils.log("Couldn't get schemas", e2);
            sb.append("  ?? Couldn't get schemas ??").append(nl);
        } finally {
            try {
                tableRs.close();
            } catch (Exception ignore) {
                // ignore
            }
        }

        sb.append(nl);
        sb.append("----");
        sb.append(nl);
        return sb.toString();
    }

    /**
     * Returns if we are on Oracle
     *
     * @todo-javadoc Write javadocs for exception
     * @todo-javadoc Write javadocs for exception
     * @return <code>true</code> we are on Oracle, <code>false</code> otherwise
     */
    private boolean isOracle() {
        boolean ret = false;
        try {
            ret = (this.getMetaData().getDatabaseProductName().toLowerCase().indexOf("oracle") != -1);
        } catch (Exception ignore) {
        }

        return ret;
    }

    /**
     * Returns synonym owner for Oracle.
     *
     * @param synonymName Syn name
     * @return Synonym owner for Oracle
     * @throws MiddlegenException If something orrible happens
     */
    private String getSynonymOwner(String synonymName) throws MiddlegenException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String ret = null;
        /*
         * added by yangyanzhao 20100604 ������ͬ��ʱ?������ԭ�������ʱ����?����޸���sql
         * select table_owner from sys.all_synonyms where table_name=? and owner=?�ĳ�
         * select table_owner from sys.all_synonyms where (table_name=? or synonym_name=?) and owner=?
         */
        try {
            ps = this
                .getConnection()
                .prepareStatement(
                    "select table_owner from sys.all_synonyms where (table_name=? or synonym_name=?) and owner=?");
            ps.setString(1, synonymName);
            ps.setString(2, synonymName);
            ps.setString(3, _schema);
            rs = ps.executeQuery();
            if (rs.next()) {
                ret = rs.getString(1);
            } else {
                String databaseStructure = getDatabaseStructure();
                throw new MiddlegenException("Wow! Synonym " + synonymName
                                             + " not found. How can it happen? "
                                             + databaseStructure);
            }
        } catch (SQLException e) {
            String databaseStructure = getDatabaseStructure();
            LogUtils.log(e.getMessage(), e);
            throw new MiddlegenException("Exception in getting synonym owner " + databaseStructure);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
        }
        return ret;
    }

    /**
     * Marks the columns as foreign keys if they have a relationship to an
     * unwanted table
     *
     * @todo-javadoc Write javadocs for exception
     * @throws MiddlegenException Describe the exception
     */
    private void markFksToUnwantedTables() throws MiddlegenException {
        ResultSet tableRs = null;
        try {
            tableRs = this.getMetaData().getTables(_catalog, _schema, null, _types);
            while (tableRs.next()) {
                String tableName = tableRs.getString("TABLE_NAME");
                String tableType = tableRs.getString("TABLE_TYPE");
                // ignore the views, they don't have foreign key relationships
                if (("TABLE".equals(tableType) && !middlegen.containsTable(tableName))
                    || ("SYNONYM".equals(tableType) && isOracle())) {
                    String ownerSinonimo = null;
                    if ("SYNONYM".equals(tableType) && isOracle()) {
                        ownerSinonimo = getSynonymOwner(tableName);
                    }
                    ResultSet exportedKeyRs = null;
                    if (ownerSinonimo != null) {
                        exportedKeyRs = this.getMetaData().getExportedKeys(_catalog, ownerSinonimo,
                            tableName);
                    } else {
                        exportedKeyRs = this.getMetaData().getExportedKeys(_catalog, _schema,
                            tableName);
                    }
                    while (exportedKeyRs.next()) {
                        String fkTableName = exportedKeyRs.getString("FKTABLE_NAME");
                        String fkColumnName = exportedKeyRs.getString("FKCOLUMN_NAME");
                        // Mark the fk field as an fk anyway. This will be useful for column sorting for example
                        if (middlegen.containsTable(fkTableName)) {
                            DbTable fkTable = middlegen.getTable(fkTableName);
                            DbColumn fkColumn = (DbColumn) fkTable.getColumn(fkColumnName);
                            fkColumn.setFk(true);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            // schemaRs and catalogRs are only used for error reporting if we get an exception
            String databaseStructure = getDatabaseStructure();
            LogUtils.log(e.getMessage(), e);
            throw new MiddlegenException(
                "Couldn't get list of tables from database. Probably a JDBC driver problem."
                        + databaseStructure);
        } finally {
            try {
                tableRs.close();
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Adds columns to the table, and registers any relations.
     */
    private void addColumns(DbTable table) throws MiddlegenException, SQLException {
        LogUtils.log("-------setColumns(" + table.getSqlName() + ")");

        // get the primary keys
        List<String> primaryKeys = new LinkedList<String>();
        ResultSet primaryKeyRs = null;
        if (table.getTableElement().getOwnerSynonymName() != null) {
            primaryKeyRs = this.getMetaData().getPrimaryKeys(_catalog,
                table.getTableElement().getOwnerSynonymName(), table.getSqlName());
        } else {
            primaryKeyRs = this.getMetaData().getPrimaryKeys(_catalog, _schema, table.getSqlName());
        }
        while (primaryKeyRs.next()) {
            String columnName = primaryKeyRs.getString("COLUMN_NAME");
            LogUtils.log("primary key:" + columnName);
            primaryKeys.add(columnName);
        }
        primaryKeyRs.close();

        // get the indices and unique columns
        List<String> indices = new LinkedList<String>();
        // maps index names to a list of columns in the index
        Map<String, String> uniqueIndices = new HashMap<String, String>();
        // maps column names to the index name.
        Map<String, List<String>> uniqueColumns = new HashMap<String, List<String>>();

        ResultSet indexRs = null;
        try {
            if (table.getTableElement().getOwnerSynonymName() != null) {
                indexRs = this.getMetaData().getIndexInfo(_catalog,
                    table.getTableElement().getOwnerSynonymName(), table.getSqlName(), false, true);
            } else {
                indexRs = this.getMetaData().getIndexInfo(_catalog, _schema, table.getSqlName(),
                    false, true);
            }

            while (indexRs.next()) {
                String columnName = indexRs.getString("COLUMN_NAME");

                if (columnName != null) {
                    LogUtils.log("index:" + columnName);
                    indices.add(columnName);
                }

                // now look for unique columns
                String indexName = indexRs.getString("INDEX_NAME");
                boolean nonUnique = indexRs.getBoolean("NON_UNIQUE");

                if (!nonUnique && columnName != null && indexName != null) {
                    List<String> l = uniqueColumns.get(indexName);

                    if (l == null) {
                        l = new ArrayList<String>();
                        uniqueColumns.put(indexName, l);
                    }
                    l.add(columnName);

                    uniqueIndices.put(columnName, indexName);
                    LogUtils.log("unique:" + columnName + " (" + indexName + ")");
                }
            }
        } catch (Throwable t) {
            // Bug #604761 Oracle getIndexInfo() needs major grants
            // http://sourceforge.net/tracker/index.php?func=detail&aid=604761&group_id=36044&atid=415990
        } finally {
            if (indexRs != null) {
                indexRs.close();
            }
        }

        // get the columns
        List<Column> columns = new LinkedList<Column>();
        ResultSet columnRs = null;
        if (table.getTableElement().getOwnerSynonymName() != null) {
            columnRs = this.getMetaData().getColumns(_catalog,
                table.getTableElement().getOwnerSynonymName(), table.getSqlName(), null);
        } else {
            columnRs = this.getMetaData().getColumns(_catalog, _schema, table.getSqlName(), null);
        }

        while (columnRs.next()) {
            int sqlType = columnRs.getInt("DATA_TYPE");
            String sqlTypeName = columnRs.getString("TYPE_NAME");
            String columnName = columnRs.getString("COLUMN_NAME");
            String columnDefaultValue = columnRs.getString("COLUMN_DEF");

            String remark = columnRs.getString("REMARKS");
            if (StringUtils.isBlank(remark)) {
                remark = columnName;
            }

            // if columnNoNulls or columnNullableUnknown assume "not nullable"
            boolean isNullable = (DatabaseMetaData.columnNullable == columnRs.getInt("NULLABLE"));
            int size = columnRs.getInt("COLUMN_SIZE");
            int decimalDigits = columnRs.getInt("DECIMAL_DIGITS");

            boolean isPk = false;
            List<String> pkColumnsOverride = table.getTableElement()
                .getPkColumnsOverrideCollection();
            if (pkColumnsOverride.size() > 0) {
                isPk = pkColumnsOverride.contains(columnName);
            } else {
                isPk = primaryKeys.contains(columnName);
            }
            boolean isIndexed = indices.contains(columnName);
            String uniqueIndex = uniqueIndices.get(columnName);
            List<String> columnsInUniqueIndex = null;
            if (uniqueIndex != null) {
                columnsInUniqueIndex = uniqueColumns.get(uniqueIndex);
            }

            boolean isUnique = columnsInUniqueIndex != null && columnsInUniqueIndex.size() == 1;
            if (isUnique) {
                LogUtils.log("unique column:" + columnName);
            }

            Column column = new DbColumn(table, sqlType, sqlTypeName, columnName, remark, size,
                decimalDigits, isPk, isNullable, isIndexed, isUnique, columnDefaultValue);
            columns.add(column);
        }
        columnRs.close();

        for (Column column : columns) {
            table.addColumn(column);
        }

        // for each unique index, add a unique tuple to the table
        for (List<String> l : uniqueColumns.values()) {
            List<Column> uniqueTuple = new ArrayList<Column>();
            for (String colName : l) {
                uniqueTuple.add(table.getColumn(colName));
            }

            table.addUniqueTuple(uniqueTuple);
        }

        // In case none of the columns were primary keys, issue a warning.
        if (primaryKeys.size() == 0) {
            LogUtils.log("WARNING: The JDBC driver didn't report any primary key columns in "
                         + table.getSqlName());
        }
    }

    /**
     * Tunes the settings depending on database.
     */
    private void tune() throws SQLException {
        DatabaseMetaData metaData = this.getMetaData();

        String databaseProductName = metaData.getDatabaseProductName();
        String databaseProductVersion = metaData.getDatabaseProductVersion();
        String driverName = metaData.getDriverName();
        String driverVersion = metaData.getDriverVersion();

        DatabaseInfo databaseInfo = new DatabaseInfo(databaseProductName, databaseProductVersion,
            driverName, driverVersion);

        middlegen.setDatabaseInfo(databaseInfo);

        LogUtils.log("databaseProductName= " + databaseProductName);
        LogUtils.log("databaseProductVersion= " + databaseProductVersion);
        LogUtils.log("driverName= " + driverName);
        LogUtils.log("driverVersion= " + driverVersion);
        LogUtils.log("schema= " + _schema);
        LogUtils.log("catalog= " + _catalog);

        // ORACLE TUNING
        if (isOracle()) {
            // capitalize catalogue
            if (_catalog != null) {
                _catalog = _catalog.toUpperCase();
            }

            // usually the access rights are set up so that you can only query your schema
            // ie. schema = username
            if (_schema != null) {
                _schema = _schema.toUpperCase();
            }

            // null will also retrieve objects for which only synonyms exists, but this objects will not
            // be successfully processed anyway - did not check why  -probably columns not retrieved
            _types = new String[] { "TABLE", "VIEW", "SYNONYM" };
        }

        // MSSQL TUNING
        // TODO David Cowan: check the driverName instead. All drivers for MSSQL
        // will probably not behave in the same way. Possibly add other types too...
        if (databaseProductName.toLowerCase().indexOf("microsoft") != -1) {
            Sql2Java.overridePreferredJavaTypeForSqlType(Types.BINARY, "java.lang.String");
        }
    }

    /**
     * Describe the method
     */
    private void addTables(Map<String, TableElement> wantedTables) throws MiddlegenException,
                                                                  SQLException {
        // get the tables
        LogUtils.log("-- tables --");

        // We're keeping track of the table names so we can detect if a table
        // occurs in different schemas
        Map<String, String> tableSchemaMap = new HashMap<String, String>();

        for (TableElement tableElement : wantedTables.values()) {
            String tableName = tableElement.getName();
            String schemaName = null;
            // check that the table really exists
            ResultSet tableRs = null;
            try {
                tableRs = this.getMetaData().getTables(_catalog, _schema, tableName, _types);
                if (!tableRs.next()) {
                    tableRs = this.getMetaData().getTables(_catalog, _schema,
                        tableName.toLowerCase(), _types);
                    if (!tableRs.next()) {
                        tableRs = this.getMetaData().getTables(_catalog, _schema,
                            tableName.toUpperCase(), _types);
                        if (!tableRs.next()) {
                            throw new MiddlegenException(
                                "The database doesn't have any table named "
                                        + tableName
                                        + ".  Please make sure the table exists. Also note that some databases are case sensitive."
                                        + getDatabaseTables());
                        }
                    }
                }
                // BUG [ 596044 ] Case in table names - relationships
                // Update the tableElement with the name reported by the resultset.
                // The case might not be the same, and some drivers want correct case
                // in getCrossReference/getExportedKeys which we'll call later.
                schemaName = Util.ensureNotNull(tableRs.getString("TABLE_SCHEM"));
                String realTableName = tableRs.getString("TABLE_NAME");
                String tableType = tableRs.getString("TABLE_TYPE");
                tableElement.setPhysicalName(realTableName);

                if ("SYNONYM".equals(tableType) && isOracle()) {
                    tableElement.setOwnerSynonymName(getSynonymOwner(realTableName));
                }
                // do this for non-synonyms only
                // Test for tables in different schemas
                String alreadySchema = (String) tableSchemaMap.get(realTableName);
                if (alreadySchema != null) {
                    throw new MiddlegenException(
                        "The table named " + realTableName + " was found both in the schema "
                                + "named " + alreadySchema + " and in the schema named "
                                + schemaName + ". "
                                + "You have to specify schema=\"something\" in the middlegen task.");
                }

                tableSchemaMap.put(realTableName, schemaName);

                // Some more schema sanity testing
                // do for non-synonyms only
                if (!("".equals(schemaName)) && !("null".equals(schemaName))
                    && !Util.equals(_schema, schemaName)
                    && !("SYNONYM".equals(tableType) && isOracle())) {
                    LogUtils
                        .log("The table named "
                             + realTableName
                             + " was found in the schema "
                             + "named \""
                             + schemaName
                             + "\". However, Middlegen was not configured "
                             + "to look for tables in a specific schema. You should consider specifying "
                             + "schema=\"" + schemaName + "\" instead of schema=\"" + _schema
                             + "\" in the middlegen task.");
                }
            } finally {
                try {
                    tableRs.close();
                } catch (SQLException ignore) {
                } catch (NullPointerException ignore) {
                }
            }
            DbTable table = new DbTable(tableElement, schemaName);
            table.init();
            middlegen.addTable(table);
        }
    }

    /**
     * Describe what this class does
     *
     * @author ewa
     * @created 8. mars 2004
     * @todo-javadoc Write javadocs
     */
    private static class ColumnComparator implements Comparator<Column> {

        /**
         * @todo-javadoc Describe the field
         */
        private String _orderBy;

        /**
         * Describe what the ColumnComparator constructor does
         */
        public ColumnComparator(String orderBy) {
            _orderBy = orderBy;
        }

        /**
         * {@inheritDoc}
         */
        public int compare(Column c1, Column c2) {
            if (c1.equals(c2)) {
                return 0;
            }

            int priority1 = getColumnPriority(c1);
            int priority2 = getColumnPriority(c2);

            if (priority1 < priority2) {
                return -1;
            }

            if (priority1 > priority2) {
                return 1;
            }

            return c1.getSqlName().compareTo(c2.getSqlName());
        }

        /**
         * Gets the ColumnPriority attribute of the ColumnComparator object
         */
        private int getColumnPriority(Column column) {
            int priority = _orderBy.length();

            int pos = -1;
            if (column.isPk()) {
                pos = _orderBy.indexOf("pk");
                if (pos >= 0 && pos < priority) {
                    priority = pos;
                }
            }
            if (column.isFk()) {
                pos = _orderBy.indexOf("fk");
                if (pos >= 0 && pos < priority) {
                    priority = pos;
                }
            }
            if (column.isNullable()) {
                pos = _orderBy.indexOf("nullable");
                if (pos >= 0 && pos < priority) {
                    priority = pos;
                }
            }
            if (!column.isNullable()) {
                pos = _orderBy.indexOf("mandatory");
                if (pos >= 0 && pos < priority) {
                    priority = pos;
                }
            }
            if (column.isIndexed()) {
                pos = _orderBy.indexOf("indexed");
                if (pos >= 0 && pos < priority) {
                    priority = pos;
                }
            }
            if (column.isUnique()) {
                pos = _orderBy.indexOf("unique");
                if (pos >= 0 && pos < priority) {
                    priority = pos;
                }
            }
            return priority;
        }
    }

}
