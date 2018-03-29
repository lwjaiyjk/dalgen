/*
 * Taobao.com Inc.
 * Copyright (c) 2000-2004 All Rights Reserved.
 */
package middlegen.plugins.iwallet;

import middlegen.javax.Sql2Java;
import middlegen.plugins.iwallet.util.DalUtil;

import org.apache.commons.lang.StringUtils;


/**
 * @author Cheng Li
 *
 * @version $Id: IWalletResultMapResult.java,v 1.3 2005/04/15 04:02:24 lei.shi Exp $
 */
public class IWalletResultMapResult implements ResultMapResult {
    /** the column associated with the result map */
    protected IWalletColumn column;
    
    /** 
     * Constructor for IWalletResultMapResult.
     */
    public IWalletResultMapResult(IWalletColumn c) {
        column = c;
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.ResultMapResult#getProperty()
     */
    public String getProperty() {
        if (column != null) {
            if (IWalletPlugin.MONEY_CLASS.equals(column.getJavaType())) {
                return column.getVariableName() + ".cent"; 
            } else {
                return column.getVariableName();
            }
        } else {
            return null;
        }
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.ResultMapResult#getColumn()
     */
    public String getColumn() {
        if (column != null) {
            return column.getSqlName();
        } else {
            return null;
        }
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.ResultMapResult#getColumnIndex()
     */
    public int getColumnIndex() {
        // not used yet
        return -1;
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.ResultMapResult#getJavaType()
     */
    public String getJavaType() {
        if (column != null) {
            if (IWalletPlugin.MONEY_CLASS.equals(column.getJavaType())) {
                // for Money, we map between db column and Money property cent
                return "long"; 
            } else {
                return column.getJavaType();
            }
        } else {
            return null;
        }
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.ResultMapResult#getJdbcType()
     */
    public String getJdbcType() {
    	if (column != null) {
        	String origJdbcType = column.getSqlTypeName();
        	
        	if (StringUtils.equalsIgnoreCase(origJdbcType, "DATE")) {
        		return "DATETIME";
        	} else {
        		return origJdbcType;
        	}
    	} else {
    		return null;
    	}
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.ResultMapResult#getNullValue()
     */
    public String getNullValue() {
//      if (column != null) {
//          return column.getResultMapNullValue();
//      } else {
//          return null;
//      }
        return DalUtil.getDefaultValue(getJavaType());     
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.ResultMapResult#isHasNullValue()
     */
    public boolean isHasNullValue() {
        return Sql2Java.isPrimitive(getJavaType());
//      if (column != null) {
//          return column.isNeedResultMapNullValue();
//      } else {
//          return false;
//      }
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.ResultMapResult#getSelect()
     */
    public String getSelect() {
        // not supported yet
        return "";
    }
}
