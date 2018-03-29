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
package middlegen.util;

import java.util.Observable;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A BooleanNode can have the value true or false, giving additional information
 * on whether or not the true value is "uniform", that is if all sub nodes are
 * also true.
 *
 * @author Aslak Helles
 * @created 26. mars 2002
 * @task test haha
 */
public class BooleanNode extends Observable {
   /**
    * @todo-javadoc Describe the column
    */
   private boolean _value;
   /**
    * @todo-javadoc Describe the column
    */
   private final BooleanNode _parent;
   /**
    * @todo-javadoc Describe the column
    */
   private Collection _children;


   /**
    * Describe what the BooleanNode constructor does
    *
    * @todo-javadoc Write javadocs for method parameter
    * @todo-javadoc Write javadocs for constructor
    * @todo-javadoc Write javadocs for method parameter
    * @todo-javadoc Write javadocs for method parameter
    * @param parent Describe what the parameter does
    * @param initialValue Describe what the parameter does
    */
   private BooleanNode(BooleanNode parent, boolean initialValue) {
      _parent = parent;
      _value = initialValue;
   }


   /**
    * Sets the Value attribute of the BooleanNode object
    *
    * @param value The new Value value
    */
   public void setValue(boolean value) {
      if (!haveChildren()) {
         _value = value;
      }
      else {
         Iterator i = _children.iterator();
         while (i.hasNext()) {
            BooleanNode child = (BooleanNode)i.next();
            child.setValue(value);
            child.notifyChanged();
         }
      }
      if (_parent != null) {
         // Tell parent that we changed
         _parent.notifyChanged();
      }
      notifyChanged();
   }


   /**
    * Gets the CompletelyTrue attribute of the BooleanNode object
    *
    * @return The CompletelyTrue value
    */
   public boolean isCompletelyTrue() {
      return isTrue(true);
   }


   /**
    * Gets the PartiallyTrue attribute of the BooleanNode object
    *
    * @return The PartiallyTrue value
    */
   public boolean isPartiallyTrue() {
      return isTrue(false);
   }


   /**
    * Describe what the method does
    *
    * @todo-javadoc Write javadocs for method parameter
    * @todo-javadoc Write javadocs for method
    * @todo-javadoc Write javadocs for method parameter
    * @todo-javadoc Write javadocs for return value
    * @param initialValue Describe what the parameter does
    * @return Describe the return value
    */
   public BooleanNode createChild(boolean initialValue) {
      BooleanNode child = new BooleanNode(this, initialValue);
      if (_children == null) {
         _children = new ArrayList();
      }
      _children.add(child);
      notifyChanged();
      return child;
   }


   /**
    * Describe what the method does
    *
    * @todo-javadoc Write javadocs for method
    * @todo-javadoc Write javadocs for return value
    * @return Describe the return value
    */
   public String toString() {
      if (isCompletelyTrue()) {
         return "true";
      }
      else {
         if (isPartiallyTrue()) {
            return "fuzzy";
         }
         else {
            return "false";
         }
      }
   }


   /**
    * Gets the True attribute of the BooleanNode object
    *
    * @todo-javadoc Write javadocs for method parameter
    * @param andAllChildren Describe what the parameter does
    * @return The True value
    */
   private boolean isTrue(boolean andAllChildren) {
      boolean result = andAllChildren;
      if (!haveChildren()) {
         result = _value;
      }
      else {
         Iterator i = _children.iterator();
         boolean doLoop = true;
         while (i.hasNext() && doLoop) {
            BooleanNode child = (BooleanNode)i.next();
            boolean childValue = child.isTrue(andAllChildren);
            if (andAllChildren && !childValue) {
               result = false;
               doLoop = false;
            }
            else if (!andAllChildren && childValue) {
               result = true;
               doLoop = false;
            }
         }
      }
      return result;
   }


   /**
    * Describe what the method does
    *
    * @todo-javadoc Write javadocs for method
    * @todo-javadoc Write javadocs for return value
    * @return Describe the return value
    */
   private boolean haveChildren() {
      return _children != null && !_children.isEmpty();
   }


   /**
    * Describe what the method does
    *
    * @todo-javadoc Write javadocs for method
    */
   private void notifyChanged() {
      setChanged();
      notifyObservers();
      if (_parent != null) {
         _parent.notifyChanged();
      }
   }


   /**
    * Describe what the method does
    *
    * @todo-javadoc Write javadocs for method
    * @todo-javadoc Write javadocs for method parameter
    * @todo-javadoc Write javadocs for return value
    * @param initialValue Describe what the parameter does
    * @return Describe the return value
    */
   public static BooleanNode createRoot(boolean initialValue) {
      return new BooleanNode(null, initialValue);
   }
}
