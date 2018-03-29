/*
 * Taobao.com Inc.
 * Copyright (c) 2000-2004 All Rights Reserved.
 */
package middlegen.plugins.iwallet;

import java.util.List;

import middlegen.Table;

/**
 * An interface that describe an operation.
 *
 * @author Cheng Li
 *
 * @version $Id: Operation.java,v 1.1 2004/12/24 07:34:20 baobao Exp $
 */
public interface Operation {
    /**
     * Gets the table attribute of the Operation object
     *
     * @return The Table value
     */
    public Table getTable();

    /**
     * Gets the returnType attribute of the Operation object.
     *
     * @return The returnType value
     */
    public String getReturnType();

    /**
     * Gets the simpleReturnType attribute of the Operation object.
     *
     * @return The simpleReturnType value
     */
    public String getSimpleReturnType();

    /**
     * Get the name attribute of the operation.
     *
     * @return
     */
    public String getName();

    /**
     * Get the suffix for the template that render the operation.
     *
     * <p>
     * @return
     */
    public String getTemplateSuffix();

    /**
     * Get the parsed sql statement for the operation.
     *
     * @return
     */
    public String getParsedSql();

    /**
     * Get the default return value, used to generate method stub.
     *
     * @return
     */
    public String getDefaultReturnValue();

    /**
     * Get all throwed exception.
     *
     * @return
     */
    public List getExceptions();

    /**
     * Get all throwed exception types, in simple form.
     * @return
     */
    public List getSimpleExceptions();

    /**
     * @return
     */
    public String getMappedStatementType();
}
