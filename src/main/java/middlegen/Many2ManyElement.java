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

import com.atom.dalgen.utils.LogUtils;

/**
 * This class represents a many2many relationship.
 *
 * @author Aslak Helles
 * @created 5. juli 2002
 */
public class Many2ManyElement {
    /**
     * @todo-javadoc Describe the field
     */
    private TableElement _tablea;
    /**
     * @todo-javadoc Describe the field
     */
    private TableElement _jointable;
    /**
     * @todo-javadoc Describe the field
     */
    private TableElement _tableb;

    /**
     * @todo-javadoc Describe the field
     */
    private boolean      _matched = false;

    /**
     * Gets the Tablea attribute of the Many2ManyElement object
     *
     * @return The Tablea value
     */
    public TableElement getTablea() {
        return _tablea;
    }

    /**
     * Gets the Jointable attribute of the Many2ManyElement object
     *
     * @return The Jointable value
     */
    public TableElement getJointable() {
        return _jointable;
    }

    /**
     * Gets the Tableb attribute of the Many2ManyElement object
     *
     * @return The Tableb value
     */
    public TableElement getTableb() {
        return _tableb;
    }

    /**
     * Gets the Matched attribute of the Many2ManyElement object
     *
     * @return The Matched value
     */
    public boolean isMatched() {
        return _matched;
    }

    /**
     * Gets the OrderedNameWithoutJoinTable attribute of the Many2ManyElement
     * object
     *
     * @return The OrderedNameWithoutJoinTable value
     */
    public String getOrderedNameWithoutJoinTable() {
        return _tablea.getName() + "--" + _tableb.getName();
    }

    /**
     * Describe the method
     *
     * @todo-javadoc Describe the method
     * @todo-javadoc Describe the method parameter
     * @param tableElement Describe the method parameter
     */
    public void addConfiguredTablea(TableElement tableElement) {
        _tablea = tableElement;
    }

    /**
     * Describe the method
     *
     * @todo-javadoc Describe the method
     * @todo-javadoc Describe the method parameter
     * @param tableElement Describe the method parameter
     */
    public void addConfiguredJointable(TableElement tableElement) {
        _jointable = tableElement;
    }

    /**
     * Describe the method
     *
     * @todo-javadoc Describe the method
     * @todo-javadoc Describe the method parameter
     * @param tableElement Describe the method parameter
     */
    public void addConfiguredTableb(TableElement tableElement) {
        _tableb = tableElement;
    }

    /**
     * Describe what the method does
     *
     * @todo-javadoc Write javadocs for method
     * @todo-javadoc Write javadocs for return value
     * @return Describe the return value
     */
    public String toString() {
        return _tablea.getName() + "-" + _jointable.getName() + "-" + _tableb.getName();
    }

    /** swaps a and b if a is before b in the alphabet */
    public void order() {
        if (_tablea.getName().compareTo(_tableb.getName()) > 0) {
            TableElement swap = _tablea;
            _tablea = _tableb;
            _tableb = swap;
        }
    }

    /**
     * Describe what the method does
     *
     * @todo-javadoc Write javadocs for method
     * @todo-javadoc Write javadocs for method parameter
     * @todo-javadoc Write javadocs for method parameter
     * @todo-javadoc Write javadocs for method parameter
     * @todo-javadoc Write javadocs for return value
     * @param tableone Describe what the parameter does
     * @param jointable Describe what the parameter does
     * @param tabletwo Describe what the parameter does
     * @return Describe the return value
     */
    public boolean matches(String tableone, String jointable, String tabletwo) {
        boolean straight = compare(tableone, jointable, tabletwo);
        boolean queer = compare(tabletwo, jointable, tableone);
        boolean matched = straight || queer;
        if (matched) {
            _matched = true;
        }
        LogUtils.get().debug(this.toString() + " matches " + tableone + "-" + jointable + "-" + tabletwo + ":" + matched);
        return matched;
    }

    /**
     * Describe what the method does
     *
     * @todo-javadoc Write javadocs for method
     * @todo-javadoc Write javadocs for method parameter
     * @todo-javadoc Write javadocs for method parameter
     * @todo-javadoc Write javadocs for method parameter
     * @todo-javadoc Write javadocs for return value
     * @param tableone Describe what the parameter does
     * @param jointable Describe what the parameter does
     * @param tabletwo Describe what the parameter does
     * @return Describe the return value
     */
    private boolean compare(String tableone, String jointable, String tabletwo) {
        boolean aIsOne = _tablea.getName().equals(tableone);
        boolean bIsTwo = _tableb.getName().equals(tabletwo);
        boolean joinMatched = _jointable.getName().equals(jointable);
        return aIsOne && bIsTwo && joinMatched;
    }
}
