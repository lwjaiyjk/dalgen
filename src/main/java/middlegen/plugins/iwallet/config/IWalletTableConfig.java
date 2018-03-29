/*
 * Taobao.com Inc.
 * Copyright (c) 2000-2004 All Rights Reserved.
 */
package middlegen.plugins.iwallet.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A bean class represents an iwallet table configuration.
 */
public class IWalletTableConfig {
    /** the table name in database */
    private String                           sqlName;

    /** the dataobject name */
    private String                           doName;

    /** the sub package name */
    private String                           subPackage;

    /** the sequence corresponding to the table */
    private String                           sequence;

    // add by zhaoxu 20061225
    private boolean                          autoSwitchDataSrc;

    //add by yuanxiao 
    private String                           confidentiality;
    private String                           integrity;

    // DRM自配置
    private boolean                          drmConfig;

    private String                           encodekeyname;
    private String                           abstractkeyname;

    /** 虚拟主键 */
    private String                           dummyPk;

    /** 票据ID */
    private boolean                          ticket;

    /** 票据名称 */
    private String                           ticketName;

    /** 票据转换 */
    private boolean                          fmtNo;

    /** 票据转换Bean名称 */
    private String                           fmtNoName;

    /** 是否生成开关值 */
    private boolean                          valve;

    /** a list of all public sqls. */
    private List<IWalletSqlConfig>           sqls       = new ArrayList<IWalletSqlConfig>();

    /** a list of all public copys. */
    private List<CopyConfig>                 copys      = new ArrayList<CopyConfig>();

    /** a list of all configured operations */
    private List<IWalletOperationConfig>     operations = new ArrayList<IWalletOperationConfig>();

    /** a map of all column configuration */
    private Map<String, IWalletColumnConfig> columns    = new HashMap<String, IWalletColumnConfig>();

    /** a list of all result maps */
    private List<IWalletResultMapConfig>     resultMaps = new ArrayList<IWalletResultMapConfig>();

    /**
     * Constructor for IWalletTableConfig.
     */
    public IWalletTableConfig() {
        super();
        this.bConfidentiality();
        this.bIntegrity();
    }

    /**
     * @return
     */
    public String getSqlName() {
        return sqlName;
    }

    /**
     * @return
     */
    public String getDoName() {
        return doName;
    }

    /**
     * @return
     */
    public String getSubPackage() {
        return subPackage;
    }

    /**
     * @param string
     */
    public void setSqlName(String string) {
        sqlName = string;
    }

    /**
     * @param string
     */
    public void setDoName(String string) {
        doName = string;
    }

    /**
     * @param string
     */
    public void setSubPackage(String string) {
        subPackage = string;
    }

    /**
     * @return
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();

        String newLine = System.getProperty("line.separator");

        sb.append("[").append("sqlname=").append(sqlName).append(", doname=").append(doName).append(", subpackage=").append(subPackage).append(", sequence=").append(sequence)
            .append(", confidentiality=").append(confidentiality).append(", integrity=").append(integrity).append(", encodekeyname=").append(encodekeyname).append(", abstractkeyname=")
            .append(abstractkeyname).append(", drmConfig=").append(drmConfig).append(", ticket=").append(ticket).append("]").append(newLine);

        for (int i = 1; i <= resultMaps.size(); i++) {
            sb.append("rm-").append(i).append(": ").append(resultMaps.get(i - 1)).append(newLine);
        }

        for (int i = 1; i <= operations.size(); i++) {
            sb.append("op-").append(i).append(": ").append(operations.get(i - 1)).append(newLine);
        }

        for (Iterator<String> i = columns.keySet().iterator(); i.hasNext();) {
            sb.append("column:").append(columns.get(i.next())).append(newLine);
        }

        return sb.toString();
    }

    public List<IWalletSqlConfig> getSqls() {
        return this.sqls;
    }

    public void addSql(IWalletSqlConfig sql) {
        this.sqls.add(sql);
    }

    public List<CopyConfig> getCopys() {
        return this.copys;
    }

    public void addCopy(CopyConfig copy) {
        this.copys.add(copy);
    }

    /**
     * @return
     */
    public List<IWalletOperationConfig> getOperations() {
        return operations;
    }

