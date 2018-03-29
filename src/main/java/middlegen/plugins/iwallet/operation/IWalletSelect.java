/*
 * Taobao.com Inc.
 * Copyright (c) 2000-2004 All Rights Reserved.
 */
package middlegen.plugins.iwallet.operation;

import java.util.Vector;

import middlegen.Table;
import middlegen.plugins.iwallet.IWalletOperation;
import middlegen.plugins.iwallet.IWalletPlugin;
import middlegen.plugins.iwallet.IWalletTable;
import middlegen.plugins.iwallet.config.IWalletOperationConfig;
import middlegen.plugins.iwallet.util.DalUtil;

import org.apache.commons.lang.StringUtils;

import Zql.ZQuery;
import Zql.ZSelectItem;

/**
 * An implementation of "select" operation.
 *
 * @author Cheng Li
 *
 * @version $Id: IWalletSelect.java,v 1.4 2006/07/18 08:36:54 lusu Exp $
 */
public class IWalletSelect extends IWalletOperation {

    /** default java type to return the result of multiple records */
    public static final String DEFAULT_MANY_RETURN_TYPE_NO_PAGING = "java.util.List";

    /** default java type to return the result of multiple records with paging */
    public static final String DEFAULT_MANY_RETURN_TYPE_PAGING    = "com.iwallet.biz.common.util.PageList";
    public static final String OP_TYPE                            = "select";
    private String             parsedSqlForCount                  = null;

    /**
     * Constructor for IWalletSelect.
     */
    public IWalletSelect(IWalletOperationConfig conf) {
        super(conf);

        //向下兼容，当没有配置机密性及完整性时，不进行SQL拼接
        if ((opConfig.getTableConfig().getConfidentiality() != null) || (opConfig.getTableConfig().getIntegrity() != null)) {
            getFinalSql(opConfig);
        }

        if (PARAM_TYPE_OBJECT.equals(conf.getParamType())) {
            paramType = PARAM_TYPE_OBJECT;
        } else {
            // default
            paramType = PARAM_TYPE_PRIMITIVE;
        }

        if (MULTIPLICITY_MANY.equals(conf.getMultiplicity())) {
            multiplicity = MULTIPLICITY_MANY;
        } else {
            // default
            multiplicity = MULTIPLICITY_ONE;
        }
        //add by yuanxiao 2011-11-23---
        if (COUNT_TRUE.equals(conf.getCount())) {
            count = COUNT_TRUE;
        } else {
            // default
            count = COUNT_FALSE;
        }
        //----
    }

    /**
     * 机密性和完整性方案，获得配置后的SQL语句
     * @param opConfig
     */
    private void getFinalSql(IWalletOperationConfig opConfig) {
        //add by yuanxiao -------------
        //获得传入的SQL
        String sql = opConfig.getSqlParser().getSql();
        //获得select语句中 from之前的内容
        int indexFrom = StringUtils.indexOfAny(sql, new String[] { " from", " FROM" });
        //截取From之前的子串
        String ret = StringUtils.substring(sql, 0, indexFrom);
        //考虑select count(*)的情况
        int indexCount = StringUtils.indexOfAny(ret, new String[] { "count(*)", "COUNT(*)" });

        if (indexCount > 0) {
            return;
        }

        //判断是否是select *这种情况
        int allStart = StringUtils.indexOfAny(ret, "*");
        if (allStart > 0) {
            opConfig.setSql(sql);
        } else {
            //截取From之后的子串
            String finalRet = StringUtils.substring(sql, indexFrom + 5, sql.length());

            StringBuffer sb = new StringBuffer();

            sb.append(ret);

            if (opConfig.getTableConfig().getConfidentiality() != null) {
                sb.append(",").append(opConfig.getTableConfig().getConfidentiality()).append("_confidentiality");
            }
            if (opConfig.getTableConfig().getIntegrity() != null) {
                sb.append(",").append(opConfig.getTableConfig().getIntegrity()).append("_integrity");
            }
            sb.append(" from ").append(finalRet);

            String finalSql = sb.toString();

            opConfig.setSql(finalSql);
        }
    }

    protected String getReturnTypeOne() {
        if (opConfig.getResultMap() != null) {
            return ((IWalletTable) getTable()).getResultMap(opConfig.getResultMap()).getClassAttr();
        } else if (opConfig.getResultClass() != null) {
            return opConfig.getResultClass();
        } else {
            return getColumnType();
        }
    }

