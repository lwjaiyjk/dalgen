/*
 * Taobao.com Inc.
 * Copyright (c) 2000-2004 All Rights Reserved.
 */
package middlegen.plugins.iwallet;


/**
 * An interface models an operation parameter.
 *
 * @author Cheng Li
 *
 * @version $Id: Parameter.java,v 1.1 2004/12/24 07:34:20 baobao Exp $
 */
public interface Parameter {
    /**
     *
     * @return
     */
    public String getQualifiedJavaType();

    /**
     * @return
     */
    public String getSimpleJavaType();

    /**
     *
     * @return
     */
    public String getName();
}
