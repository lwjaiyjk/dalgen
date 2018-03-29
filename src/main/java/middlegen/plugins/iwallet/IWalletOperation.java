/*
 * Taobao.com Inc.
 * Copyright (c) 2000-2004 All Rights Reserved.
 */
package middlegen.plugins.iwallet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import middlegen.PreferenceAware;
import middlegen.Table;
import middlegen.javax.Sql2Java;
import middlegen.plugins.iwallet.config.IWalletOperationConfig;
import middlegen.plugins.iwallet.config.IWalletParamConfig;
import middlegen.plugins.iwallet.util.DalUtil;
import middlegen.plugins.iwallet.util.SqlParser;

import org.apache.commons.lang.StringUtils;

import Zql.ZSelectItem;

import com.atom.dalgen.utils.CfgUtils;
import com.atom.dalgen.utils.LogUtils;

/**
 * A decoration class for a dao operation.
 * 
 * @author Cheng Li
 * 
 * @version $Id: IWalletOperation.java,v 1.2 2005/05/25 06:09:15 lusu Exp $
 */
public abstract class IWalletOperation extends PreferenceAware implements Operation {
    public static final String       MAPPED_STATEMENT_PREFIX = "";
    public static final String       PARAM_TYPE_OBJECT       = "object";
    public static final String       PARAM_TYPE_PRIMITIVE    = "primitive";
    public static final String       MULTIPLICITY_ONE        = "one";
    public static final String       MULTIPLICITY_MANY       = "many";
    //add by yuanxiao 2011-11-23----
    public static final String       COUNT_TRUE              = "true";
    public static final String       COUNT_FALSE             = "false";

    /** an operation config instance holds operation configuration. */
    protected IWalletOperationConfig opConfig;

    /** the table instance */
    private Table                    table;

    /**
     * a list of all method parameters, each one is an instance of IWalletParameter
     */
    protected List<IWalletParameter> objectParams            = new ArrayList<IWalletParameter>();

    /**
     * a list of all method parameters, each one is an instance of IWalletParameter
     */
    protected List<IWalletParameter> primitiveParams         = new ArrayList<IWalletParameter>();

    /** the type of how to pass parameters to dao */
    protected String                 paramType;

    /** the multiplicity of results */
    protected String                 multiplicity;

    //add by yuanxiao 2011-11-23---
    protected String                 count;

    protected String                 parameterClass;

    /**
     * Constructor for IWalletOperation.
     */
    public IWalletOperation(IWalletOperationConfig conf) {
        super();

        opConfig = conf;
    }

    /**
     * @return
     * 
     * @see middlegen.PreferenceAware#prefsPrefix()
     */
    protected String prefsPrefix() {
        return "java-method";
    }

    /**
     * @return
     * 
     * @see middlegen.plugins.iwallet.Operation#getName()
     */
    public String getName() {
        return opConfig.getName();
    }

    /**
     * @return
     * 
     * @see middlegen.plugins.iwallet.Operation#getTable()
     */
    public Table getTable() {
        return table;
    }

    /**
     * @param table
     */
    public void setTable(Table t) {
        table = t;

        setupParams();

        IWalletTable iwTable = (IWalletTable) t;

        if (getParams().size() > 1) {
            // because we need a map to bundle all params.
            iwTable.addIbatisImport("java.util.Map");
            iwTable.addIbatisImport("java.util.HashMap");
        }

        // add imports for return type
        iwTable.addDaoImport(getReturnType());

        // add imports for all exception type
        iwTable.addDaoImports(getExceptions());

        // add imports for ibatis
        iwTable.addIbatisImports(getIbatisExceptions());
    }

    /**
     * @return
     * 
     * @see middlegen.plugins.iwallet.Operation#getParsedSql()
     */
    public String getParsedSql( ) {
        return opConfig.getZst().toString();
    }

    /**
     * @return
     */
    public SqlParser getSqlParser() {
        return opConfig.getSqlParser();
    }

    /**
     * @return
     * 
     * @see middlegen.plugins.iwallet.Operation#getDefaultReturnValue()
     */
    public String getDefaultReturnValue() {
        return DalUtil.getDefaultValue(getReturnType());
    }

