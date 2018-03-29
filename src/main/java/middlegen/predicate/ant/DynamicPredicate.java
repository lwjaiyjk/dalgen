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
package middlegen.predicate.ant;

import middlegen.predicate.AttributedPredicate;
import middlegen.predicate.CompositePredicate;
import middlegen.predicate.PredicateException;
import middlegen.predicate.PredicateFactory;

import org.apache.commons.collections.Predicate;
import org.apache.tools.ant.DynamicConfigurator;

import com.atom.dalgen.utils.LogUtils;

/**
 * This class creates logic trees from Ant
 *
 * @author Aslak Helles
 * @created 21. august 2002
 */
public class DynamicPredicate implements DynamicConfigurator {

   /**
    * @todo-javadoc Describe the field
    */
   private AttributedPredicate _predicate;

   /**
    * @todo-javadoc Describe the field
    */
   private boolean _isRoot = false;

   /**
    * @todo-javadoc Describe the field
    */
   private final PredicateFactory _predicateFactory;

   /**
    * Describe what the DynamicPredicate constructor does
    */
   public DynamicPredicate(PredicateFactory predicateFactory) {
      _predicateFactory = predicateFactory;
   }


   /**
    * Sets the DynamicAttribute attribute of the DynamicPredicate object
    *
    * @param name The new DynamicAttribute value
    * @param value The new DynamicAttribute value
    */
   public void setDynamicAttribute(String name, String value) {
      _predicate.put(name, value);
   }


   /**
    * Gets the Predicate attribute of the DynamicPredicate object
    *
    * @return The Predicate value
    */
   public Predicate getPredicate() {
      return _predicate;
   }


   /**
    * Describe what the method does
    *
    * @todo-javadoc Write javadocs for method
    * @todo-javadoc Write javadocs for method parameter
    * @todo-javadoc Write javadocs for return value
    * @param name Describe what the parameter does
    * @return Describe the return value
    */
   public Object createDynamicElement(String name) {
      try {
         AttributedPredicate predicate = _predicateFactory.createPredicate(name);

         // Sanity check to avoid more than one root
         if (_isRoot && _predicate != null) {
            String msg = "Can only have one root predicate.";
            LogUtils.get().error(msg);
            throw new IllegalStateException(msg);
         }

         // First see if we have a _predicate. If not, we're at the root level,
         // and should accept only one child.
         if (_predicate == null) {
            // We're the root
            _isRoot = true;
            _predicate = predicate;
         }
         else {
            // Now verify that our _predicate accepts children
            if (_predicate instanceof CompositePredicate) {
               CompositePredicate compositePredicate = (CompositePredicate)_predicate;
               compositePredicate.add(predicate);
            }
            else {
               String msg = "Can't create a " + name + " element here.";
               LogUtils.get().error(msg);
               throw new IllegalStateException(msg);
            }
         }

         // Create a new DynamicPredicate wrapping the real subject and using the same factory
         DynamicPredicate child = new DynamicPredicate(_predicateFactory);
         child._predicate = predicate;
         return child;
      } catch (PredicateException e) {
         LogUtils.get().error(e.getMessage());
         throw new IllegalStateException(e.getMessage());
      }
   }
}
