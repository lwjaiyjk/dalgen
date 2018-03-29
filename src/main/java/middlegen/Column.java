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
 * A Column represents a column in a table. It can be decorated with
 * ColumnDecorator subclasses. (See GoF Decorator pattern).
 *
 * @author <a href="mailto:aslak.hellesoy@bekk.no">Aslak Helles</a>
 * @created 3. oktober 2001
 * @version $Id: Column.java,v 1.1 2005/10/25 14:59:22 lusu Exp $
 */
public interface Column {
   /**
    * Gets the Table attribute of the Column object
    *
    * @return The Table value
    */
   public Table getTable();


   /**
    * Gets the SqlType attribute of the Column object
    *
    * @return The SqlType value
    */
   public int getSqlType();


   /**
    * Gets the SqlTypeName attribute of the Column object
    *
    * @return The SqlTypeName value
    */
   public String getSqlTypeName();


   /**
    * Gets the SqlName attribute of the Column object
    *
    * @return The SqlName value
    */
   public String getSqlName();


   /**
    * Gets the Size attribute of the Column object
    *
    * @return The Size value
    */
   public int getSize();


   /**
    * Gets the DecimalDigits attribute of the Column object
    *
    * @return The DecimalDigits value
    */
   public int getDecimalDigits();


   /**
    * Gets the Pk attribute of the Column object
    *
    * @return The Pk value
    */
   public boolean isPk();


   /**
    * Gets the Fk attribute of the Column object
    *
    * @return The Fk value
    */
   public boolean isFk();


   /**
    * Gets the Nullable attribute of the Column object
    *
    * @return The Nullable value
    */
   public boolean isNullable();


   /**
    * Gets the Indexed attribute of the Column object
    *
    * @return The Indexed value
    */
   public boolean isIndexed();


   /**
    * Returns true if the column is unique
    *
    * @return true if the column is unique
    */
   public boolean isUnique();


   /**
    * Gets the DefaultValue attribute of the Column object
    *
    * @return The DefaultValue value
    */
   public String getDefaultValue();
   
   public String getRemark();
}

