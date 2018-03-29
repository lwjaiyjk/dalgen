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

import java.text.MessageFormat;

import middlegen.predicates.column.PrimaryKey;

import com.atom.dalgen.utils.LogUtils;

/**
 * @author <a href="aslak.hellesoy@netcom.no">Aslak Helles</a>
 * @created 21. mars 2002
 * @todo extends PreferenceAware so we can store directionality/cardinality in
 *      prefs (or use aspectj!!)
 */
public class RelationshipRole extends PreferenceAware {

   /** The relation we're a part of */
   private Relation _relation;

   /**
    * @todo-javadoc Describe the field
    */
   private RelationshipRole _targetRole;
   /**
    * @todo-javadoc Describe the field
    */
   private final Table _origin;
   /**
    * @todo-javadoc Describe the field
    */
   private final Table _target;

   /**
    * @todo-javadoc Describe the column
    */
   private boolean _isEnabled;

   /**
    * @todo-javadoc Describe the field
    */
   private boolean _isTargetMany;

   /**
    * This column is null, unless we're the fk side of one of several relations
    * between two tables. In that case, it will have a value that can be used to
    * generate different names
    */
   private final String _suffix;

   /**
    * @todo-javadoc Describe the column
    */
   private ColumnMap[] _columnMaps;

   /**
    * @todo-javadoc Describe the field
    */
   private boolean _isFkPk;

   /**
    * @todo-javadoc Describe the field
    */
   private String _namePattern;

   /**
    * Describe what the RelationshipRole constructor does
    */
   public RelationshipRole(Table origin, Table target, ColumnMap[] columnMaps, Relation relation, String suffix) {
      if (suffix == null) {
         throw new IllegalArgumentException("suffix can't be null!");
      }
      if (relation == null) {
         throw new IllegalArgumentException("relation can't be null!");
      }

      _origin = origin;
      _target = target;
      _columnMaps = columnMaps;
      _relation = relation;
      _suffix = suffix;

      if (getOrigin() == getTarget()) {
         // Avoid equal names on both sides. This is a bit fragile; See Relation's constructor
         if (getRelation().getLeftRole() == null) {
            _namePattern = "{0}-1-has-{1}-2" + _suffix;
         }
         else {
            _namePattern = "{0}-2-has-{1}-1" + _suffix;
         }
      }
      else {
         _namePattern = "{0}-has-{1}" + _suffix;
      }

      init();
      // Set multiplicity
      if (relation.getJoinTable() == null) {
         //setLeftMany(false);

         if (columnMaps.length != 0) {
            // Verify that the ref is "well formed" and warn if it isn't
            int pkSidePkCount = _origin.getColumns(PrimaryKey.getInstance()).size();
            if (pkSidePkCount != _columnMaps.length) {
                    LogUtils.get().warn("WARNING: The " + _origin.getSqlName() + " table's primary key consists of " +
                     pkSidePkCount + " columns, but one of the relationships uses " + _columnMaps.length +
                     " foreign keys. That is not a well-defined relationships, as all columns in a primary key " +
                     "(and only primary key columns) should be referenced by a foreign key (all columns in the foreign key)."
                     );
            }
            // The target side is many unless the foreign key is also the primary key.
            // The foreign/primary key may consist of several columns, so in order to
            // determine that, we need to check that all the foreign key columns are
            // primary key columns, and also check that the number of foreign key columns
            // (length of the column map) equals the number of primary key columns.

            int fkSidePkCount = _target.getColumns(PrimaryKey.getInstance()).size();
            if (fkSidePkCount != _columnMaps.length) {
               _isFkPk = false;
            }
            else {
               // The number is equal. Now just verify that all the "pk columns" really are
               // pk columns. Assume they are, and set to false if not.
               _isFkPk = true;
               for (int i = 0; i < _columnMaps.length; i++) {
                  Column fkColumn = _target.getColumn(_columnMaps[i].getForeignKey());
                  if (!fkColumn.isPk()) {
                     _isFkPk = false;
                     break;
                  }
               }
            }

            String targetManyString = getPrefsValue("target-many");
            if (targetManyString != null) {
               setTargetMany(Util.bool(targetManyString));
            }
            else {
               setTargetMany(!_isFkPk);
            }
         }
         else {
            setTargetMany(false);
         }
      }
      else {
         // many-to-many since the join table is specified
         setTargetMany(true);
      }

      String enabledString = getPrefsValue("enabled");
      if (enabledString != null) {
         setEnabled(Util.bool(enabledString));
      }
      else {
         setEnabled(true);
      }
   }


   /**
    * Sets the Enabled attribute of the RelationshipRole object
    *
    * @param flag The new Enabled value
    */
   public void setEnabled(boolean flag) {
      setPrefsValue("enabled", Util.string(flag));
      _isEnabled = flag;
   }


