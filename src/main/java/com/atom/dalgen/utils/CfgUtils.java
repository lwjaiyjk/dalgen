/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.atom.dalgen.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 配置工具类
 * 
 * @author shizihu
 * @version $Id: CfgUtils.java, v 0.1 2012-8-22 下午07:20:34 shizihu Exp $
 */
public final class CfgUtils {

    /** 配置参数 */
    private static final Map<String, String> cfgs = initCfgMap();

    /**
     * 初始化
     */
    private static final Map<String, String> initCfgMap() {
        Map<String, String> map = new HashMap<String, String>();

        String path = new File(".").getAbsolutePath();
        String file = FilenameUtils.normalize(path + "/system-config.properties");

        InputStream is = null;
        try {
            is = new FileInputStream(file);
            Properties props = new Properties();
            props.load(is);

            for (Object key : props.keySet()) {
                map.put(String.valueOf(key), String.valueOf(props.get(key)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }

        for (Map.Entry<String, String> entry : map.entrySet()) {
            LogUtils.log("系统参数，KEY[" + entry.getKey() + "], Value[" + entry.getValue() + "].");
        }

        return map;
    }

    public static final String findValue(String key) {
        String value = cfgs.get(key);

        LogUtils.log("[系统参数]-查询系统参数, Key[" + key + "], Value[" + value + "].");

        return value;
    }

    public static final String findValue(String key, String defaultValue) {
        String value = findValue(key);

        if (StringUtils.isBlank(value)) {
            value = defaultValue;
        }

        LogUtils.log("[系统参数]-查询系统参数, Key[" + key + "], Value[" + value + "].");

        return value;
    }

    public static final Set<String> findFilterKeys() {
        Set<String> keys = new HashSet<String>();

        for (String key : cfgs.keySet()) {
            if (StringUtils.startsWithIgnoreCase(key, "filter.")) {
                keys.add(key);
            }
        }

        return keys;
    }

    // ~~~~~~~~~~~~~~ methods ~~~~~~~~~~~~~~ //

    private static final String DV = "NULL";

    public static final String getAppName() {
        return findValue("app.name", DV);
    }

    public static final String getSchema() {
        return findValue("jdbc.schema", "");
    }

    public static final String getCatalog() {
        return findValue("jdbc.catalog");
    }

    public static final String getSortColumns() {
        return findValue("sort.columns");
    }

}
