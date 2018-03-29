/*
 * Taobao.com Inc.
 * Copyright (c) 2000-2004 All Rights Reserved.
 */
package middlegen.plugins.iwallet.operation;

import middlegen.Table;
import middlegen.plugins.iwallet.IWalletOperation;
import middlegen.plugins.iwallet.config.IWalletOperationConfig;

/**
 * An implementation of "delete" statement.
 *
 * @author Cheng Li
 *
 * @version $Id: IWalletDelete.java,v 1.1 2004/12/24 07:34:20 baobao Exp $
 */
public class IWalletDelete extends IWalletOperation {

    public static final String  OP_TYPE = "delete";

    /**
     * Constructor for IWalletDelete.
     */
    public IWalletDelete(IWalletOperationConfig conf) {
        super(conf);

        if (PARAM_TYPE_OBJECT.equals(conf.getParamType())) {
            paramType = PARAM_TYPE_OBJECT;
        } else {
            // default
            paramType = PARAM_TYPE_PRIMITIVE;
        }

        multiplicity = MULTIPLICITY_ONE;
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.Operation#getReturnType()
     */
    public String getReturnType() {
        return "int";
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.Operation#getSimpleReturnType()
     */
    public String getSimpleReturnType() {
        return "int";
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.Operation#getTemplateSuffix()
     */
    public String getTemplateSuffix() {
        return OP_TYPE;
    }

    /**
     * @param t
     *
     * @see middlegen.plugins.iwallet.IWalletOperation#setTable(middlegen.Table)
     */
    public void setTable(Table t) {
        super.setTable(t);
    }

    /**
     * @return
     */
    public String getMappedStatementType() {
        return OP_TYPE;
    }
}
