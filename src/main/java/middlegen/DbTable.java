package middlegen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import middlegen.predicates.column.Mandatory;
import middlegen.predicates.column.PrimaryKey;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.atom.dalgen.utils.LogUtils;

/**
 * This class represents a table in a database
 */
public class DbTable extends PreferenceAware implements Table {

    /**
     * @todo-javadoc Describe the field
     */
    private TableElement                     _tableElement;

    /** All the relations connected to this table */
    // private final Collection                 _relationsipRoles        = new ArrayList();

    /**
     * @todo-javadoc Describe the field
     */
    private final String                     _schemaName;

    /** All the columns of this table */
    private final List<Column>               _columns                 = new ArrayList<Column>();

    /**
     * @todo-javadoc Describe the column
     */
    private final Map                        _columnSqlName2ColumnMap = new HashMap();

    /** The unique tuples of this table */
    private final Collection                 _uniqueTuples            = new ArrayList();

    /**
     * Describe what the Table constructor does
     */
    public DbTable(TableElement tableElement, String schemaName) {
        _tableElement = tableElement;
        _schemaName = schemaName;
    }

    /**
     * Sets the Position attribute of the DbTable object
     *
     * @param x The new Position value
     * @param y The new Position value
     */
    public void setPosition(int x, int y) {
        setPrefsValue("x", String.valueOf(x));
        setPrefsValue("y", String.valueOf(y));
    }

    /**
     * @return x position stored in prefs, or -1 if no value exists
     */
    public int getPrefsX() {
        return getPrefsPos("x");
    }

    /**
     * @return x position stored in prefs, or -1 if no value exists
     */
    public int getPrefsY() {
        return getPrefsPos("y");
    }

    /**
     * Gets the Columns attribute of the DbTable object
     *
     * @todo-javadoc Write javadocs for method parameter
     * @param predicate Describe what the parameter does
     * @return The Columns value
     */
    public Collection getColumns(Predicate predicate) {
        return CollectionUtils.select(getColumns(), predicate);
    }

    /**
     * Gets the PkTableSqlName attribute of the DbTable object
     *
     * @return The PkTableSqlName value
     */
    public TableElement getTableElement() {
        return _tableElement;
    }

    /**
     * Gets the SqlName attribute of the DbTable object
     *
     * @todo-javadoc Write javadocs for method parameter
     * @param withSchemaPrefix Describe what the parameter does
     * @return The SqlName value
     */
    public String getSqlName(boolean withSchemaPrefix) {
        return withSchemaPrefix ? getSchemaPrefixedSqlName() : getSqlName();
    }

    /**
     * Gets the Name attribute of the Table object
     *
     * @return The Name value
     */
    public String getSchemaPrefixedSqlName() {
        boolean noschema = _schemaName == null || _schemaName.trim().equals("");
        String result;
        if (noschema) {
            result = getSqlName();
        } else {
            result = _schemaName + "." + getSqlName();
        }
        return result;
    }

    /**
     * Gets the SqlName attribute of the DbTable object
     *
     * @return The SqlName value
     */
    public String getSqlName() {
        return getTableElement().getPhysicalName();
    }

    /**
     * Gets the Name attribute of the DbTable object
     *
     * @return The Name value
     */
    public String getName() {
        return getTableElement().getName();
    }

    /**
     * Gets the enabled relationship roles
     *
     * @return The RelationCount value
     */
//    public Collection getRelationshipRoles() {
//        return _relationsipRoles;
//    }

    /**
     * Gets all the columns
     *
     * @return a list of all the columns
     */
    public Collection<Column> getColumns() {
        return Collections.unmodifiableCollection(_columns);
    }

    /**
     * Gets all the mandatory columns (columns that are not nullable)
     *
     * @return a list of all the mandatory columns
     */
    public final Collection getMandatoryColumns() {
        return getColumns(Mandatory.getInstance());
    }

    /**
     * Gets the PrimaryKeyColumns attribute of the DbTable object
     *
     * @return The PrimaryKeyColumns value
     */
    public Collection getPrimaryKeyColumns() {
        return getColumns(PrimaryKey.getInstance());
    }

    /**
     * Gets the RelationshipRoles attribute of the DbTable object
     *
     * @todo-javadoc Write javadocs for method parameter
     * @param predicate Describe what the parameter does
     * @return The RelationshipRoles value
     */
    //    public Collection getRelationshipRoles(Predicate predicate) {
    //        return CollectionUtils.select(getRelationshipRoles(), predicate);
    //    }

