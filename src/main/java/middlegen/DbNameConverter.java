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
package middlegen;

import com.atom.dalgen.utils.LogUtils;

/**
 * Describe what this class does
 *
 * @author David Channon and Aslak Helles
 * @created 3. october 2004
 * @todo-javadoc Write javadocs
 * @version $Id: DbNameConverter.java,v 1.1 2005/10/25 14:59:22 lusu Exp $
 */
public abstract class DbNameConverter {

    /**
     * @todo-javadoc Describe the column
     */
    protected static DbNameConverter _instance = null;

    /**
     * Describe what the method does
     *
     * @todo-javadoc Write javadocs for method
     * @todo-javadoc Write javadocs for method parameter
     * @todo-javadoc Write javadocs for return value
     * @param columnName Describe what the parameter does
     * @return Describe the return value
     */
    public abstract String columnNameToVariableName(String columnName);

    /**
     * Describe what the method does
     *
     * @todo-javadoc Write javadocs for method
     * @todo-javadoc Write javadocs for method parameter
     * @todo-javadoc Write javadocs for return value
     * @param tableName Describe what the parameter does
     * @return Describe the return value
     */
    public abstract String tableNameToVariableName(String tableName);

    /**
     * Gets the Instance attribute of the DbNameConverter class
     *
     * @return The Instance value
     */
    public static DbNameConverter getInstance() {
        if (_instance == null) {
            String className = System.getProperty("middlegen.DbNameConverter");
            LogUtils.get().debug("Custom DbNameConverter: " + className);
            if (className != null) {
                try {
                    _instance = (DbNameConverter) Class.forName(className).newInstance();
                } catch (Exception e) {
                    LogUtils.get().error(e.getMessage() + " Couldn't instantiate " + className + ". Using default.");
                    _instance = new DbNameConverterImpl();
                }
            } else {
                _instance = new DbNameConverterImpl();
            }
        }
        return _instance;
    }
}