   /**
    * Sets the TargetMany attribute of the RelationshipRole object
    *
    * @param flag The new TargetMany value
    */
   public void setTargetMany(boolean flag) {
      setPrefsValue("target-many", Util.string(flag));
      _isTargetMany = flag;
   }


   /**
    * Gets the FkPk attribute of the RelationshipRole object
    *
    * @return The FkPk value
    */
   public boolean isFkPk() {
      return _isFkPk;
   }


   /**
    * Gets the Suffix attribute of the RelationshipRole object
    *
    * @return The Suffix value
    */
   public String getSuffix() {
      return _suffix;
   }


   /**
    * Gets the plugin-specific name of the role. The plugin will provide a
    * String for each table name, used to build the name of the relation
    *
    * @param plugin the plugin that does the name conversion of the tables
    * @return the plugin-specific name
    */
    //   public String getName(Plugin plugin) {
    //      String originName = plugin.getRelationName(_origin);
    //      String targetName = plugin.getRelationName(_target);
    //      String name = MessageFormat.format(_namePattern, new String[]{originName, targetName});
    //      return name;
    //   }


   /**
    * Gets the general name of the relation.
    *
    * @return The Name value
    */
   public String getName() {
      String name = MessageFormat.format(_namePattern, new String[]{_origin.getSqlName(), _target.getSqlName()});
      return name;
   }


   /**
    * Gets the TargetRole attribute of the RelationshipRole object
    *
    * @return The TargetRole value
    */
   public RelationshipRole getTargetRole() {
      return _targetRole;
   }


   /**
    * Gets the Enabled attribute of the RelationshipRole object
    *
    * @return The Enabled value
    */
   public boolean isEnabled() {
      return _isEnabled && getRelation().isBothTablesGenerate();
   }


   /**
    * Gets the OriginMany attribute of the RelationshipRole object
    *
    * @return The OriginMany value
    */
   public boolean isOriginMany() {
      return getTargetRole().isTargetMany();
   }


   /**
    * Gets the TargetMany attribute of the RelationshipRole object
    *
    * @return The TargetMany value
    */
   public boolean isTargetMany() {
      return _isTargetMany;
   }


   /**
    * Gets the OriginPrimaryKey attribute of the RelationshipRole object
    *
    * @return The OriginPrimaryKey value
    */
   public boolean isOriginPrimaryKey() {
      return getColumnMaps().length != 0;
   }


   /**
    * Gets the TargetPrimaryKey attribute of the RelationshipRole object
    *
    * @return The TargetPrimaryKey value
    */
   public boolean isTargetPrimaryKey() {
      return getTargetRole().getColumnMaps().length != 0;
   }


   /**
    * Gets the ColumnMaps attribute of the RelationshipRole object
    *
    * @return The ColumnMaps value
    */
   public ColumnMap[] getColumnMaps() {
      return _columnMaps;
   }


   /**
    * Gets the origin table decorated by the plugin
    *
    * @param plugin the plugin used to decorate
    * @return The decorated origin table
    */
   public Table getOrigin(Plugin plugin) {
      return plugin.decorate(getOrigin());
   }


   /**
    * Gets the target table decorated by the plugin
    *
    * @param plugin the plugin used to decorate
    * @return The decorated target table
    */
   public Table getTarget(Plugin plugin) {
      return plugin.decorate(getTarget());
   }


   /**
    * Gets the Relation attribute of the RelationshipRole object
    *
    * @return The Relation value
    */
   public Relation getRelation() {
      return _relation;
   }


   /**
    * Gets the Origin attribute of the RelationshipRole object
    *
    * @return The Origin value
    */
   public Table getOrigin() {
      return _origin;
   }


   /**
    * Gets the Target attribute of the RelationshipRole object
    *
    * @return The Target value
    */
   public Table getTarget() {
      return _target;
   }


   /**
    * A relationhsipRole is mandatory if it belongs to the fk side and at least
    * one of the fk columns can't be null
    *
    * @return The Mandatory value
    */
   public boolean isMandatory() {
      if (isOriginPrimaryKey()) {
         // If we're on the primary key side we can't be madatory.
         return false;
      }
      else {
         ColumnMap[] columnMaps = getTargetRole().getColumnMaps();
         Table table = getTargetRole().getTarget();
         boolean result = false;
         for (int i = 0; i < columnMaps.length; i++) {
            Column column = table.getColumn(columnMaps[i].getForeignKey());
            if (!column.isNullable()) {
               result = true;
               break;
            }
         }
         return result;
      }
   }


   /**
    * Describe what the method does
    *
    * @todo-javadoc Write javadocs for method
    * @todo-javadoc Write javadocs for return value
    * @return Describe the return value
    */
   protected String prefsPrefix() {
      return "relations/" + getRelation().getName() + "/" + getName();
   }


   /**
    * Sets the TargetRole attribute of the RelationshipRole object
    *
    * @param targetRole The new TargetRole value
    */
   void setTargetRole(RelationshipRole targetRole) {
      _targetRole = targetRole;
   }

}
