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
 * Subclasses of this class can store and retrieve values from preferences
 *
 * @author Aslak Helles
 * @created 15. april 2002
 * @todo-javadoc Write javadocs
 */
public abstract class PreferenceAware {

   /**
    * @todo-javadoc Describe the column
    */
   private Plugin _plugin;

   /**
    * @todo-javadoc Describe the column
    */
   private String _prefsPath;


   /**
    * Sets the Plugin attribute of the PreferenceAware object
    *
    * @param plugin The new Plugin value
    */
   public void setPlugin(Plugin plugin) {
      _plugin = plugin;
   }


   /**
    * Sets the PrefsValue attribute of the PreferenceAware object
    *
    * @param attributeName The new PrefsValue value
    * @param value The new PrefsValue value
    */
   protected void setPrefsValue(String attributeName, String value) {
      try {
         Prefs.getInstance().set(prefsPath(), attributeName, value);
      } catch (NoClassDefFoundError ignore) {
      }
   }


   /**
    * Gets the Plugin attribute of the PreferenceAware object. NB: This method
    * must not be called from the subclass' constructor, as it isn't set until
    * the {@link #setPlugin} method is called. If it must be called during
    * inititalisation, call it in the {@link #init} method
    *
    * @return The Plugin value
    */
   protected Plugin getPlugin() {
      return _plugin;
   }


   /**
    * Gets the PrefsValue attribute of the PreferenceAware object
    *
    * @todo-javadoc Write javadocs for method parameter
    * @param attributeName Describe what the parameter does
    * @return The PrefsValue value
    */
   protected String getPrefsValue(String attributeName) {
//		System.out.println(getClass().getName() + ".getPrefsValue(" + attributeName + ")");
      String value = null;
      try {
         value = Prefs.getInstance().get(prefsPath(), attributeName);
      } catch (NoClassDefFoundError ignore) {
      }
      return value;
   }


   /**
    * Describe what the method does
    *
    * @todo-javadoc Write javadocs for method
    * @todo-javadoc Write javadocs for return value
    * @return Describe the return value
    */
   protected abstract String prefsPrefix();


   /**
    * Describe what the method does
    *
    * @todo-javadoc Write javadocs for method
    */
   protected void init() {
      _prefsPath = prefsPrefix();
   }


   /**
    * Describe what the method does
    *
    * @todo-javadoc Write javadocs for method
    * @todo-javadoc Write javadocs for return value
    * @return Describe the return value
    */
   protected final String prefsPath() {
      if (_prefsPath == null) {
         throw new IllegalStateException("init hasn't been called yet!");
      }
      return _prefsPath;
   }
}
