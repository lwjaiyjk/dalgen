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
package middlegen.predicate.xml;

import java.util.Stack;

import middlegen.predicate.AttributedPredicate;
import middlegen.predicate.CompositePredicate;
import middlegen.predicate.PredicateException;
import middlegen.predicate.PredicateFactory;

import org.apache.commons.collections.Predicate;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.atom.dalgen.utils.LogUtils;

/**
 * This class parses an XML structure and builds a predicate tree.
 *
 * @author Aslak Helles
 * @created 21. august 2002
 */
public class PredicateBuilder extends DefaultHandler {
    /**
     * @todo-javadoc Describe the field
     */
    private final Stack            _stack = new Stack();

    /**
     * @todo-javadoc Describe the field
     */
    private Predicate              _predicate;

    /**
     * @todo-javadoc Describe the field
     */
    private final PredicateFactory _predicateFactory;

    /**
     * Describe what the DynamicPredicate constructor does
     */
    public PredicateBuilder(PredicateFactory predicateFactory) {
        _predicateFactory = predicateFactory;
    }

    /**
     * Gets the Predicate attribute of the PredicateBuilder object
     *
     * @return The Predicate value
     */
    public Predicate getPredicate() {
        return _predicate;
    }

    /**
     * Describe what the method does
     */
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        try {
            AttributedPredicate predicate = _predicateFactory.createPredicate(qName);
            for (int i = 0; i < attributes.getLength(); i++) {
                String name = attributes.getQName(i);
                String value = attributes.getValue(i);
                predicate.put(name, value);
            }
            if (!_stack.isEmpty()) {
                Object parent = _stack.peek();
                if (parent instanceof CompositePredicate) {
                    CompositePredicate compositePredicate = (CompositePredicate) parent;
                    compositePredicate.add(predicate);
                } else {
                    String msg = "Can't create a " + qName + " element here.";
                    LogUtils.get().error(msg);
                    throw new IllegalStateException(msg);
                }
            }
            _stack.push(predicate);
        } catch (PredicateException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Describe what the method does
     *
     * @todo-javadoc Write javadocs for method
     * @todo-javadoc Write javadocs for method parameter
     * @todo-javadoc Write javadocs for method parameter
     * @todo-javadoc Write javadocs for method parameter
     * @param uri Describe what the parameter does
     * @param localName Describe what the parameter does
     * @param qName Describe what the parameter does
     */
    public void endElement(String uri, String localName, String qName) {
        _stack.pop();
    }
}
