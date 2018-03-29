/*
 * Copyright (c) 2001, Aslak Hellesøy, BEKK Consulting
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * - Neither the name of BEKK Consulting nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */
package middlegen;

import java.beans.Introspector;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.atom.dalgen.utils.LogUtils;

/**
 * Various static utility methods
 *
 * @author <a href="mailto:aslak.hellesoy@bekk.no">Aslak Helles</a>
 * @created 3. oktober 2001
 * @version $Id: Util.java,v 1.1 2005/10/25 14:59:22 lusu Exp $
 * @todo move to middlegen.jdbc package
 */
public class Util {

    /**
     * Gets the QualifiedClassName attribute of the Util class
     */
    public static String getQualifiedClassName(String packageName, String className) {
        String result;
        if ("".equals(packageName)) {
            result = className;
        } else {
            result = packageName + "." + className;
        }
        return result;
    }

    /**
     * Describe what the method does
     *
     * @todo-javadoc Write javadocs for method
     * @todo-javadoc Write javadocs for method parameter
     * @todo-javadoc Write javadocs for method parameter
     * @todo-javadoc Write javadocs for return value
     * @param a Describe what the parameter does
     * @param b Describe what the parameter does
     * @return Describe the return value
     */
    public static boolean equals(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }
        if (a != null && a.equals(b)) {
            return true;
        }
        return false;
    }

    /**
     * Ensures that the string is not null
     *
     * @param s a string
     * @return an empty string if the given string was null, else the string.
     */
    public static String ensureNotNull(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    /**
     * Converts a database name (table or column) to a java name (first letter
     * decapitalised). employee_name -> employeeName
     *
     * @todo-javadoc Write javadocs for method parameter
     * @todo-javadoc Write javadocs for method parameter
     * @param s Describe what the parameter does
     * @return the converted database name
     * @return != null
     * @pre s != null
     */
    public static String decapitalise(String s) {
        String result = Introspector.decapitalize(s);
        if ("class".equals(result)) {
            // "class" is illegal becauseOf Object.getClass() clash
            result = "clazz";
        }
        return result;
    }

    /**
     * Describe what the method does
     *
     * @todo-javadoc Write javadocs for method
     * @todo-javadoc Write javadocs for method parameter
     * @todo-javadoc Write javadocs for return value
     * @param s Describe what the parameter does
     * @return Describe the return value
     */
    public static boolean bool(String s) {
        return "true".equals(s);
    }

    /**
     * Describe what the method does
     *
     * @todo-javadoc Write javadocs for method
     * @todo-javadoc Write javadocs for method parameter
     * @todo-javadoc Write javadocs for return value
     * @param b Describe what the parameter does
     * @return Describe the return value
     */
    public static String string(boolean b) {
        return b ? "true" : "false";
    }

    /**
     * Describe what the method does
     *
     * @todo-javadoc Write javadocs for method
     * @todo-javadoc Write javadocs for method parameter
     * @todo-javadoc Write javadocs for return value
     * @param name Describe what the parameter does
     * @return Describe the return value
     */
    public static String pluralise(String name) {
        LogUtils.log("pluralise:" + name);
        String result = name;
        if (name.length() == 1) {
            // just append 's'
            result += 's';
        } else {
            String lower = name.toLowerCase();
            char secondLast = lower.charAt(name.length() - 2);
            if (!isVowel(secondLast) && lower.endsWith("y")) {
                // city, body etc --> cities, bodies
                result = name.substring(0, name.length() - 1) + "ies";
            } else if (lower.endsWith("ch") || lower.endsWith("s")) {
                // switch --> switches  or bus --> buses
                result = name + "es";
            } else {
                result = name + "s";
            }
        }
        LogUtils.log("pluralised " + name + " to " + result);
        return result;
    }

    /**
     * Describe what the method does
     */
    public static String singularise(String name) {
        LogUtils.log("singularise:" + name);

        String result = name;
        String lower = name.toLowerCase();
        if (lower.endsWith("ies")) {
            // cities --> city
            result = name.substring(0, name.length() - 3) + "y";
        } else if (lower.endsWith("ches") || lower.endsWith("ses")) {
            // switches --> switch or buses --> bus
            result = name.substring(0, name.length() - 2);
        } else if (lower.endsWith("s")) {
            // customers --> customer
            result = name.substring(0, name.length() - 1);
        }

        LogUtils.log("singularised " + name + " to " + result);

        return result;
    }

    /**
     * Describe what the method does
     *
     * @todo-javadoc Write javadocs for method
     * @todo-javadoc Write javadocs for method parameter
     * @todo-javadoc Write javadocs for return value
     * @param s Describe what the parameter does
     * @return Describe the return value
     */
    public static String capitalise(String s) {
        if (s.equals("")) {
            return "";
        }
        if (s.length() == 1) {
            return s.toUpperCase();
        } else {
            String caps = s.substring(0, 1).toUpperCase();
            String rest = s.substring(1);
            return caps + rest;
        }
    }

    /**
     * Gets the Vowel attribute of the Util object
     *
     * @todo-javadoc Write javadocs for method parameter
     * @param c Describe what the parameter does
     * @return The Vowel value
     */
    private static final boolean isVowel(char c) {
        boolean vowel = false;
        vowel |= c == 'a';
        vowel |= c == 'e';
        vowel |= c == 'i';
        vowel |= c == 'o';
        vowel |= c == 'u';
        vowel |= c == 'y';
        return vowel;
    }

    /**
     * �޳��xml�ļ���DOCTYPE����
     * @param xmlFileName
     * @return
     * @throws Exception
     */
    public static String trimDocType(File xmlFile) throws Exception {
        InputStream input = new FileInputStream(xmlFile);
        byte[] b = new byte[input.available()];
        input.read(b);
        input.close();
        String docStr = new String(b);
        if (docStr.indexOf("DOCTYPE") == -1 || docStr.indexOf("<!") == -1) {
            return docStr;
        }

        int docTypeStart = 0;
        int docTypeEnd = 0;
        for (int i = 0; i < b.length; i++) {
            if (b[i] == '<' && b[i + 1] == '!') {
                docTypeStart = i;
            } else if (docTypeStart != 0 && b[i] == '>') {
                docTypeEnd = i;
                break;
            }
        }
        String docTypeStr = docStr.substring(docTypeStart, docTypeEnd + 1);
        docStr = docStr.replaceAll(docTypeStr, "");
        return docStr;
    }

}
