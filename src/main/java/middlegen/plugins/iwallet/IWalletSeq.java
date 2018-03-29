/*
 * Taobao.com Inc.
 * Copyright (c) 2000-2004 All Rights Reserved.
 */
package middlegen.plugins.iwallet;

import java.util.ArrayList;
import java.util.List;

import middlegen.DbNameConverter;
import middlegen.Table;
import middlegen.Util;
import middlegen.plugins.iwallet.config.IWalletSeqConfig;

/**
 * A decorator class for sequence.
 *
 * @author Cheng Li
 *
 * @version $Id: IWalletSeq.java,v 1.1 2004/12/24 07:34:20 baobao Exp $
 */
public class IWalletSeq implements Operation {
    /** the sequence configuration name */
    private IWalletSeqConfig seqConfig;

    /** the operation name for implementing "select <sequence>.nextval from dual" */
    private String operationName;

    /**
     * Constructor for IWalletSeq.
     */
    public IWalletSeq(IWalletSeqConfig config) {
        super();

        seqConfig = config;
    }

    /**
     * @return
     */
    public IWalletSeqConfig getSeqConfig() {
        return seqConfig;
    }

    /**
     * @return
     */
    public String getName() {
        return seqConfig.getName();
    }

    /**
     *
     * @return
     */
    public String getOperationName() {
        return "getNext"
        + Util.singularise(DbNameConverter.getInstance().tableNameToVariableName(getName()));
    }

    /**
     * @return
     */
    public String getMappedStatementId() {
        return IWalletOperation.MAPPED_STATEMENT_PREFIX
        + middlegen.plugins.iwallet.util.DalUtil.toUpperCaseWithDash(getName());
    }

    /**
     * @return
     */
    public String getMappedStatementSql() {
        return getParsedSql();
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.Operation#getDefaultReturnValue()
     */
    public String getDefaultReturnValue() {
        return "0";
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.Operation#getExceptions()
     */
    public List getExceptions() {
        List list = new ArrayList();

        list.add("com.taobao.common.persistence.exception.FindException");

        return list;
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.Operation#getExceptions()
     */
    public List getIbatisExceptions() {
        List list = new ArrayList();

        list.add("com.taobao.common.persistence.exception.PersistenceException");

        return list;
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.Operation#getMappedStatementType()
     */
    public String getMappedStatementType() {
        return "select";
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.Operation#getParsedSql()
     */
    public String getParsedSql() {
        return "SELECT " + getName() + "." + "nextval FROM dual";
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.Operation#getReturnType()
     */
    public String getReturnType() {
        return "long";
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.Operation#getSimpleExceptions()
     */
    public List getSimpleExceptions() {
        List list    = getExceptions();
        List newList = new ArrayList();

        for (int i = 0; i < list.size(); i++) {
            String exception = (String) list.get(i);

            newList.add(middlegen.plugins.iwallet.util.DalUtil.getSimpleJavaType(exception));
        }

        return newList;
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.Operation#getSimpleReturnType()
     */
    public String getSimpleReturnType() {
        return middlegen.plugins.iwallet.util.DalUtil.getSimpleJavaType(getReturnType());
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.Operation#getTable()
     */
    public Table getTable() {
        return null;
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.Operation#getTemplateSuffix()
     */
    public String getTemplateSuffix() {
        return "-seq";
    }
}
