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

/**
 * This class is used to tell Middlegen about an implied cross reference that's
 * not actually declared in the database.
 *
 * @author Kyle Downey
 * @author Aslak Helles
 * @created September 6, 2002
 */
public class CrossrefElement {

   /** */
   private String _name;
   /**
    * @todo-javadoc Describe the field
    */
   private String _fktable;
   /**
    * @todo-javadoc Describe the field
    */
   private String _pkcolumn;
   /**
    * @todo-javadoc Describe the field
    */
   private String _fkcolumn;


   /**
    * The name of the foreign key. This is _not_ the same as the foreign key
    * column. It's only needed when declaring relations that consist of multiple
    * foreign key columns
    *
    * @param name The new Name value
    */
   public void setName(String name) {
      _name = name;
   }


   /**
    * The SQL name of the primary key table
    *
    * @param fktable the SQL name of the primary key table
    */
   public void setFktable(String fktable) {
      _fktable = fktable;
   }


   /**
    * The SQL name of the foreign key column
    *
    * @param pkcolumn The new Pkcolumn value
    */
   public void setPkcolumn(String pkcolumn) {
      _pkcolumn = pkcolumn;
   }


   /**
    * The SQL name of the foreign key column
    *
    * @param fkcolumn The new Fkcolumn value
    */
   public void setFkcolumn(String fkcolumn) {
      _fkcolumn = fkcolumn;
   }


   /**
    * Gets the Name attribute of the CrossrefElement object
    *
    * @return The Name value
    */
   public String getName() {
      return _name;
   }


   /**
    * Gets the Fktable attribute of the CrossrefElement object
    *
    * @return The Fktable value
    */
   public String getFktable() {
      return _fktable;
   }


   /**
    * Gets the Pkcolumn attribute of the CrossrefElement object
    *
    * @return The Pkcolumn value
    */
   public String getPkcolumn() {
      return _pkcolumn;
   }


   /**
    * Gets the Fkcolumn attribute of the CrossrefElement object
    *
    * @return The Fkcolumn value
    */
   public String getFkcolumn() {
      return _fkcolumn;
   }

}
