/*
 * Copyright (c) 2001, Aslak Hellesy, BEKK Consulting
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
package middlegen.javax;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;

import middlegen.DbNameConverter;
import middlegen.Plugin;
import middlegen.Table;
import middlegen.TableDecorator;
import middlegen.Util;

import com.atom.dalgen.utils.LogUtils;

/**
 * Baseclass for Table decorators that map to java types.
 */
public class JavaTable extends TableDecorator {
    /**
     * @todo-javadoc Describe the column
     */
    private String _baseClassName;

    /**
     * @todo-javadoc Describe the field
     */
    private String _package;

    /**
     * @todo-javadoc Describe the field
     */
    private String _sequenceName = null;

    /**
     * Describe what the JavaTable constructor does
     */
    public JavaTable(Table subject) {
        super(subject);

        _sequenceName = getTableElement().getSequenceName();

        if (_sequenceName != null) {
            _sequenceName = _sequenceName.trim().toUpperCase();
        }

    }

    /**
     * Sets the Package attribute of the JavaTable object
     *
     * @todo-javadoc Write javadocs for return value
     * @param pakkage The new Package value
     * @return Describe the return value
     */
    public String setPackage(String pakkage) {
        return _package = pakkage;
    }

    /**
     * Sets the Plugin attribute of the JavaTable object
     *
     * @param plugin The new Plugin value
     */
    public void setPlugin(Plugin plugin) {
        super.setPlugin(plugin);
        JavaPlugin javaPlugin = (JavaPlugin) plugin;
        String packageName = javaPlugin.getPackage();
        if (packageName.indexOf("{0}") != -1) {
            // Parameterised package name. Replace {0} with lowercase table name.
            packageName = MessageFormat.format(packageName, new Object[] { getName().toLowerCase() });
        }
        setPackage(packageName);
    }

    /**
     * Gets the SequenceName attribute of the JavaTable object. If a sequence
     * name was not supplied the sequenceName will be assumed to be <tt>
     * &lt;SQL-tablename&gt; + "_SEQ"</tt> .
     *
     * @return The SequenceName value
     */
    public String getSequenceName() {
        if (_sequenceName == null || _sequenceName.equals("")) {
            _sequenceName = getSqlName().toUpperCase() + "_SEQ";
        }
        return _sequenceName;
    }

    /**
     * Gets the BaseClassName attribute of the JavaTable object
     *
     * @return The BaseClassName value
     */
    public String getBaseClassName() {
        return _baseClassName;
    }

    /**
     * Gets the DestinationClassName attribute of the JavaTable object
     *
     * @return The DestinationClassName value
     */
    public String getDestinationClassName() {
        return getBaseClassName();
    }

    /**
     * Gets the SimplePkClassName attribute of the Entity11Plugin object
     *
     * @todo-javadoc Write javadocs for method parameter
     * @return The SimplePkClassName value
     */
    public String getSimplePkClassName() {
        JavaColumn pkColumn = (JavaColumn) getPkColumn();
        if (pkColumn != null) {
            return pkColumn.getJavaType();
        } else {
            return null;
        }
    }

    /**
     * Gets the QualifiedBaseClassName attribute of the JavaTable object
     *
     * @return The QualifiedBaseClassName value
     */
    public String getQualifiedBaseClassName() {
        String pakkage = ((JavaPlugin) getPlugin()).getPackage();
        return Util.getQualifiedClassName(pakkage, getBaseClassName());
    }

    /**
     * @return the name of the sub directory of the original directory
     */
    public String getSubDirPath() {
        return getPackage().replace('.', '/');
    }

    /**
     * Gets the ClassName attribute of the JavaTable object
     *
     * @todo-javadoc Write javadocs for method parameter
     * @param relationshipRole Describe what the parameter does
     * @return The ClassName value
     */
    //    public String getClassName(RelationshipRole relationshipRole) {
    //        if (relationshipRole.getOrigin(getPlugin()) != this) {
    //            throw new IllegalArgumentException("The relationshipRole's origin must be "
    //                                               + getSqlName() + " , but was "
    //                                               + relationshipRole.getOrigin().getSqlName());
    //        }
    //        String result;
    //        JavaTable target = (JavaTable) relationshipRole.getTarget(getPlugin());
    //        if (relationshipRole.isTargetMany()) {
    //            result = target.getManyClassName();
    //        } else {
    //            result = target.getQualifiedDestinationClassName();
    //        }
    //        return result;
    //    }