    /**
     * Add an operation configuration to the operation list,
     * and have the operation points to this table configuration.
     *
     * @param operationConfig
     */
    public void addOperation(IWalletOperationConfig operationConfig) {
        operations.add(operationConfig);
        operationConfig.setTableConfig(this);
    }

    /**
     * Get a column configuration by its name.
     *
     * @param name
     * @return
     */
    public IWalletColumnConfig getColumn(String name) {
        return columns.get(name.toLowerCase());
    }

    /**
     * Add a column configuration.
     *
     * @param columnConfig
     */
    public void addColumn(IWalletColumnConfig columnConfig) {
        if (columnConfig != null) {
            columns.put(columnConfig.getName().toLowerCase(), columnConfig);
        }
    }

    /**
     * @return
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * @param string
     */
    public void setSequence(String string) {
        sequence = string;
    }

    /**
     * @return
     */
    public List<IWalletResultMapConfig> getResultMaps() {
        return resultMaps;
    }

    /**
     * @param operationConfig
     */
    public void addResultMap(IWalletResultMapConfig resultMapConfig) {
        resultMaps.add(resultMapConfig);
        resultMapConfig.setTableConfig(this);
    }

    /**
     * @return the autoSwitchDataSrc
     */
    public boolean isAutoSwitchDataSrc() {
        return autoSwitchDataSrc;
    }

    /**
     * add by zhaoxu 20061225
     * 
     * @param autoSwitchDataSrc the autoSwitchDataSrc to set
     */
    public void setAutoSwitchDataSrc(boolean autoSwitchDataSrc) {
        this.autoSwitchDataSrc = autoSwitchDataSrc;
    }

    /**
     * @return Returns the dummyPk.
     */
    public String getDummyPk() {
        return dummyPk;
    }

    /**
     * @param dummyPk The dummyPk to set.
     */
    public void setDummyPk(String dummyPk) {
        this.dummyPk = dummyPk;
    }

    public String getConfidentiality() {
        return confidentiality;
    }

    public void setConfidentiality(String confidentiality) {
        this.confidentiality = confidentiality;
    }

    public String getIntegrity() {
        return integrity;
    }

    public void setIntegrity(String integrity) {
        this.integrity = integrity;
    }

    public String getEncodekeyname() {
        return encodekeyname;
    }

    public void setEncodekeyname(String encodekeyname) {
        this.encodekeyname = encodekeyname;
    }

    public String getAbstractkeyname() {
        return abstractkeyname;
    }

    public void setAbstractkeyname(String abstractkeyname) {
        this.abstractkeyname = abstractkeyname;
    }

    public boolean bConfidentiality() {
        if (this.getConfidentiality() != null && this.getConfidentiality().length() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean bIntegrity() {
        if (this.getIntegrity() != null && this.getIntegrity().length() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean getDrmConfig() {
        return drmConfig;
    }

    public void setDrmConfig(boolean drmConfig) {
        this.drmConfig = drmConfig;
    }

    public boolean isTicket() {
        return ticket;
    }

    public void setTicket(boolean ticket) {
        this.ticket = ticket;
    }

    public String getTicketName() {
        return ticketName;
    }

    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }

    public boolean isFmtNo() {
        return fmtNo;
    }

    public void setFmtNo(boolean fmtNo) {
        this.fmtNo = fmtNo;
    }

    public String getFmtNoName() {
        return fmtNoName;
    }

    public void setFmtNoName(String fmtNoName) {
        this.fmtNoName = fmtNoName;
    }

    public boolean isValve() {
        return valve;
    }

    public void setValve(boolean valve) {
        this.valve = valve;
    }

}
