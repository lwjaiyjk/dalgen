/*
 * Taobao.com Inc.
 * Copyright (c) 2000-2004 All Rights Reserved.
 */
package middlegen.plugins.iwallet.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.digester.Digester;

import com.atom.dalgen.utils.LogUtils;

/**
 * The top class to hold dal configuration.
 */
public class IWalletConfig {
    /** 单例 */
    private static IWalletConfig                   instance        = null;

    /** a map of all configured tables */
    private static Map<String, IWalletTableConfig> tableConfigs    = new HashMap<String, IWalletTableConfig>();

    /** a map to convert from one java type to another java type */
    private Map<String, String>                    javaTypeMaps    = new HashMap<String, String>();

    /** a list of all configured sequences */
    private List<IWalletSeqConfig>                 seqConfigs      = new ArrayList<IWalletSeqConfig>();

    /** a list of included tables */
    private static List<String>                    includes        = new ArrayList<String>();

    /** 忽略的表名前缀列表 */
    private List<String>                           tablePrefixList = new ArrayList<String>();

    public static IWalletConfig getInstance() throws IWalletConfigException {
        if (instance == null) {
            LogUtils.log("DAL配置没有初始化。");
            throw new IWalletConfigException("DAL配置没有初始化。");
        }

        return instance;
    }

    /**
     * Add a table config.
     */
    public void addTableConfig(IWalletTableConfig tableConfig) {
        if (tableConfig != null) {
            tableConfigs.put(tableConfig.getSqlName().toLowerCase(), tableConfig);
        }
    }

    /**
     * Add a type mapping.
     */
    public void addJavaTypeMap(String fromType, String toType) {
        javaTypeMaps.put(fromType, toType);
    }

    /**
     * Add a type mapping.
     */
    public void addInclude(String table) {
        includes.add(table);
    }

    /**
     * Add a table name prefix to list.
     */
    public void addTablePrefix(String prefix) {
        tablePrefixList.add(prefix);
    }

