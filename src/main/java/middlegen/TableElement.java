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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class is used to tell Middlegen what tables we want to generate for and
 * what (optional) table/sequence to use for pk generation.
 *
 * @author Aslak Helles
 * @created 5. juli 2002
 */
public class TableElement {

    /**
     * @todo-javadoc Describe the field
     */
    private Collection   _crossrefs                   = new LinkedList();

    /**
     * @todo-javadoc Describe the field
     */
    private boolean      _isGenerate                  = true;

    /** The table name (as defined by the user) */
    private String       _name;

    /** The table name reported by the database */
    private String       _physicalName;

    /**
     * @todo-javadoc Describe the field
     */
    private String       _plural;
    /**
     * @todo-javadoc Describe the field
     */
    private String       _singular;
    /**
     * @todo-javadoc Describe the field
     */
    private String       _pkTableSqlName;
    /**
     * @todo-javadoc Describe the field
     */
    private String       _pkCacheSize;

    /** the name of the sequence this table uses (optional attribute) */
    private String       _sequenceName                = null;

    /**
     * @todo-javadoc Describe the field
     */
    private String       _pkColumnsOverride           = "";

    /**
     * @todo-javadoc Describe the field
     */
    private List<String> _pkColumnsOverrideCollection = new ArrayList<String>();

    /** the name of the owner of the synonym if this table is a synonym */
    private String       _ownerSynonymName            = null;

    /**
     * Sets the Pkcolumnsoverride attribute of the TableElement object
     *
     * @param pkColumnsOverride The new Pkcolumnsoverride value
     */
    public void setPkcolumnsoverride(String pkColumnsOverride) {
        _pkColumnsOverride = pkColumnsOverride;
        StringTokenizer st = new StringTokenizer(_pkColumnsOverride, ",", false);
        while (st.hasMoreTokens()) {
            _pkColumnsOverrideCollection.add(st.nextToken());
        }
    }

    /**
     * Sets the SequenceName attribute of the TableElement object
     *
     * @param name The new SequenceName value
     */
    public void setSequenceName(String name) {
        _sequenceName = name;
    }

    /**
     * Sets the OwnerSynonymName attribute of the TableElement object
     *
     * @param name The new OwnerSynonymName value
     */
    public void setOwnerSynonymName(String name) {
        _ownerSynonymName = name;
    }

    /**
     * Sets the Generate attribute of the TableElement object
     *
     * @param flag The new Generate value
     */
    public void setGenerate(boolean flag) {
        _isGenerate = flag;
    }

    /**
     * Sets the Singular attribute of the TableElement object
     *
     * @param s The new Singular value
     */
    public void setSingular(String s) {
        _singular = s;
    }

    /**
     * Sets the Plural attribute of the TableElement object
     *
     * @param s The new Plural value
     */
    public void setPlural(String s) {
        _plural = s;
    }

    /**
     * Sets the Name attribute of the TableElement object
     *
     * @param name The new Name value
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * Sets the PhysicalName attribute of the TableElement object
     *
     * @param physicalName The new PhysicalName value
     */
    public void setPhysicalName(String physicalName) {
        _physicalName = physicalName;
    }

    /**
     * Sets the PkTableSqlName attribute of the TableElement object
     *
     * @param pkTableSqlName The new PkTableSqlName value
     */
    public void setPktable(String pkTableSqlName) {
        _pkTableSqlName = pkTableSqlName;
    }

    /**
     * Sets the Pkcachesize attribute of the TableElement object
     *
     * @param pkCacheSize The new Pkcachesize value
     */
    public void setPkcachesize(String pkCacheSize) {
        _pkCacheSize = pkCacheSize;
    }

    /**
     * Gets the PkColumnsOverride attribute of the TableElement object
     *
     * @return The PkColumnsOverride value
     */
    public String getPkColumnsOverride() {
        return _pkColumnsOverride;
    }

    /**
     * Gets the PkColumnsOverrideCollection attribute of the TableElement object
     *
     * @return The PkColumnsOverrideCollection value
     */
    public List<String> getPkColumnsOverrideCollection() {
        return _pkColumnsOverrideCollection;
    }

    /**
     * Gets the SequenceName attribute of the TableElement object
     *
     * @return The SequenceName value
     */
    public String getSequenceName() {
        return _sequenceName;
    }

    /**
     * Gets the OwnerSynonymName attribute of the TableElement object
     *
     * @return The OwnerSynonymName value
     */
    public String getOwnerSynonymName() {
        return _ownerSynonymName;
    }

    /**
     * @return The Singular value
     */
    public String getSingular() {
        return _singular;
    }

    /**
     * Gets the Plural attribute of the TableElement object
     *
     * @return The Plural value
     */
    public String getPlural() {
        return _plural;
    }

    /**
     * Gets the Generate attribute of the TableElement object
     *
     * @return The Generate value
     */
    public boolean isGenerate() {
        return _isGenerate;
    }

    /**
     * Gets the Name attribute of the TableElement object
     *
     * @return The Name value
     */
    public String getName() {
        return _name;
    }

    /**
     * Gets the PhysicalName attribute of the TableElement object
     *
     * @return The PhysicalName value
     */
    public String getPhysicalName() {
        return _physicalName;
    }

    /**
     * Gets the PkTableSqlName attribute of the TableElement object
     *
     * @return The PkTableSqlName value
     */
    public String getPkTableSqlName() {
        return _pkTableSqlName;
    }

    /**
     * Gets the PkCacheSize attribute of the TableElement object
     *
     * @return The PkCacheSize value
     */
    public String getPkCacheSize() {
        return _pkCacheSize;
    }

    /**
     * Gets the Crossrefs attribute of the TableElement object
     *
     * @return The Crossrefs value
     */
    public Collection getCrossrefs() {
        return _crossrefs;
    }

    /**
     * Describe the method
     *
     * @todo-javadoc Describe the method
     * @todo-javadoc Describe the method parameter
     * @param crossref Describe the method parameter
     */
    public void addCrossref(CrossrefElement crossref) {
        _crossrefs.add(crossref);
    }

    /**
     * Describe what the method does
     *
     * @todo-javadoc Write javadocs for method
     * @todo-javadoc Write javadocs for method parameter
     * @todo-javadoc Write javadocs for return value
     * @param o Describe what the parameter does
     * @return Describe the return value
     */
    public boolean equals(Object o) {
        TableElement other = (TableElement) o;
        return getName().equals(other.getName());
    }

    /**
     * Describe what the method does
     *
     * @todo-javadoc Write javadocs for method
     * @todo-javadoc Write javadocs for return value
     * @return Describe the return value
     */
    public int hashCode() {
        return getName().hashCode();
    }
}
