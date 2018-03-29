/*
 * Taobao.com Inc.
 * Copyright (c) 2000-2004 All Rights Reserved.
 */
package middlegen.plugins.iwallet;

import middlegen.Column;
import middlegen.javax.JavaColumn;
import middlegen.javax.Sql2Java;
import middlegen.plugins.iwallet.config.IWalletColumnConfig;
import middlegen.plugins.iwallet.config.IWalletConfig;
import middlegen.plugins.iwallet.config.IWalletConfigException;
import middlegen.plugins.iwallet.util.DalUtil;

import org.apache.commons.lang.StringUtils;

import com.atom.dalgen.utils.LogUtils;

/**
 * A column decorator relates a database table column
 * to a java class field.
 *
 * @author Cheng Li
 *
 * @version $Id: IWalletColumn.java,v 1.2 2005/04/15 04:02:24 lei.shi Exp $
 */
public class IWalletColumn extends JavaColumn {

    /**
     * Constructor for IWalletColumnDecorator.
     */
    public IWalletColumn(Column subject) {
        super(subject);
    }

    /**
     * 
     * @return
     * 
     * @see middlegen.Column#getDefaultValue()
     */
    public String getResultMapNullValue() {
        if (StringUtils.isNotEmpty(getDefaultValue())) {
            return getDefaultValue();
        } else {
            return DalUtil.getDefaultValue(getJavaType());
        }
    }

    /**
     * 
     * @return
     */
    public boolean isNeedResultMapNullValue() {
        return Sql2Java.isPrimitive(getJavaType());
    }

    /**
     * Get java type without package name.
     *
     * @return
     */
    public String getSimpleJavaType() {
        return DalUtil.getSimpleJavaType(getJavaType());
    }

    /**
     *
     *
     * @see middlegen.PreferenceAware#init()
     */
    protected void init() {
        super.init();
    }

    protected IWalletColumnConfig getColumnConfig() {
        return ((IWalletTable) getTable()).getTableConfig().getColumn(getSqlName());
    }

    /**
     * @see middlegen.javax.JavaColumn#getVariableName()
     */
    public String getVariableName() {
        if (StringUtils.equals(super.getVariableName(), "return")) {
            return "returnValue";
        }

        return super.getVariableName();
    }

    /**
     *
     *
     * @see middlegen.javax.JavaColumn#setJavaType()
     */
    public void setJavaType() {
        super.setJavaType();

        String temp = null;

        IWalletColumnConfig columnConfig = getColumnConfig();

        if ((columnConfig != null) && StringUtils.isNotBlank(columnConfig.getJavaType())) {
            temp = columnConfig.getJavaType();
        } else {
            try {
                temp = IWalletConfig.getInstance().getMappedJavaType(super.getJavaType());
            } catch (IWalletConfigException e) {
                LogUtils.get().error(e.getMessage());
            }
        }

        if (StringUtils.isNotBlank(temp)) {
            super.setJavaType(temp);

            if (Character.isUpperCase(DalUtil.getSimpleJavaType(temp).charAt(0))) {
                ((IWalletTable) getTable()).addDoImport(temp);
            }
        }
    }
}
