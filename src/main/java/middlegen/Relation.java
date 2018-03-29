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

import com.atom.dalgen.utils.LogUtils;

/**
 * This class represents a relation between two tables. If the relation is 1:1
 * or 1:n, the left table should always be the 1 side with the primary key.
 *
 * @author <a href="aslak.hellesoy@netcom.no">Aslak Helles</a>
 * @created 28. mars 2002
 */
public class Relation {

   /**
    * @todo-javadoc Describe the field
    */
   private final String _namePattern;

   /** The origin table */
   private Table _leftTable;

   /** The target table */
   private Table _rightTable;

   /** The join table. It's null unless it's an m:n relation */
   private Table _joinTable;

   /** The left part of the relationship. Never null. */
   private RelationshipRole _leftRole;

   /** The right part of the relationship. Null if unidirectional. */
   private RelationshipRole _rightRole;

   /**
    * @todo-javadoc Describe the field
    */
   private String _relationSuffix;
   /**
    * @todo-javadoc Describe the field
    */
   private String _fkRoleSuffix;

   /**
    * Constructor
    *
    * @todo-javadoc Write javadocs for method parameter
    * @param leftTable the left table
    * @param leftColumnMaps Describe what the parameter does
    * @param rightTable the right table
    * @param rightColumnMaps Describe what the parameter does
    * @param joinTable Describe what the parameter does
    * @param relationSuffix Describe what the parameter does
    * @param fkRoleSuffix Describe what the parameter does
    */
   public Relation(
         Table leftTable,
         ColumnMap[] leftColumnMaps,
         Table rightTable,
         ColumnMap[] rightColumnMaps,
         Table joinTable,
         String relationSuffix,
         String fkRoleSuffix
         ) {
      if (leftTable == null) {
         throw new IllegalArgumentException("leftTable can't be null!");
      }
      if (rightTable == null) {
         throw new IllegalArgumentException("rightTable can't be null!");
      }
      if (leftTable.getClass() != DbTable.class) {
         throw new IllegalArgumentException("leftTable must be of class " + DbTable.class.getName());
      }
      if (rightTable.getClass() != DbTable.class) {
         throw new IllegalArgumentException("rightTable must be of class " + DbTable.class.getName());
      }
      LogUtils.get().debug("new Relation:" + leftTable.getSqlName() + "," + rightTable.getSqlName() + ":" + relationSuffix + ":" + fkRoleSuffix);

      _leftTable = leftTable;
      _rightTable = rightTable;
      _joinTable = joinTable;
      _relationSuffix = relationSuffix;
      _fkRoleSuffix = fkRoleSuffix;
      _namePattern = "{0}-{1}" + relationSuffix;

      // Make left and right role
      // We use the {0} so this String can be replaced with something else if desired
      //String leftRoleName = (leftTable.getSqlName() + "{0}-has-" + rightTable.getSqlName() + "{1}").toLowerCase();
      //String rightRoleName = (rightTable.getSqlName() + "{0}-has-" + leftTable.getSqlName() + "{0}").toLowerCase();

      _leftRole = new RelationshipRole(leftTable, rightTable, leftColumnMaps, this, fkRoleSuffix);
      _rightRole = new RelationshipRole(rightTable, leftTable, rightColumnMaps, this, fkRoleSuffix);
      _leftRole.setTargetRole(_rightRole);
      _rightRole.setTargetRole(_leftRole);

      // Add the two roles to each of the tables in the relation
        //      _leftTable.addRelationshipRole(_leftRole);
        //      _rightTable.addRelationshipRole(_rightRole);
   }


   /**
    * Gets the BothTablesGenerate attribute of the Relation object
    *
    * @return The BothTablesGenerate value
    */
   public boolean isBothTablesGenerate() {
      return _leftTable.getTableElement().isGenerate() && _rightTable.getTableElement().isGenerate();
   }


   /**
    * Gets the One2One attribute of the Relation object
    *
    * @return The One2One value
    */
   public boolean isOne2One() {
      return !_leftRole.isTargetMany() && !_rightRole.isTargetMany();
   }


   /**
    * Gets the Name attribute of the Relation object
    *
    * @todo-javadoc Write javadocs for method parameter
    * @param plugin Describe what the parameter does
    * @return The Name value
    */
    //   public String getName(Plugin plugin) {
    //      String leftName = plugin.getRelationName(_leftTable);
    //      String rightName = plugin.getRelationName(_rightTable);
    //      String name = MessageFormat.format(_namePattern, new String[]{leftName, rightName});
    //      return name;
    //   }


   /**
    * Gets the plugin-specific name of the role. The plugin will provide a
    * String for each table name, used to build the name of the relation
    *
    * @return the plugin-specific name
    */
   public String getName() {
      String name = MessageFormat.format(_namePattern, new String[]{_leftTable.getSqlName(), _rightTable.getSqlName()});
      return name;
   }


   /**
    * Gets the LeftTable attribute of the Relation object
    *
    * @return The LeftTable value
    */
   public Table getLeftTable() {
      return _leftTable;
   }


   /**
    * Gets the left table decorated by the plugin
    *
    * @param plugin the plugin used to decorate
    * @return The decorated left table
    */
   public Table getLeft(Plugin plugin) {
      return plugin.decorate(getLeftTable());
   }


   /**
    * Gets the right table decorated by the plugin
    *
    * @param plugin the plugin used to decorate
    * @return The decorated right table
    */
   public Table getRight(Plugin plugin) {
      return plugin.decorate(getRightTable());
   }


   /**
    * Gets the RightTable attribute of the Relation object
    *
    * @return The RightTable value
    */
   public Table getRightTable() {
      return _rightTable;
   }


   /**
    * Gets the JoinTable attribute of the Relation object
    *
    * @return The JoinTable value
    */
   public Table getJoinTable() {
      return _joinTable;
   }


   /**
    * Gets the Bidirectional attribute of the Relation object
    *
    * @return The Bidirectional value
    */
   public boolean isBidirectional() {
      return _leftRole.isEnabled() && _rightRole.isEnabled();
   }


   /**
    * Gets the Many2Many attribute of the Relation object
    *
    * @return The Many2Many value
    */
   public boolean isMany2Many() {
      return getJoinTable() != null;
   }


   /**
    * Gets the LeftRole attribute of the Relation object
    *
    * @return The LeftRole value
    */
   public RelationshipRole getLeftRole() {
      return _leftRole;
   }


   /**
    * Gets the RightRole attribute of the Relation object
    *
    * @return The RightRole value
    */
   public RelationshipRole getRightRole() {
      return _rightRole;
   }


   /**
    * Gets the RelationSuffix attribute of the Relation object
    *
    * @return The RelationSuffix value
    */
   String getRelationSuffix() {
      return _relationSuffix;
   }


   /**
    * Gets the FkRoleSuffix attribute of the Relation object
    *
    * @return The FkRoleSuffix value
    */
   String getFkRoleSuffix() {
      return _fkRoleSuffix;
   }
}
