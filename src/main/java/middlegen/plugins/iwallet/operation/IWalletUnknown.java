/*
 * Taobao.com Inc.
 * Copyright (c) 2000-2004 All Rights Reserved.
 */
package middlegen.plugins.iwallet.operation;

import middlegen.plugins.iwallet.IWalletOperation;
import middlegen.plugins.iwallet.config.IWalletOperationConfig;

/**
 * An implementation of unknown operation decorator.
 *
 * @author Cheng Li
 *
 * @version $Id: IWalletUnknown.java,v 1.1 2004/12/24 07:34:20 baobao Exp $
 */
public class IWalletUnknown extends IWalletOperation {
    /**
     * Constructor for IWalletUnknown.
     */
    public IWalletUnknown(IWalletOperationConfig opConfig) {
        super(opConfig);
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.Operation#getReturnTypeName()
     */
    public String getReturnType() {
        return null;
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.Operation#getTemplateSuffix()
     */
    public String getTemplateSuffix() {
        return "unknown";
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.Operation#getSimpleReturnType()
     */
    public String getSimpleReturnType() {
        return null;
    }

    /**
     * @return
     */
    public String getMappedStatementType() {
        return "unknown";
    }
}
