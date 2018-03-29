/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package middlegen.plugins.iwallet.config;

/**
 * SQL节点
 * 
 * @author shizihu
 * @version $Id: IWalletSqlConfig.java, v 0.1 2013-1-5 下午1:11:01 shizihu Exp $
 */
public class IWalletSqlConfig {
    /** ID */
    private String  id;

    /** 是否转义 */
    private boolean escape;

    /** SQL内容 */
    private String  sql;

    /** 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[id=").append(id).append(", sql=").append(sql).append("]");
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public boolean isEscape() {
        return escape;
    }

    public void setEscape(boolean escape) {
        this.escape = escape;
    }

}