    /**
     * @return
     */
    public List getParams() {
        if (PARAM_TYPE_OBJECT.equals(paramType)) {
            return objectParams;
        } else {
            // as default;
            return primitiveParams;
        }
    }

    /**
     * @return
     */
    public IWalletParameter getParam() {
        if (getParams().size() == 1) {
            return (IWalletParameter) getParams().get(0);
        } else {
            return null;
        }
    }

    /**
     * @return
     * 
     * @see middlegen.plugins.iwallet.Operation#getExceptions()
     */
    public List getExceptions() {
        List list = new ArrayList();

//        list.add(IWalletPlugin.DATA_ACCESS_EXCEPTION_CLASS);

        return list;
    }

    /**
     * @return
     * 
     * @see middlegen.plugins.iwallet.Operation#getSimpleExceptions()
     */
    public List getSimpleExceptions() {
        List list = getExceptions();
        List newList = new ArrayList();

        for (int i = 0; i < list.size(); i++) {
            String exception = (String) list.get(i);

            newList.add(DalUtil.getSimpleJavaType(exception));
        }

        return newList;
    }

    public String getMappedStatementId() {
        return getMappedStatementId(false);
    }

    /**
     * @return
     */
    public String getMappedStatementId(boolean needAppName) {
        String appName = needAppName ? DalUtil.toUpperCaseWithDash(CfgUtils.getAppName()) + "-" : "";
        return MAPPED_STATEMENT_PREFIX + appName + DalUtil.toUpperCaseWithDash(((IWalletTable) getTable()).getBaseClassName()) + "-" + DalUtil.toUpperCaseWithDash(getName());
    }

    /**
     * @param origSql
     * @return
     */
    protected String getMappedStatementSql(String origSql) {
        if (StringUtils.isBlank(origSql)) {
            return origSql;
        }

        StringBuffer msSql = new StringBuffer();

        int startIndex = 0;
        int endIndex = origSql.indexOf("?");
        Iterator iParams = primitiveParams.iterator();

        while ((endIndex >= 0) && iParams.hasNext()) {
            msSql.append(origSql.substring(startIndex, endIndex));

            IWalletParameter param = (IWalletParameter) iParams.next();
            String paramName;

            if (PARAM_TYPE_PRIMITIVE.equals(paramType) && (primitiveParams.size() == 1)) {
                paramName = "value";
            } else {
                paramName = param.getName();
            }

            // deal with Money mapping
            if ("Money".equals(param.getSimpleJavaType())) {
                paramName = paramName + ".cent";
            }

            // // add by zhaoxu 2007-04-11 -->>>
            // String sqlType = param.getSqlType();
            // if ("VARCHAR2".equals(sqlType)) {
            // sqlType = "VARCHAR";
            // } else if (sqlType.indexOf("(") > 0) {
            // sqlType = sqlType.substring(0, sqlType.indexOf("("));
            // }
            //
            // if (sqlType != null && sqlType.length() > 0) {
            // paramName = paramName + ":" + sqlType;
            // }
            
            //拼装sql语句
            msSql.append("#").append(paramName).append("#");

            startIndex = endIndex + 1;
            endIndex = origSql.indexOf("?", startIndex);
        }

        msSql.append(origSql.substring(startIndex));

        String append = opConfig.getAppend();
        if (StringUtils.isNotBlank(append)) {
            msSql.append(" ").append(append);
        }

        return msSql.toString();
    }

    /**
     * Replace all parameter placeholders in parsedSql with their corresponding
     * names.
     * 
     * @return
     */
    public String getMappedStatementSql() {
        String sql = null;
        if (opConfig.isHasSqlmap()) {
            // TODO: optimize the logic
            List params = getParams();
            List paramNames = new ArrayList();

            for (int i = 0; i < params.size(); i++) {
                paramNames.add(((IWalletParameter) params.get(i)).getName());
            }
            sql = opConfig.getSqlmap(paramNames);
        } else {
            sql = getMappedStatementSql(getParsedSql());
        }
        return addSqlAnnotation(sql);
    }