    /**
     * Gets the Package attribute of the JavaTable object
     *
     * @return The Package value
     */
    public String getPackage() {
        return _package;
    }

    /**
     * Gets the Java signature corresponding to the columns.
     *
     * @param columns the columns to use in the signature
     * @return The Signature value
     * @includeType whether or not to put the type in the signature
     * @returns a String that can be used as a method/constructor signature
     */
    public String getSignature(Collection columns) {
        return delimit(columns, true);
    }

    /**
     * Gets the Java signature corresponding to the columns.
     *
     * @param columns the columns to use in the signature
     * @return The Signature value
     * @includeType whether or not to put the type in the signature
     * @returns a String that can be used as a method/constructor signature
     */
    public String getParameters(Collection columns) {
        return delimit(columns, false);
    }

    /**
     * Gets the ReplaceName attribute of the JavaTable object
     *
     * @return The ReplaceName value
     */
    public String getReplaceName() {
        return getBaseClassName();
    }

    /**
     * Gets the QualifiedDestinationClassName attribute of the JavaTable object
     *
     * @return The QualifiedDestinationClassName value
     */
    public String getQualifiedDestinationClassName() {
        return Util.getQualifiedClassName(getPackage(), getDestinationClassName());
    }

    /**
     * Gets the Java signature corresponding to the columns.
     *
     * @todo-javadoc Write javadocs for method parameter
     * @param columns the columns to use in the signature
     * @param includeType Describe what the parameter does
     * @return The Signature value
     * @includeType whether or not to put the type in the signature
     * @returns a String that can be used as a method/constructor signature or
     *      invocation parameters
     */
    public String delimit(Collection columns, boolean includeType) {
        StringBuffer sb = new StringBuffer();
        Iterator i = columns.iterator();
        while (i.hasNext()) {
            JavaColumn column = null;
            Object c = i.next();
            try {
                column = (JavaColumn) c;
            } catch (ClassCastException e) {
                throw new IllegalStateException(getPlugin().getClass().getName() + " must override getColumnDecoratorClass() and return a class which is " + JavaColumn.class.getName()
                                                + " (or a subclass). It was " + c.getClass().getName());
            }
            if (sb.length() != 0) {
                sb.append(", ");
            }
            if (includeType) {
                String columnType = column.getJavaType();
                sb.append(columnType).append(" ");
            }
            String columnName = column.getVariableName();
            sb.append(columnName);
        }
        return sb.toString();
    }

    /**
     * Sets the JavaType attribute of the Column object
     *
     * @param baseClassName The new BaseClassName value
     */
    protected void setBaseClassName(String baseClassName) {
        setPrefsValue("base-class-name", baseClassName);
        _baseClassName = baseClassName;
    }

    /**
     * Gets the ManyClassName attribute of the JavaTable object
     *
     * @return The ManyClassName value
     */
    protected String getManyClassName() {
        return "java.util.Collection";
    }

    /**
     * Describe what the method does
     *
     * @todo-javadoc Write javadocs for method
     */
    protected void init() {
        super.init();

        /*
         *  There are 3 ways to get the class name for a table. Attempts
         *  are done in the following order:
         *
         *  1) Look in prefs
         *  2) Use the singular name for the table (if specified)
         *  3) Use the DbNameConverter
         */
        String prefsBaseClassName = getPrefsValue("base-class-name");

        String computedBaseClassName;
        if (getTableElement().getSingular() != null) {
            computedBaseClassName = getTableElement().getSingular();
            computedBaseClassName = Util.capitalise(computedBaseClassName);
        } else {
            computedBaseClassName = DbNameConverter.getInstance().tableNameToVariableName(getName());
            computedBaseClassName = Util.singularise(computedBaseClassName);
        }
        String suffix = ((JavaPlugin) getPlugin()).getSuffix();
        computedBaseClassName += suffix;

        if (prefsBaseClassName != null && !prefsBaseClassName.equals(computedBaseClassName)) {
            LogUtils.log("WARNING (" + getPlugin().getName() + "): " + "Your prefs file indicates that the base class name for table " + getSqlName() + " should be " + prefsBaseClassName
                         + ", but according to your plugin settings " + "it should be " + computedBaseClassName + ". Middlegen will use " + prefsBaseClassName + ". "
                         + "If you want it to be the other way around, please edit or delete your prefs file, " + "or modify the name in the gui.");
        }

        if (prefsBaseClassName != null) {
            setBaseClassName(prefsBaseClassName);
        } else {
            setBaseClassName(computedBaseClassName);
        }
    }

}