    /**
     * Returns the column that is a pk column. If zero or 2+ columns are pk
     * columns, null is returned.
     *
     * @return The PkColumn value
     */
    public Column getPkColumn() {
        Column pkColumn = null;
        Iterator i = _columns.iterator();
        while (i.hasNext()) {
            Column column = (Column) i.next();
            if (column.isPk()) {
                if (pkColumn != null) {
                    // There is more than one pk column
                    pkColumn = null;
                    break;
                }
                pkColumn = column;
            }
        }
        
        LogUtils.get().debug("Table " + getSqlName() + "'s unique pk column:" + pkColumn);
        
        return pkColumn;
    }

    /**
     * Gets the Index attribute of the DbTable object
     *
     * @todo-javadoc Write javadocs for method parameter
     * @param columnSqlName Describe what the parameter does
     * @return The Index value
     */
    public int getIndex(String columnSqlName) {
        return _columns.indexOf(getColumn(columnSqlName));
    }

    /**
     * Gets the Unique tuples for this Table object
     *
     * @return a Collection of Collections of Colunns. Each entry in the returned
     *      collection represents one or more columns which make up a unique key
     *      for the table. This can be used to generate more intellegent finder
     *      methods.
     */
    public Collection getUniqueTuples() {
        return _uniqueTuples;
    }

    /**
     * Gets the column with the specified name
     *
     * @todo we should really throw an ex instead of logging an error. has to do
     *      with m:n relationships
     * @param sqlName the name of the column in the database
     * @return the column with the specified name
     */
    public Column getColumn(String sqlName) {
        Column result = (Column) _columnSqlName2ColumnMap.get(sqlName.toLowerCase());
        if (result == null) {
            throw new IllegalArgumentException("There is no column named " + sqlName
                                               + " in the table named " + getSqlName());
        }
        return result;
    }

    /**
     * Describe the method
     *
     * @todo-javadoc Describe the method parameter
     * @todo-javadoc Describe the method
     * @todo-javadoc Describe the method parameter
     * @param relationshipRole Describe the method parameter
     */
//    public void addRelationshipRole(RelationshipRole relationshipRole) {
//        _relationsipRoles.add(relationshipRole);
//    }

    /**
     * Adds a feature to the Column attribute of the Table object
     *
     * @param column The feature to be added to the Column attribute
     */
    public void addColumn(Column column) {
        _columns.add(column);
        _columnSqlName2ColumnMap.put(column.getSqlName().toLowerCase(), column);
    }

    /**
     * Adds a unique tuple to this table
     *
     * @param uniqueTuple The unique tuple to be added to the table
     */
    public void addUniqueTuple(Collection uniqueTuple) {
        _uniqueTuples.add(uniqueTuple);
    }

    /**
     * Sorts the columns in the table
     *
     * @param comparator The comparator to use for the sorting
     */
    public void sortColumns(Comparator comparator) {
        Collections.sort(_columns, comparator);
    }

    /**
     * Describe what the method does
     *
     * @todo-javadoc Write javadocs for method
     * @todo-javadoc Write javadocs for method parameter
     * @todo-javadoc Write javadocs for return value
     * @param o Describe what the parameter does
     * @return Describe the return value
     */
    public boolean equals(Object o) {
        if (o instanceof Table) {
            Table other = (Table) o;
            return getSqlName().equals(other.getSqlName());
        } else {
            return false;
        }
    }

    /**
     * Describe what the method does
     *
     * @todo-javadoc Write javadocs for method
     * @todo-javadoc Write javadocs for return value
     * @return Describe the return value
     */
    public int hashCode() {
        return getSqlName().hashCode();
    }

    /**
     * Describe what the method does
     */
    protected final String prefsPrefix() {
        return "tables/" + getSqlName();
    }

    /**
     * Gets the PrefsPos attribute of the DbTable object
     *
     * @todo-javadoc Write javadocs for method parameter
     * @param coord Describe what the parameter does
     * @return The PrefsPos value
     */
    private int getPrefsPos(String coord) {
        String c = getPrefsValue(coord);
        if (c == null) {
            return Integer.MIN_VALUE;
        } else {
            return Integer.parseInt(c);
        }
    }

}
