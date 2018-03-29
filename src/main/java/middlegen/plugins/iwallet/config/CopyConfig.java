/**
 * Author: obullxl@gmail.com
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package middlegen.plugins.iwallet.config;

/**
 * 直接复制节点
 * 
 * @author obullxl@gmail.com
 * @version $Id: CopyConfig.java, V1.0.1 2013年11月27日 下午1:35:00 $
 */
public class CopyConfig {
    /** 类型 */
    private String type;

    /** 复制内容 */
    private String copy;

    /** 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[Type=").append(type).append(", Content=").append(copy).append("].");
        return sb.toString();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCopy() {
        return copy;
    }

    public void setCopy(String copy) {
        this.copy = copy;
    }

}
