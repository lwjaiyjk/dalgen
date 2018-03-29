/*
 * Taobao.com Inc.
 * Copyright (c) 2000-2004 All Rights Reserved.
 */
package middlegen.plugins.iwallet.config;


/**
 * A bean class represents configuration concerning an sequence.
 *
 * @author Cheng Li
 *
 * @version $Id: IWalletSeqConfig.java,v 1.1 2004/12/24 07:34:20 baobao Exp $
 */
public class IWalletSeqConfig {
    /** name of the sequence */
    private String name;

    /**
     * Constructor for IWalletSeqConfig.
     */
    public IWalletSeqConfig() {
        super();
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param string
     */
    public void setName(String string) {
        name = string;
    }
}