    // added by yangyanzhao 2009-11-11
    public String getMappedStatementSqlNoAnnotation() {
        String sql = null;
        if (opConfig.isHasSqlmap()) {
            // TODO: optimize the logic
            List params = getParams();
            List paramNames = new ArrayList();

            for (int i = 0; i < params.size(); i++) {
                paramNames.add(((IWalletParameter) params.get(i)).getName());
            }
            return opConfig.getSqlmap(paramNames);
        } else {
            return getMappedStatementSql(getParsedSql());
        }
    }

    // added by yangyanzhao 2009-11-11
    public String addSqlAnnotation(String orgSql) {
        String idAnnotation = " ";
        String[] searchStrs = new String[] { "select", "SELECT", "insert", "INSERT", "delete", "DELETE", "update", "UPDATE" };
        int startOperation = StringUtils.indexOfAny(orgSql, searchStrs);
        if (-1 != startOperation) {
            String operation = StringUtils.substring(orgSql, 0, startOperation + 6);
            String afterOperation = StringUtils.substring(orgSql, startOperation + 7, orgSql.length());
            orgSql = operation + idAnnotation + afterOperation;
        }
        return orgSql;
    }

    /**
     * @return
     */
    public String getParamType() {
        return paramType;
    }

    /**
     * @return
     */
    public String getMultiplicity() {
        return multiplicity;
    }

    //add by yuanxiao 2011-11-23---
    public String getCount() {
        return count;
    }

    public String getParameterClass() {
        return opConfig.getParameterClass();
    }

    /**
     */
    protected void setupParams() {
        IWalletTable iwTable = (IWalletTable) getTable();

        IWalletParameter param = new IWalletParameter(this, iwTable.getSingularisedVariableName());

        param.setPlugin(getPlugin());
        objectParams.add(param);

        if (PARAM_TYPE_OBJECT.equals(paramType)) {
            iwTable.addDaoImport(param.getQualifiedJavaType());
        }

        List rawParams = getSqlParser().getParams();

        Map usedNames = new HashMap();

        for (Iterator i = rawParams.iterator(); i.hasNext();) {
            String paramName = (String) i.next();

            if (!usedNames.containsKey(paramName)) {
                param = new IWalletParameter(this, paramName);
                usedNames.put(paramName, new Integer(1));
            } else {
                int suffix = ((Integer) usedNames.get(paramName)).intValue();

                suffix++;
                param = new IWalletParameter(this, paramName, String.valueOf(suffix));
                usedNames.put(paramName, new Integer(suffix));
            }

            // add by zhaoxu 2007-04-11 -->>>
            String sqlType = null;
            try {
                sqlType = iwTable.getColumn(paramName).getSqlTypeName();
                param.setSqlType(sqlType);
            } catch (Exception e) {
                ;
            }
            // add by zhaoxu 2007-04-11 --<<<
            param.setPlugin(getPlugin());
            primitiveParams.add(param);

            if (PARAM_TYPE_PRIMITIVE.equals(paramType)) {
                iwTable.addDaoImport(param.getQualifiedJavaType());
            }
        }

        // add additional parameters needed by extraparams
        if (!opConfig.getExtraParams().isEmpty()) {
            for (IWalletParamConfig paramConfig : opConfig.getExtraParams()) {
                param = new IWalletParameter(this, paramConfig.getName());
                param.setJavaType(paramConfig.getJavaType());
                param.setGenericType(paramConfig.getGenericType());

                param.setPlugin(getPlugin());

                if (PARAM_TYPE_OBJECT.equals(paramType)) {
                    objectParams.add(param);
                } else {
                    primitiveParams.add(param);
                }

                iwTable.addDaoImport(param.getQualifiedJavaType());
                iwTable.addIbatisImport(param.getQualifiedJavaType());
            }
        }

        // add additional parameters needed by paging
        if (isPaging()) {
            param = new IWalletParameter(this, IWalletParameter.PARAM_NAME_PAGE_SIZE);

            param.setPlugin(getPlugin());

            if (PARAM_TYPE_OBJECT.equals(paramType)) {
                objectParams.add(param);
            } else {
                primitiveParams.add(param);
            }

            param = new IWalletParameter(this, IWalletParameter.PARAM_NAME_PAGE_NUM);
            param.setPlugin(getPlugin());

            if (PARAM_TYPE_OBJECT.equals(paramType)) {
                objectParams.add(param);
            } else {
                primitiveParams.add(param);
            }
        }
    }