    /**
     * Load configuration from file.
     */
    public static synchronized void init(File configFile) throws IWalletConfigException {
        if (instance != null) {
            return;
        }

        try {
            Digester digester = new Digester();

            // 传参是两个字符串，将他们作为一个String数组传进来
            digester.addCallMethod("tables/typemap", "addJavaTypeMap", 2, new String[] { "java.lang.String", "java.lang.String" });
            digester.addCallParam("tables/typemap", 0, "from");
            digester.addCallParam("tables/typemap", 1, "to");

            // support for include config
            digester.addCallMethod("tables/include", "addInclude", 1, new String[] { "java.lang.String" });
            digester.addCallParam("tables/include", 0, "table");

            // support for tableprefix config
            digester.addCallMethod("tables/tableprefix", "addTablePrefix", 1, new String[] { "java.lang.String" });
            digester.addCallParam("tables/tableprefix", 0, "prefix");

            // IWalletSeqConfig类用于展现SEQ属性的一些情况
            digester.addObjectCreate("tables/seq", IWalletSeqConfig.class);
            digester.addSetProperties("tables/seq", "name", "name");
            digester.addSetNext("tables/seq", "addSeqConfig");

            // 解析
            instance = new IWalletConfig();
            digester.push(instance);
            digester.setValidating(false);
            digester.parse(configFile);

            if (!includes.isEmpty()) {
                digester = new Digester();
                digester.setValidating(false);

                // parse basic table config
                // IWalletTableConfig类适用于展现表名.xml中的属性<table></table>中的属性。
                digester.addObjectCreate("table", IWalletTableConfig.class);
                digester.addSetProperties("table", "sqlname", "sqlName");
                digester.addSetProperties("table", "doname", "doName");
                digester.addSetProperties("table", "subpackage", "subPackage");
                digester.addSetProperties("table", "sequence", "sequence");

                digester.addSetProperties("table", "confidentiality", "confidentiality");
                digester.addSetProperties("table", "integrity", "integrity");
                digester.addSetProperties("table", "encodekeyname", "encodekeyname");
                digester.addSetProperties("table", "abstractkeyname", "abstractkeyname");
                digester.addSetProperties("table", "drmConfig", "drmConfig");
                digester.addSetProperties("table", "autoswitchdatasrc", "autoSwitchDataSrc");
                // 虚拟主键配置，insert时，当无主键或多主键时，虚拟字段为主键
                digester.addSetProperties("table", "dummypk", "dummyPk");

                // 票据ID
                digester.addSetProperties("table", "ticket", "ticket");
                digester.addSetProperties("table", "ticketName", "ticketName");
                digester.addSetProperties("table", "fmtNo", "fmtNo");
                digester.addSetProperties("table", "fmtNoName", "fmtNoName");
                digester.addSetProperties("table", "valve", "valve");

                // parse column
                // IWalletColumenConfig类表示列的名称和类型
                digester.addObjectCreate("table/column", IWalletColumnConfig.class);
                digester.addSetProperties("table/column", "name", "name");
                digester.addSetProperties("table/column", "javatype", "javaType");
                digester.addSetNext("table/column", "addColumn");

                // parse resultmap
                digester.addObjectCreate("table/resultMap", IWalletResultMapConfig.class);
                digester.addSetProperties("table/resultMap", "name", "name");
                digester.addSetProperties("table/resultMap", "type", "type");
                digester.addSetNext("table/resultMap", "addResultMap");
                // parse resultmap column
                digester.addObjectCreate("table/resultMap/column", IWalletColumnConfig.class);
                digester.addSetProperties("table/resultMap/column", "name", "name");
                digester.addSetProperties("table/resultMap/column", "javatype", "javaType");
                digester.addSetNext("table/resultMap/column", "addColumn");

                // parse sql
                digester.addObjectCreate("table/sql", IWalletSqlConfig.class);
                digester.addSetProperties("table/sql", "id", "id");
                digester.addSetProperties("table/sql", "escape", "escape");
                digester.addBeanPropertySetter("table/sql");
                digester.addSetNext("table/sql", "addSql");

                // parse copy
                digester.addObjectCreate("table/copy", CopyConfig.class);
                digester.addSetProperties("table/copy", "type", "type");
                digester.addBeanPropertySetter("table/copy", "copy");
                digester.addSetNext("table/copy", "addCopy");

                // parse operation
                digester.addObjectCreate("table/operation", IWalletOperationConfig.class);
                digester.addSetProperties("table/operation", "name", "name");
                digester.addSetProperties("table/operation", "paramtype", "paramType");
                digester.addSetProperties("table/operation", "multiplicity", "multiplicity");
                //add by yuanxiao 2011-11-23---
                digester.addSetProperties("table/operation", "count", "count");
                digester.addSetProperties("table/operation", "parameterClass", "parameterClass");
                //------
                digester.addSetProperties("table/operation", "paging", "paging");
                digester.addSetProperties("table/operation", "resultmap", "resultMap");
                digester.addSetProperties("table/operation", "resultclass", "resultClass");
                digester.addSetProperties("table/operation", "append", "append");

                digester.addObjectCreate("table/operation/extraparams/param", IWalletParamConfig.class);
                digester.addSetProperties("table/operation/extraparams/param", "name", "name");
                digester.addSetProperties("table/operation/extraparams/param", "javatype", "javaType");
                digester.addSetProperties("table/operation/extraparams/param", "generictype", "genericType");
                digester.addSetNext("table/operation/extraparams/param", "addExtraParam");

                digester.addCallMethod("table/operation/sql", "setSql", 0);
                digester.addCallMethod("table/operation/sqlmap", "setSqlmap", 0);
                // added by yangyanzhao 2010-02-08
                digester.addCallMethod("table/operation/description", "setDescription", 0);
                digester.addSetNext("table/operation", "addOperation");
                digester.addSetNext("table", "addTableConfig");

                for (String table : includes) {
                    digester.clear();
                    digester.push(instance);
                    digester.parse(new File(configFile.getParentFile(), table));
                }
            }
        } catch (Exception e) {
            // clear table config
            tableConfigs = new HashMap<String, IWalletTableConfig>();
            throw new IWalletConfigException("Parse configuration file " + configFile.getPath() + " exception！", e);
        }
    }

    /**
     * Get the table configuration by its database name.
     */
    public IWalletTableConfig getTableConfig(String sqlName) {
        if (sqlName != null) {
            return tableConfigs.get(sqlName.toLowerCase());
        } else {
            return null;
        }
    }

    /**
     * Map a java type to a more proper one.
     */
    public String getMappedJavaType(String javaType) {
        if (javaTypeMaps.containsKey(javaType)) {
            return javaTypeMaps.get(javaType);
        } else {
            return javaType;
        }
    }

    /**
     * @param
     */
    public void addSeqConfig(IWalletSeqConfig seqConfig) {
        if (seqConfig != null) {
            seqConfigs.add(seqConfig);
        }
    }

    /**
     * @return
     */
    public List<IWalletSeqConfig> getSeqConfigs() {
        return seqConfigs;
    }

    /**
     * 取得所有的TableName。
     */
    public List<String> getAllTableNames() {
        List<String> tableNames = new ArrayList<String>(tableConfigs.keySet());
        Collections.sort(tableNames);

        return tableNames;
    }

    /**
     * 取得要忽略的表名前缀列表
     */
    public List<String> getTablePrefixList() {
        return tablePrefixList;
    }

}
