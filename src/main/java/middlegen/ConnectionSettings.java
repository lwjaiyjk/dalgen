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
 * @author <a href="mailto:aslak.hellesoy@bekk.no">Aslak Helles</a>
 * @created 3. oktober 2001
 * @version $Id: ConnectionSettings.java,v 1.1 2005/10/25 14:59:22 lusu Exp $
 */
public class ConnectionSettings {

    /**
     * @todo-javadoc Describe the column
     */
    private String _JDBCDriver  = "";

    /**
     * @todo-javadoc Describe the column
     */
    private String _databaseURL = "";
    /**
     * @todo-javadoc Describe the column
     */
    private String _username    = "";
    /**
     * @todo-javadoc Describe the column
     */
    private String _password    = "";
    /**
     * @todo-javadoc Describe the column
     */
    private String _catalog     = "";
    /**
     * @todo-javadoc Describe the column
     */
    private String _schema      = "";

    /** Creates new ConnectionSettings */
    public ConnectionSettings() {
    }

    /**
     * Sets the JDBCDriver attribute of the ConnectionSettings object
     *
     * @param JDBCDriver The new JDBCDriver value
     */
    public void setJDBCDriver(String JDBCDriver) {
        _JDBCDriver = JDBCDriver;
    }

    /**
     * Sets the DatabaseURL attribute of the ConnectionSettings object
     *
     * @param databaseURL The new DatabaseURL value
     */
    public void setDatabaseURL(String databaseURL) {
        _databaseURL = databaseURL;
    }

    /**
     * Sets the Username attribute of the ConnectionSettings object
     *
     * @param username The new Username value
     */
    public void setUsername(String username) {
        _username = username;
    }

    /**
     * Sets the Password attribute of the ConnectionSettings object
     *
     * @param password The new Password value
     */
    public void setPassword(String password) {
        _password = password;
    }

    /**
     * Sets the Catalog attribute of the ConnectionSettings object
     *
     * @param catalog The new Catalog value
     */
    public void setCatalog(String catalog) {
        _catalog = (catalog == null ? "" : catalog);
    }

    /**
     * Sets the Schema attribute of the ConnectionSettings object
     *
     * @param schema The new Schema value
     */
    public void setSchema(String schema) {
        _schema = (schema == null ? "" : schema);
    }

    /**
     * Gets the JDBCDriver attribute of the ConnectionSettings object
     *
     * @return The JDBCDriver value
     */
    public String getJDBCDriver() {
        return _JDBCDriver;
    }

    /**
     * Gets the DatabaseURL attribute of the ConnectionSettings object
     *
     * @return The DatabaseURL value
     */
    public String getDatabaseURL() {
        return _databaseURL;
    }

    /**
     * Gets the Username attribute of the ConnectionSettings object
     *
     * @return The Username value
     */
    public String getUsername() {
        return _username;
    }

    /**
     * Gets the Password attribute of the ConnectionSettings object
     *
     * @return The Password value
     */
    public String getPassword() {
        return _password;
    }

    /**
     * Gets the Catalog attribute of the ConnectionSettings object
     *
     * @return The Catalog value
     */
    public String getCatalog() {
        return (_catalog.trim().equals("") ? null : _catalog);
    }

    /**
     * Gets the Schema attribute of the ConnectionSettings object
     *
     * @return The Schema value
     */
    public String getSchema() {
        return (_schema.trim().equals("") ? null : _schema);
    }
}
