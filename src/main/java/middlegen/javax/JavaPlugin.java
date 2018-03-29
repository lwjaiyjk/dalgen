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

import middlegen.*;

/**
 * Base class for all Plugins that generate java classes
 *
 * @author <a href="mailto:aslak.hellesoy@bekk.no">Aslak Helles</a>
 * @created 3. april 2002
 */
public class JavaPlugin extends Plugin {
   /**
    * @todo-javadoc Describe the column
    */
   private String _pakkage;

   /**
    * @todo-javadoc Describe the field
    */
   private String _suffix = "";


   /**
    * Describe what the JavaPlugin constructor does
    *
    * @todo-javadoc Write javadocs for method parameter
    * @todo-javadoc Write javadocs for constructor
    * @todo-javadoc Write javadocs for method parameter
    */
   public JavaPlugin() {
      super();
   }


   /**
    * Sets a string to append to each class name. Example: <p>
    *
    * suffix="Bar" <p>
    *
    * will generate a class named FooBar for a table named foo.
    *
    * @param suffix The new Suffix value
    * @ant.required No,defaultis""
    *
    */
   public void setSuffix(String suffix) {
      _suffix = suffix;
   }


   /**
    * Sets the Package attribute of the JavaPlugin object
    *
    * @param pakkage The new Package value
    */
   public final void setPackage(String pakkage) {
      if (pakkage.trim().equals("")) {
         throw new IllegalArgumentException("Can't use empty String for package!");
      }
      if (!allowsPackageSubstitution() && pakkage.indexOf("{0}") != -1) {
         throw new IllegalArgumentException("Sorry, can't use {0} in the " +
               "package name for " + getName() + " plugin, because some global classes will be " +
               "generated, and I wouldn't know what to substitute the {0} with.");
      }
      _pakkage = pakkage;
   }


   /**
    * Gets the Suffix attribute of the JavaPlugin object
    *
    * @return The Suffix value
    */
   public String getSuffix() {
      return _suffix;
   }


   /**
    * Gets the ColumnDecoratorClass attribute of the JavaPlugin object
    *
    * @return The ColumnDecoratorClass value
    */
   public Class getColumnDecoratorClass() {
      return JavaColumn.class;
   }


   /**
    * Gets the TableDecoratorClass attribute of the JavaPlugin object
    *
    * @return The TableDecoratorClass value
    */
   public Class getTableDecoratorClass() {
      return JavaTable.class;
   }


   /**
    * Gets the Package attribute of the JavaPlugin object
    *
    * @return The Package value
    */
   public final String getPackage() {
      return _pakkage;
   }


   /**
    * Describe what the method does
    *
    * @todo-javadoc Write javadocs for method
    * @todo-javadoc Write javadocs for exception
    * @exception MiddlegenException Describe the exception
    */
   public void validate() throws MiddlegenException {
      super.validate();
      if (getPackage() == null) {
         throw new MiddlegenException("package is not specified for plugin " + getDisplayName());
      }
   }


   /**
    * Describe what the method does
    *
    * @todo-javadoc Write javadocs for method
    * @todo-javadoc Write javadocs for return value
    * @return Describe the return value
    */
   protected boolean allowsPackageSubstitution() {
      return true;
   }
}
