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
package middlegen.predicate;

import java.util.Map;
import java.util.HashMap;
/**
 * Simple predicate factory. Should be subclassed to add domain-specific
 * predicates
 *
 * @author Aslak Helles
 * @created 21. august 2002
 */
public class SimplePredicateFactory implements PredicateFactory {
   /**
    * @todo-javadoc Describe the field
    */
   private final Map _predicates = new HashMap();


   /**
    * Describe what the SimplePredicateFactory constructor does
    *
    * @todo-javadoc Write javadocs for constructor
    */
   public SimplePredicateFactory() {
      register("and", And.class);
      register("or", Or.class);
      register("not", Not.class);
      register("true", True.class);
      register("false", False.class);
   }


   /**
    * Describe what the method does
    *
    * @todo-javadoc Write javadocs for method
    * @todo-javadoc Write javadocs for method parameter
    * @todo-javadoc Write javadocs for return value
    * @todo-javadoc Write javadocs for exception
    * @param name Describe what the parameter does
    * @return Describe the return value
    * @exception PredicateException Describe the exception
    */
   public AttributedPredicate createPredicate(String name) throws PredicateException {
      try {
         // Instantiate the predicate
         Class clazz = (Class)_predicates.get(name);
         if (clazz == null) {
            String msg = "No known predicate named <" + name + "/>";
            throw new PredicateException(msg);
         }
         AttributedPredicate predicate = (AttributedPredicate)clazz.newInstance();
         return predicate;
      } catch (IllegalAccessException e) {
         throw new PredicateException(e.getMessage());
      } catch (InstantiationException e) {
         throw new PredicateException(e.getMessage());
      }
   }


   /**
    * Describe what the method does
    *
    * @todo-javadoc Write javadocs for method
    * @todo-javadoc Write javadocs for method parameter
    * @todo-javadoc Write javadocs for method parameter
    * @param name Describe what the parameter does
    * @param predicateClass Describe what the parameter does
    */
   public void register(String name, Class predicateClass) {
      _predicates.put(name, predicateClass);
   }
}
