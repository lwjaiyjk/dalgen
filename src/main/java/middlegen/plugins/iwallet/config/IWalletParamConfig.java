/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2005 All Rights Reserved.
 */
package middlegen.plugins.iwallet.config;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * An configuration object represents a DAO method param.
 */
public class IWalletParamConfig {

    /** 参数名 */
    private String name;

    /** 参数类型 */
    private String javaType;

    /** 泛型类型 */
    private String genericType;

    /**
     * Constructor.
     */
    public IWalletParamConfig() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getGenericType() {
        return genericType;
    }

    public void setGenericType(String genericType) {
        this.genericType = genericType;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
