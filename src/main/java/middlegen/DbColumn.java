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

import com.atom.dalgen.utils.LogUtils;

/**
 * Abstract implementation of Column
 *
 * @author <a href="mailto:aslak.hellesoy@bekk.no">Aslak Helles</a>
 * @created 3. oktober 2001
 * @version $Id: DbColumn.java,v 1.1 2005/10/25 14:59:22 lusu Exp $
 */
class DbColumn extends PreferenceAware implements Column {
    /** Reference to the containing table */
    private final Table   _table;

    /** The java.sql.Types type */
    private final int     _sqlType;

    /** The sql typename. provided by JDBC driver */
    private final String  _sqlTypeName;

    /** The name of the column */
    private final String  _sqlName;

    /** True if the column is a primary key */
    private boolean       _isPk;

    /** True if the column is a foreign key */
    private boolean       _isFk;
    /**
     * @todo-javadoc Describe the column
     */
    private final int     _size;
    /**
     * @todo-javadoc Describe the column
     */
    private final int     _decimalDigits;

    /** True if the column is nullable */
    private final boolean _isNullable;

    /** True if the column is indexed */
    private final boolean _isIndexed;

    /** True if the column is unique */
    private final boolean _isUnique;

    /** Null if the DB reports no default value */
    private final String  _defaultValue;

    /** ����ֶα�ע */
    private final String  remark;

    /**
     * Describe what the DbColumn constructor does
     */
    public DbColumn(Table table, int sqlType, String sqlTypeName, String sqlName, String remark, int size, int decimalDigits, boolean isPk, boolean isNullable, boolean isIndexed, boolean isUnique,
                    String defaultValue) {
        _table = table;
        _sqlType = sqlType;
        _sqlName = sqlName;
        this.remark = remark;
        _sqlTypeName = sqlTypeName;
        _size = size;
        _decimalDigits = decimalDigits;
        _isPk = isPk;
        _isNullable = isNullable;
        _isIndexed = isIndexed;
        _isUnique = isUnique;
        _defaultValue = defaultValue;

        LogUtils.get().debug(sqlName + " isPk -> " + _isPk);

        init();
    }

    /**
     * Gets the SqlType attribute of the Column object
     *
     * @return The SqlType value
     */
    public int getSqlType() {
        return _sqlType;
    }

    /**
     * Gets the Table attribute of the DbColumn object
     *
     * @return The Table value
     */
    public Table getTable() {
        return _table;
    }

    /**
     * Gets the Size attribute of the DbColumn object
     *
     * @return The Size value
     */
    public int getSize() {
        return _size;
    }

    /**
     * Gets the DecimalDigits attribute of the DbColumn object
     *
     * @return The DecimalDigits value
     */
    public int getDecimalDigits() {
        return _decimalDigits;
    }

    /**
     * Gets the SqlTypeName attribute of the Column object
     *
     * @return The SqlTypeName value
     */
    public String getSqlTypeName() {
        return _sqlTypeName;
    }

    /**
     * Gets the SqlName attribute of the Column object
     *
     * @return The SqlName value
     */
    public String getSqlName() {
        return _sqlName;
    }

    /**
     * Gets the Pk attribute of the Column object
     *
     * @return The Pk value
     */
    public boolean isPk() {
        return _isPk;
    }

    /**
     * Gets the Fk attribute of the Column object
     *
     * @return The Fk value
     */
    public boolean isFk() {
        return _isFk;
    }

    /**
     * Gets the Nullable attribute of the Column object
     *
     * @return The Nullable value
     */
    public final boolean isNullable() {
        return _isNullable;
    }

    /**
     * Gets the Indexed attribute of the DbColumn object
     *
     * @return The Indexed value
     */
    public final boolean isIndexed() {
        return _isIndexed;
    }

    /**
     * Gets the Unique attribute of the DbColumn object
     *
     * @return The Unique value
     */
    public boolean isUnique() {
        return _isUnique;
    }

    /**
     * Gets the DefaultValue attribute of the DbColumn object
     *
     * @return The DefaultValue value
     */
    public final String getDefaultValue() {
        return _defaultValue;
    }

    /**
     * Describe what the method does
     *
     * @todo-javadoc Write javadocs for method
     * @todo-javadoc Write javadocs for return value
     * @return Describe the return value
     */
    public int hashCode() {
        return (getTable().getSqlName() + "#" + getSqlName()).hashCode();
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
        // we can compare by identity, since there won't be dupes
        return this == o;
    }

    /**
     * Describe what the method does
     *
     * @todo-javadoc Write javadocs for method
     * @todo-javadoc Write javadocs for return value
     * @return Describe the return value
     */
    public String toString() {
        return getSqlName();
    }

    /**
     * Describe what the method does
     *
     * @todo-javadoc Write javadocs for method
     * @todo-javadoc Write javadocs for return value
     * @return Describe the return value
     */
    protected final String prefsPrefix() {
        return "tables/" + getTable().getSqlName() + "/columns/" + getSqlName();
    }

    /**
     * Sets the Pk attribute of the DbColumn object
     *
     * @param flag The new Pk value
     */
    void setFk(boolean flag) {
        _isFk = flag;
    }

    public String getRemark() {
        return remark;
    }
}
