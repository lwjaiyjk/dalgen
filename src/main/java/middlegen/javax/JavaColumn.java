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

import middlegen.Column;
import middlegen.ColumnDecorator;
import middlegen.DbNameConverter;
import middlegen.Util;

/**
 * Baseclass for Column decorators that map to java types.
 *
 * @author <a href="mailto:aslak.hellesoy@bekk.no">Aslak Helles</a>
 * @created 3. oktober 2001
 * @version $Id: JavaColumn.java,v 1.1 2005/10/25 14:59:22 lusu Exp $
 */
public class JavaColumn extends ColumnDecorator {
   /** The java type */
   private String _javaType;
   /** The java name */
   private String _variableName;


   /**
    * Describe what the JavaColumn constructor does
    *
    * @todo-javadoc Write javadocs for method parameter
    * @todo-javadoc Write javadocs for constructor
    * @todo-javadoc Write javadocs for method parameter
    * @param subject Describe what the parameter does
    */
   public JavaColumn(Column subject) {
      super(subject);
//		System.out.println("JavaColumn() subject = " + subject.getSqlName());
   }


   /**
    * Sets the Java type
    *
    * @param javaType The new value
    */
   public void setJavaType(String javaType) {
      if (javaType == null) {
         throw new IllegalStateException("BUG: javaType can't be null. ");
      }
      
      setPrefsValue("java-type", javaType);
      _javaType = javaType;
   }


   /**
    * Sets the VariableName attribute of the JavaColumn object
    *
    * @param variableName The new VariableName value
    */
   public void setVariableName(String variableName) {
      setPrefsValue("java-name", variableName);
      _variableName = variableName;
   }


   /**
    * Gets the JavaType attribute of the JavaColumn object
    *
    * @return The JavaType value
    */
   public String getJavaType() {
      return _javaType;
   }


   /**
    * Gets the VariableName attribute of the JavaColumn object
    *
    * @return The VariableName value
    */
   public String getVariableName() {
      return _variableName;
   }


   /**
    * Gets the CapitalisedVariableName attribute of the JavaColumn object
    *
    * @return The CapitalisedVariableName value
    */
   public String getCapitalisedVariableName() {
      return Util.capitalise(getVariableName());
   }


   /**
    * Gets the GetterName attribute of the JavaColumn object
    *
    * @return The GetterName value
    */
   public String getGetterName() {
      return "get" + getCapitalisedVariableName();
   }


   /**
    * Gets the SetterName attribute of the JavaColumn object
    *
    * @return The SetterName value
    */
   public String getSetterName() {
      return "set" + getCapitalisedVariableName();
   }


   /**
    * Gets the PrimitiveOrComparable attribute of the JavaColumn object
    *
    * @return The PrimitiveOrComparable value
    */
   public boolean isPrimitiveOrComparable() {
      boolean result = false;
      try {
         Class clazz = Class.forName(getJavaType());
         // It's a class. Let's see if it's Comparable
         if (Comparable.class.isAssignableFrom(clazz)) {
            // yup, it's Comparable
            result = true;
         }
      } catch (ClassNotFoundException e) {
         result = isPrimitive();
      }
      return result;
   }


   /**
    * Returns true if the column is mapped to a Java primitive
    *
    * @return The Primitive value
    */
   public boolean isPrimitive() {
      return Sql2Java.isPrimitive(getJavaType());
   }


   /**
    * Returns true if the column is a mapped to a numeric class type
    *
    * @todo-javadoc Write javadocs for method parameter
    * @return The Primitive value
    */
   public boolean isNumericClass() {
      return Sql2Java.isNumericClass(getJavaType());
   }


   /**
    * Returns the corresponding java class for the primitive type of this
    * column. If this column's java type is not a primitive, a runtime exception
    * will be thrown.
    *
    * @return The ClassForPrimitive value
    */
   public String getClassForPrimitive() {
      return Sql2Java.getClassForPrimitive(getJavaType());
   }


   /** Sets the java type. This method can be overridden. */
   protected void setJavaType() {
      if (getPrefsValue("java-type") != null) {
         setJavaType(getPrefsValue("java-type"));
      }
      else {
         setJavaType(Sql2Java.getPreferredJavaType(getSqlType(), getSize(), getDecimalDigits()));
      }
   }

   /**
    * Describe what the method does
    *
    * @todo-javadoc Write javadocs for method
    */
   protected void init() {
      super.init();
      setJavaType();
//		System.out.println(this.toString() + " JavaColumn.init()");
      if (getPrefsValue("java-name") != null) {
         setVariableName(getPrefsValue("java-name"));
      }
      else {
         setVariableName(Util.decapitalise(DbNameConverter.getInstance().columnNameToVariableName(getSqlName())));
      }
   }
}

