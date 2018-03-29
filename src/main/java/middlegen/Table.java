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

import java.util.Collection;

import org.apache.commons.collections.Predicate;

/**
 * This class represents a table in a database
 *
 * @author Aslak Helles
 * @created 3. oktober 2001
 * @todo-javadoc Write javadocs
 */
public interface Table {

   /**
    * Gets the Name attribute of the Table object
    *
    * @return The Name value
    */
   public String getName();


   /**
    * Gets the SqlName attribute of the Table object
    *
    * @return The SqlName value
    */
   public String getSqlName();


   /**
    * Gets the SqlName attribute of the Table object
    *
    * @todo-javadoc Write javadocs for method parameter
    * @param withSchemaPrefix Describe what the parameter does
    * @return The SqlName value
    */
   public String getSqlName(boolean withSchemaPrefix);


   /**
    * Gets the SchemaPrefixedSqlName attribute of the Table object
    *
    * @return The SchemaPrefixedSqlName value
    */
   public String getSchemaPrefixedSqlName();


   /**
    * Returns the table element which holds the name of the table (or sequence)
    * that is used for primary key generation.
    *
    * @return The PkTableSqlName value
    */
   public TableElement getTableElement();


   /**
    * Gets the RelationshipRoles attribute of the Table object
    *
    * @return The RelationshipRoles value
    */
   // public Collection getRelationshipRoles();


   /**
    * Gets all the columns
    *
    * @return a list of all the columns
    */
   public Collection getColumns();


   /**
    * Gets the Columns attribute of the Table object
    *
    * @todo-javadoc Write javadocs for method parameter
    * @todo-javadoc Write javadocs for method parameter
    * @param predicate Describe what the parameter does
    * @return The Columns value
    */
   public Collection getColumns(Predicate predicate);


   /**
    * Gets the column with the specified name
    *
    * @param sqlName the name of the column in the database
    * @return the column with the specified name
    */
   public Column getColumn(String sqlName);


   /**
    * Gets all the mandatory columns (columns that are not nullable)
    *
    * @return a list of all the mandatory columns
    */
   public Collection getMandatoryColumns();


   /**
    * Returns the column that is a pk column. If zero or 2+ columns are pk
    * columns, null is returned.
    *
    * @return The PkColumn value
    */
   public Column getPkColumn();


   /**
    * Gets the Index attribute of the Table object
    *
    * @todo-javadoc Write javadocs for method parameter
    * @param columnSqlName Describe what the parameter does
    * @return The Index value
    */
   public int getIndex(String columnSqlName);


   /**
    * Gets the Unique tuples for this Table object
    *
    * @return a Collection of Collections of Colunns. Each entry in the returned
    *      collection represents one or more columns which make up a unique key
    *      for the table. This can be used to generate more intelligent finder
    *      methods.
    */
   public Collection getUniqueTuples();


   /**
    * Describe the method
    *
    * @todo-javadoc Describe the method
    * @todo-javadoc Describe the method parameter
    * @param relatinshipRole Describe the method parameter
    */
   // public void addRelationshipRole(RelationshipRole relatinshipRole);
}