    /**
     * @return
     */
    public List getIbatisExceptions() {
        List list = new ArrayList();

        return list;
    }

    /**
     * 
     * @return
     */
    public boolean isReturnTypePrimitive() {
        // simplistic approach
        return Sql2Java.isPrimitive(getReturnType());
    }

    /**
     * @return
     */
    public String getReturnTypeForPrimitive() {
        return Sql2Java.getClassForPrimitive(getReturnType());
    }

    /**
     * @return
     */
    public String getSimpleReturnTypeForPrimitive() {
        return DalUtil.getSimpleJavaType(getReturnTypeForPrimitive());
    }

    /**
     * @return
     */
    public boolean isPaging() {
        return false;
    }

    /**
     * @return
     */
    public boolean isHasSqlmap() {
        return opConfig.isHasSqlmap();
    }

    public String getDescription() {
        return StringUtils.isNotBlank(opConfig.getDescription()) ? opConfig.getDescription() : "No descripions";
    }

    // add by yuanxiao--这个方法其实没什么作用，只是为了区分返回List情况下的泛型
    public int getField() {

        SqlParser parser = opConfig.getSqlParser();
        String ret = parser.getSql();

        this.getAllField();
        // 获得逗号的位置
        int commaStart = StringUtils.indexOfAny(ret, ",");
        // 获得第一个from的位置
        // int fromStart = StringUtil.indexOfAny(ret, "from");
        // 如果逗号的位置小于from的位置，说明此次操作的参数不止一个
        // if (commaStart < fromStart) {
        // return true;
        // }
        // else if (commaStart == -1) {
        // return false;
        // }
        return commaStart;
    }

    // add by yuanxiao --为了区分是select * 的情况
    public int getAllField() {
        SqlParser parser = opConfig.getSqlParser();
        String ret = parser.getSql();
        int start = StringUtils.indexOfAny(ret, "*");
        return start;
    }

    public String getColumnType() {
        // 为了运行getFiled方法，只想到这种方法了
        this.getField();

        SqlParser parser = opConfig.getSqlParser();

        if (parser.isSelectItemSingle()) {
            ZSelectItem item = parser.getSelectItem();

            if (item.getAggregate() != null) {
                // the select item is an aggregate
                String aggregateFunc = item.getAggregate();

                if (LogUtils.get().isDebugEnabled()) {
                    //  LogUtils.get().debug("The aggregate func is " + aggregateFunc);
                }

                if (aggregateFunc.equalsIgnoreCase("COUNT")) {
                    return "long";
                } else if (aggregateFunc.equalsIgnoreCase("SUM") || aggregateFunc.equalsIgnoreCase("AVG") || aggregateFunc.equalsIgnoreCase("MAX") || aggregateFunc.equalsIgnoreCase("MIN")) {
                    String columnName = item.getColumn();
                    int indexStart = columnName.indexOf("(");
                    int indexEnd = columnName.indexOf(")", indexStart);

                    columnName = columnName.substring(indexStart + 1, indexEnd);

                    if (LogUtils.get().isDebugEnabled()) {
                        //  LogUtils.get().debug("The column to be aggregated is " +
                        // columnName + ".");
                    }

                    return ((IWalletColumn) (getTable().getColumn(columnName))).getJavaType();
                } else {
                    // can not happen
                    return "void";
                }
            } else {
                return ((IWalletColumn) (getTable().getColumn(item.getColumn()))).getJavaType();
            }
        } else {
            return ((IWalletTable) getTable()).getQualifiedDOClassName();
        }
    }

    /**
    * 解决sqlmapping文件id显示不成功问题
    */
    public String getMappedStatementIdForCount() {
        boolean needAppName = true;
        return getMappedStatementId(needAppName) + "-COUNT-FOR-PAGING";
    }

}
