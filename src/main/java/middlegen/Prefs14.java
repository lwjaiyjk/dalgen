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

import java.util.prefs.*;
import java.io.*;

/**
 * Wrapper around java.util.prefs.Preferences which simplifies the API and makes
 * it easier to handle NoClassDefFoundError in non JDK 1.4 environments
 *
 * @author Aslak Helles
 * @created 22. mars 2002
 * @todo Make use of registry prefs optional
 */
public class Prefs14 extends Prefs {
   /**
    * @todo-javadoc Describe the column
    */
   private Preferences _prefs;
   /**
    * @todo-javadoc Describe the column
    */
   private File _prefsFile;


   /**
    * Describe what the method does
    *
    * @todo-javadoc Write javadocs for method
    * @todo-javadoc Write javadocs for method parameter
    * @todo-javadoc Write javadocs for method parameter
    * @todo-javadoc Write javadocs for method parameter
    * @param pathName Describe what the parameter does
    * @param key Describe what the parameter does
    * @param value Describe what the parameter does
    */
   public void set(String pathName, String key, String value) {
      Preferences node = _prefs.node(pathName);
      node.put(key, value);
   }


   /**
    * Gets a value and saves prefs if it wasn't found and defaultValue != null
    *
    * @todo-javadoc Write javadocs for method parameter
    * @todo-javadoc Write javadocs for method parameter
    * @todo-javadoc Write javadocs for method parameter
    * @todo-javadoc Write javadocs for return value
    * @param pathName Describe what the parameter does
    * @param key Describe what the parameter does
    * @return Describe the return value
    */
   public String get(String pathName, String key) {
      String result = null;
      Preferences node = _prefs.node(pathName);
      result = node.get(key, null);
      return result;
   }


   /**
    * Describe what the method does
    *
    * @todo-javadoc Write javadocs for method
    */
   public void save() {
      try {
         _prefsFile.getParentFile().mkdirs();
         _prefs.exportSubtree(new FileOutputStream(_prefsFile));
         System.out.println("Updated preferences in " + _prefsFile.getAbsolutePath());
      } catch (Exception e) {
         System.out.println("Couldn't save preferences to " + _prefsFile.getAbsolutePath() + ":" + e.getMessage());
      }
   }


   /**
    * Describe what the method does
    *
    * @todo-javadoc Write javadocs for method
    * @todo-javadoc Write javadocs for method parameter
    * @todo-javadoc Write javadocs for method parameter
    * @todo-javadoc Write javadocs for exception
    * @param prefsDir Describe what the parameter does
    * @param prefsId Describe what the parameter does
    * @exception MiddlegenException Describe the exception
    */
   public void init(File prefsDir, String prefsId) throws MiddlegenException {
      _prefsFile = new File(prefsDir, prefsId + "-prefs.xml");
      try {
         // load preferences from registry
         _prefs = Preferences.userRoot().node("middlegen/" + prefsId);

         if (_prefsFile.exists()) {
            try {
               FileInputStream in = new FileInputStream(_prefsFile);
               _prefs.importPreferences(in);
            } catch (IOException e) {
               System.out.println("No preferences file found at " + _prefsFile.getAbsolutePath());
            } catch (InvalidPreferencesFormatException e) {
               System.out.println("Bad preferences format: " + _prefsFile.getAbsolutePath());
            } catch (ClassCastException e) {
               e.printStackTrace();
               System.out.println();
               System.out.println("WARNING: Couldn't read preferences!");
               System.out.println("It seems you have an XML parser on your classpath that interferes with the XML parser in JDK 1.4. Please remove that XML parser from your classpath.");
               System.out.println("Chances are you have ANT_HOME/lib/xercesImpl.jar. Try to delete that file.");
            }
         }
         else {
            try {
               // remove prefs if the file doesn't exist
               _prefs.removeNode();
               _prefs = Preferences.userRoot().node("middlegen/" + prefsId);
            } catch (BackingStoreException e) {
               System.out.println("Couldn't reset preferences: " + _prefsFile.getAbsolutePath());
            } catch (IllegalStateException e) {
               // ignore. Only means that parent didn't exist
            }
         }
      } catch (Throwable t) {
         t.printStackTrace();
         throw new MiddlegenException("Couldn't initialise preferences system:" + t.getMessage());
      }
   }
}
