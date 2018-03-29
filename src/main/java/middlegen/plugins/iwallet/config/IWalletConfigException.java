/*
 * Taobao.com Inc.
 * Copyright (c) 2000-2004 All Rights Reserved.
 */
package middlegen.plugins.iwallet.config;


/**
 * @author Cheng Li
 *
 * @version $Id: IWalletConfigException.java,v 1.1 2004/12/24 07:34:20 baobao Exp $
 */
public class IWalletConfigException extends Exception {
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3690192148834105400L;

	/**
     * Constructor for IWalletConfigException.
     */
    public IWalletConfigException() {
        super();
    }

    /**
     * Constructor for IWalletConfigException.
     */
    public IWalletConfigException(String message) {
        super(message);
    }

    /**
     * Constructor for IWalletConfigException.
     */
    public IWalletConfigException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor for IWalletConfigException.
     */
    public IWalletConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