    public String getColumnType() {
        /**SqlParser parser = opConfig.getSqlParser();

        if (parser.isSelectItemSingle()) {
            ZSelectItem item = parser.getSelectItem();

            if (item.getAggregate() != null) {
                // the select item is an aggregate
                String aggregateFunc = item.getAggregate();

                if (logger.isDebugEnabled()) {
                    //                        logger.debug("The aggregate func is " + aggregateFunc);
                }

                if (aggregateFunc.equalsIgnoreCase("COUNT")) {
                    return "long";
                } else if (aggregateFunc.equalsIgnoreCase("SUM")
                           || aggregateFunc.equalsIgnoreCase("AVG")
                           || aggregateFunc.equalsIgnoreCase("MAX")
                           || aggregateFunc.equalsIgnoreCase("MIN")) {
                    String columnName = item.getColumn();
                    int indexStart = columnName.indexOf("(");
                    int indexEnd = columnName.indexOf(")", indexStart);

                    columnName = columnName.substring(indexStart + 1, indexEnd);

                    if (logger.isDebugEnabled()) {
                        //                            logger.debug("The column to be aggregated is " + columnName + ".");
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
        */

        String resultClass = opConfig.getResultClass();
        if (StringUtils.isNotBlank(resultClass)) {
            return resultClass;
        }

        return super.getColumnType();
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.Operation#getReturnTypeName()
     */
    public String getReturnType() {
        if (MULTIPLICITY_MANY.equals(multiplicity)) {
            if (isPaging()) {
                return DEFAULT_MANY_RETURN_TYPE_PAGING;
            }

            this.addImprotForGenericType();
            return DEFAULT_MANY_RETURN_TYPE_NO_PAGING;
        }

        return getReturnTypeOne();
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.Operation#getTemplateSuffix()
     */
    public String getTemplateSuffix() {
        return OP_TYPE;
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.IWalletOperation#isPaging()
     */
    public boolean isPaging() {
        return opConfig.isPaging();
    }

    /**
     * @param t
     *
     * @see middlegen.plugins.iwallet.IWalletOperation#setTable(middlegen.Table)
     */
    public void setTable(Table t) {
        super.setTable(t);

        // add additional imports used by method body
        if (isPaging()) {
            ((IWalletTable) t).addIbatisImport(IWalletPlugin.PAGINATOR_CLASS);
        }
    }

    /**
     * @return
     *
     * @see middlegen.plugins.iwallet.Operation#getSimpleReturnType()
     */
    public String getSimpleReturnType() {
        String simpleReturnType = DalUtil.getSimpleJavaType(getReturnType());
        if (StringUtils.equals("List", simpleReturnType)) {
            String itemType = this.getColumnType();
            if (StringUtils.equals(itemType, "long")) {
                itemType = "Long";
            } else if (StringUtils.equals(itemType, "int")) {
                itemType = "Integer";
            } else if (StringUtils.equals(itemType, "map")) {
                itemType = "java.util.Map<String, Object>";
            } else {
                itemType = DalUtil.getSimpleJavaType(itemType);
            }

            simpleReturnType += "<" + itemType + ">";
        }

        if (StringUtils.equals(simpleReturnType, "map")) {
            simpleReturnType = "java.util.Map<String, Object>";
        }

        return simpleReturnType;
    }

    /**
     * 增加泛型类的导入
     * <p>
     * 当方法返回类型为List时,我们将它转化为泛型,即返回List<???>类型;<br>
     * 此方法,就是针对这种情况,而增加对???类的导入<br>
     * 如果???类型为long,int,则不需要增加导入;
     * </p>
     * 
     * add by lejin,2009-7-31 下午03:14:27,
     * @author lejin 
     */
    private void addImprotForGenericType() {
        String itemType = this.getColumnType();
        if (StringUtils.equals(itemType, "long") || StringUtils.equals(itemType, "int")) {
            //类型为long,int,则不需要增加导入;
            return;
        } else {
            //add by lejin,2009-7-31 上午11:14:04, 在*DAO类和ibatis*DAO类中增加itemType的improt;
            ((IWalletTable) getTable()).addDaoImport(itemType);
            ((IWalletTable) getTable()).addIbatisImport(itemType);
            return;
        }
    }

    /**
     * @return
     */
    public String getMappedStatementType() {
        return OP_TYPE;
    }

    /**
     * @return
     */
    public String getMappedStatementResult() {
        if (opConfig.getResultMap() != null) {
            return "resultMap=\"" + opConfig.getResultMap() + "\"";
        } else {
            String result = getReturnTypeOne();

            if (((IWalletTable) getTable()).getQualifiedDOClassName().equals(result)) {
                return "resultMap=\"" + ((IWalletTable) getTable()).getResultMapId() + "\"";
            } else if (IWalletPlugin.MONEY_CLASS.equals(result)) {
                return "resultMap=\"" + IWalletPlugin.MONEY_RESULT_MAP_ID + "\"";
            } else {
                return "resultClass=\"" + result + "\"";
            }
        }
    }

    public String getStartRowName() {
        return "startRow";
    }

    public String getEndRowName() {
        return "endRow";
    }

    /**
     * @return
     */
    public String getMappedStatementSqlForPaging() {
        StringBuffer pagingSql = new StringBuffer();

        if (isHasSqlmap()) {
            pagingSql.append("SELECT * FROM (").append("SELECT T1.*, rownum linenum FROM (").append(getMappedStatementSqlNoAnnotation()).append(") T1 WHERE rownum &lt;= #").append(getEndRowName())
                .append("#").append(") T2 WHERE linenum &gt;= #").append(getStartRowName()).append("#");
        } else {
            pagingSql.append("SELECT * from (").append("SELECT T1.*, rownum linenum FROM (").append(getMappedStatementSqlNoAnnotation()).append(") T1 WHERE rownum <= #").append(getEndRowName())
                .append("#").append(") T2 WHERE linenum >= #").append(getStartRowName()).append("#");
        }

        return pagingSql.toString();
    }

    /**
     * 从将原始的SQL转换成一个统计数量的SQL.
     *
     * <p>
     * 将select子句转换为select count(*)，将order by子句去掉。
     *
     * @return
     */
    public String getMappedStatementSqlForCount() {
        if (isHasSqlmap()) {
            // 原始mapped statement - 可能为ibatis格式的动态statement
            String origMs = getMappedStatementSqlNoAnnotation();

            /*
             * 由于mapped statement不是标准的SQL，因此很难进行彻底地分析。这里采取一种简单的方法，
             * 根据关键字和一些必要的假设来将原始的mapped statement中的一些部分直接替换或删除。
             */
            int indexSelectStart = StringUtils.indexOfAny(origMs, new String[] { "select ", "SELECT " });
            int indexSelectEnd = StringUtils.indexOfAny(origMs, new String[] { "from ", "FROM " });
            int indexOrderByStart = StringUtils.indexOfAny(origMs, new String[] { "order by ", "ORDER BY " });
            int indexOrderByEnd = 0;

            if (indexOrderByStart > 0) {
                // 假设order by是最后一个子句
                indexOrderByEnd = StringUtils.indexOf(origMs, "]]>", indexOrderByStart);

                if (indexOrderByEnd < 0) {
                    indexOrderByEnd = origMs.length();
                }
            } else {
                indexOrderByStart = origMs.length();
            }

            // 转换后的mapped statement
            StringBuffer ret = new StringBuffer();

            ret.append(StringUtils.substring(origMs, 0, indexSelectStart));
            ret.append("SELECT count(*) ");
            ret.append(StringUtils.substring(origMs, indexSelectEnd, indexOrderByStart));

            if ((indexOrderByStart < origMs.length()) && (indexOrderByEnd < origMs.length())) {
                ret.append(StringUtils.substring(origMs, indexOrderByEnd));
            }

            return addSqlAnnotationForCount(ret.toString());
        } else {
            return addSqlAnnotationForCount(getMappedStatementSql(getParsedSqlForCount()));
        }
    }

    public String addSqlAnnotationForCount(String orgSql) {
        String idAnnotation = " ";
        String[] searchStrs = new String[] { "select", "SELECT" };
        int startOperation = StringUtils.indexOfAny(orgSql, searchStrs);
        if (-1 != startOperation) {
            String operation = StringUtils.substring(orgSql, 0, startOperation + 6);
            String afterOperation = StringUtils.substring(orgSql, startOperation + 7, orgSql.length());
            orgSql = operation + idAnnotation + afterOperation;
        }
        return orgSql;
    }

    /**
     *为了解决分页时mapping语句id渲染不成功问题
     *add by yuanxiao 
     * @return
     */
    public String getMappedStatementIdForCount(boolean needAppName) {
        //return getMappedStatementId(needAppName) + "-COUNT-FOR-PAGING";
        return super.getMappedStatementIdForCount();
    }

    /**
     * 只有这种方法才可以渲染getMappedStatementIdForCount方法
     * @return
     */
    public String getMappedStatementResultForCount() {
        // we use int because Paginator use int to store item count

        this.getMappedStatementIdForCount(true);

        return "resultType=\"long\"";
    }

    /**
     * @return
     */
    public String getParsedSqlForCount() {
        if (parsedSqlForCount == null) {
            ZQuery zst = (ZQuery) opConfig.getZst();
            ZQuery zstCount = new ZQuery();

            Vector select = new Vector();

            select.add(new ZSelectItem("count(*)"));
            zstCount.addSelect(select);

            zstCount.addFrom(zst.getFrom());
            zstCount.addWhere(zst.getWhere());

            // TODO: need to support more features? 
            parsedSqlForCount = zstCount.toString();
        }

        return parsedSqlForCount;
    }
}
