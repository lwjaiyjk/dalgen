/*
 * Taobao.com Inc.
 * Copyright (c) 2000-2004 All Rights Reserved.
 */
package middlegen.plugins.iwallet.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import middlegen.plugins.iwallet.IWalletPlugin;
import middlegen.plugins.iwallet.config.IWalletConfig;
import middlegen.plugins.iwallet.config.IWalletConfigException;

import org.apache.commons.lang.StringUtils;

import com.atom.dalgen.ObjectFactory;
import com.atom.dalgen.utils.Utils;

/**
 * A simple utility class handles misc tasks.
 *
 * @author Cheng Li
 *
 * @version $Id: Util.java,v 1.1 2004/12/24 07:34:20 baobao Exp $
 */
public class DalUtil {

    public static boolean inTabs(String tableName) throws IWalletConfigException {
        String tabs = ObjectFactory.getTabs();

        if (tabs == null || "*".equals(tabs)) {
            return true;
        }

        String[] tables = StringUtils.split(tabs, ",");
        for (String table : tables) {
            if (StringUtils.equalsIgnoreCase(tableName, table)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 去掉表名前缀
     */
    public static String removeTablePrefix(String tableName) throws IWalletConfigException {
        List<String> tablePrefixList = IWalletConfig.getInstance().getTablePrefixList();
        for (String prefix : tablePrefixList) {
            if (tableName.toLowerCase().startsWith(prefix)) {
                tableName = tableName.substring(prefix.length());
                break;
            }
        }

        return tableName;
    }

    public static String dbNameToVariableName(String s) {
        if (StringUtils.isBlank(s)) {
            return s;
        }
        StringBuffer result = new StringBuffer();

        boolean capitalize = true;
        boolean lastCapital = false;
        boolean lastDecapitalized = false;
        String p = null;
        for (int i = 0; i < s.length(); i++) {
            String c = s.substring(i, i + 1);
            if ("_".equals(c) || " ".equals(c)) {
                capitalize = true;
                continue;
            }

            if (c.toUpperCase().equals(c)) {
                if (lastDecapitalized && !lastCapital) {
                    capitalize = true;
                }
                lastCapital = true;
            } else {
                lastCapital = false;
            }

            if (capitalize) {
                if (p == null || !p.equals("_")) {
                    result.append(c.toUpperCase());
                    capitalize = false;
                    p = c;
                } else {
                    result.append(c.toLowerCase());
                    capitalize = false;
                    p = c;
                }
            } else {
                result.append(c.toLowerCase());
                lastDecapitalized = true;
                p = c;
            }

        }
        String r = result.toString();
        return r;
    }

    public static String getSimpleJavaType(String type) {
        if (StringUtils.isBlank(type)) {
            return type;
        }

        int index = type.lastIndexOf(".");

        if (index >= 0) {
            return type.substring(index + 1);
        } else {
            return type;
        }
    }

    /**
     * Get a default value for a given java type.
     *
     * @param type
     * @return
     */
    public static String getDefaultValue(String type) {
        if (StringUtils.isBlank(type)) {
            return "null";
        } else if (type.equals(IWalletPlugin.MONEY_CLASS)) {
            // special case
            return "0";
        } else if (type.lastIndexOf(".") > 0) {
            return "null";
        } else if (Character.isUpperCase(type.charAt(0))) {
            return "null";
        } else if ("boolean".equals(type)) {
            return "false";
        } else {
            return "0";
        }
    }

    public static boolean isNeedImport(String type) {
        if (StringUtils.isBlank(type)) {
            return false;
        }

        if (type.startsWith("java.lang.")) {
            return false;
        }

        if ((type.indexOf(".") < 0) && Character.isLowerCase(type.charAt(0))) {
            return false;
        }

        return true;
    }

    public static String toUpperCaseWithDash(String name) {
        String newName;

        if (name == null) {
            newName = name;
        } else {
            newName = Utils.toUpperCaseWithUnderscores(name);

            if (newName != null) {
                newName = newName.replaceAll("_", "-");
            }
        }

        return newName;
    }

    /**
     * Compares the contents of two files.
     *
     * <p>simple but sub-optimal comparision algorithm.  written for
     * working rather than fast. Better would be a block read into
     * buffers followed by long comparisions apart from the final 1-7
     * bytes.</p>
     *
     * <p>Borrowed from Ant. </p>
     */
    public static boolean contentEquals(File f1, File f2) throws IOException {
        if (f1.exists() != f2.exists()) {
            return false;
        }

        if (!f1.exists()) {
            // two not existing files are equal
            return true;
        }

        if (f1.isDirectory() || f2.isDirectory()) {
            // don't want to compare directory contents for now
            return false;
        }

        if (f1.equals(f2)) {
            // same filename => true
            return true;
        }

        if (f1.length() != f2.length()) {
            // different size =>false
            return false;
        }

        InputStream in1 = null;
        InputStream in2 = null;

        try {
            in1 = new BufferedInputStream(new FileInputStream(f1));
            in2 = new BufferedInputStream(new FileInputStream(f2));

            int expectedByte = in1.read();

            while (expectedByte != -1) {
                if (expectedByte != in2.read()) {
                    return false;
                }

                expectedByte = in1.read();
            }

            if (in2.read() != -1) {
                return false;
            }

            return true;
        } finally {
            if (in1 != null) {
                try {
                    in1.close();
                } catch (IOException e) {
                }
            }

            if (in2 != null) {
                try {
                    in2.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
