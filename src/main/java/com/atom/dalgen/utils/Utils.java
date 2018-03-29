/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.atom.dalgen.utils;

import java.util.HashMap;
import java.util.Map;

import middlegen.plugins.iwallet.util.DalUtil;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 工具类
 * 
 * @author shizihu
 * @version $Id: Utils.java, v 0.1 2012-8-23 下午05:23:32 shizihu Exp $
 */
public final class Utils {

    /**
     * 工具类
     */
    public static Map<String, Object> findUtils() {
        Map<String, Object> utils = new HashMap<String, Object>();

        utils.put("Utils", new Utils());
        utils.put("DalUtil", new DalUtil());

        utils.put("IOUtils", new IOUtils());
        utils.put("StringUtils", new StringUtils());

        utils.put("CfgUtils", new CfgUtils());
        utils.put("IndexUtils", new IndexUtils());

        return utils;
    }

    /**
     * 将字符串转换成下划线分隔的大写字符串。
     * 
     * <p>
     * 如果字符串是<code>null</code>则返回<code>null</code>。
     * <pre>
     * StringUtil.toUpperCaseWithUnderscores(null)  = null
     * StringUtil.toUpperCaseWithUnderscores("")    = ""
     * StringUtil.toUpperCaseWithUnderscores("aBc") = "A_BC"
     * StringUtil.toUpperCaseWithUnderscores("aBc def") = "A_BC_DEF"
     * StringUtil.toUpperCaseWithUnderscores("aBc def_ghi") = "A_BC_DEF_GHI"
     * StringUtil.toUpperCaseWithUnderscores("aBc def_ghi 123") = "A_BC_DEF_GHI_123"
     * StringUtil.toUpperCaseWithUnderscores("__a__Bc__") = "__A__BC__"
     * </pre>
     * </p>
     * 
     * <p>
     * 此方法会保留除了空白以外的所有分隔符。
     * </p>
     *
     * @param str 要转换的字符串
     *
     * @return 下划线分隔的大写字符串，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String toUpperCaseWithUnderscores(String str) {
        return UPPER_CASE_WITH_UNDERSCORES_TOKENIZER.parse(str);
    }

    /**
     * 解析出下列语法所构成的<code>SENTENCE</code>。
     * <pre>
     *  SENTENCE = WORD (DELIMITER* WORD)*
     * 
     *  WORD = UPPER_CASE_WORD | LOWER_CASE_WORD | TITLE_CASE_WORD | DIGIT_WORD
     * 
     *  UPPER_CASE_WORD = UPPER_CASE_LETTER+
     *  LOWER_CASE_WORD = LOWER_CASE_LETTER+
     *  TITLE_CASE_WORD = UPPER_CASE_LETTER LOWER_CASE_LETTER+
     *  DIGIT_WORD      = DIGIT+
     * 
     *  UPPER_CASE_LETTER = Character.isUpperCase()
     *  LOWER_CASE_LETTER = Character.isLowerCase()
     *  DIGIT             = Character.isDigit()
     *  NON_LETTER_DIGIT  = !Character.isUpperCase() && !Character.isLowerCase() && !Character.isDigit()
     * 
     *  DELIMITER = WHITESPACE | NON_LETTER_DIGIT
     * </pre>
     */
    private abstract static class WordTokenizer {
        protected static final char UNDERSCORE = '_';

        /**
         * Parse sentence。
         */
        public String parse(String str) {
            if (StringUtils.isEmpty(str)) {
                return str;
            }

            int length = str.length();
            StringBuffer buffer = new StringBuffer(length);

            for (int index = 0; index < length; index++) {
                char ch = str.charAt(index);

                // 忽略空白。
                if (Character.isWhitespace(ch)) {
                    continue;
                }

                // 大写字母开始：UpperCaseWord或是TitleCaseWord。
                if (Character.isUpperCase(ch)) {
                    int wordIndex = index + 1;

                    while (wordIndex < length) {
                        char wordChar = str.charAt(wordIndex);

                        if (Character.isUpperCase(wordChar)) {
                            wordIndex++;
                        } else if (Character.isLowerCase(wordChar)) {
                            wordIndex--;
                            break;
                        } else {
                            break;
                        }
                    }

                    // 1. wordIndex == length，说明最后一个字母为大写，以upperCaseWord处理之。
                    // 2. wordIndex == index，说明index处为一个titleCaseWord。
                    // 3. wordIndex > index，说明index到wordIndex - 1处全部是大写，以upperCaseWord处理。
                    if ((wordIndex == length) || (wordIndex > index)) {
                        index = parseUpperCaseWord(buffer, str, index, wordIndex);
                    } else {
                        index = parseTitleCaseWord(buffer, str, index);
                    }

                    continue;
                }

                // 小写字母开始：LowerCaseWord。
                if (Character.isLowerCase(ch)) {
                    index = parseLowerCaseWord(buffer, str, index);
                    continue;
                }

                // 数字开始：DigitWord。
                if (Character.isDigit(ch)) {
                    index = parseDigitWord(buffer, str, index);
                    continue;
                }

                // 非字母数字开始：Delimiter。 
                inDelimiter(buffer, ch);
            }

            return buffer.toString();
        }

        private int parseUpperCaseWord(StringBuffer buffer, String str, int index, int length) {
            char ch = str.charAt(index++);

            // 首字母，必然存在且为大写。
            if (buffer.length() == 0) {
                startSentence(buffer, ch);
            } else {
                startWord(buffer, ch);
            }

            // 后续字母，必为小写。
            for (; index < length; index++) {
                ch = str.charAt(index);
                inWord(buffer, ch);
            }

            return index - 1;
        }

        private int parseLowerCaseWord(StringBuffer buffer, String str, int index) {
            char ch = str.charAt(index++);

            // 首字母，必然存在且为小写。
            if (buffer.length() == 0) {
                startSentence(buffer, ch);
            } else {
                startWord(buffer, ch);
            }

            // 后续字母，必为小写。
            int length = str.length();

            for (; index < length; index++) {
                ch = str.charAt(index);

                if (Character.isLowerCase(ch)) {
                    inWord(buffer, ch);
                } else {
                    break;
                }
            }

            return index - 1;
        }

        private int parseTitleCaseWord(StringBuffer buffer, String str, int index) {
            char ch = str.charAt(index++);

            // 首字母，必然存在且为大写。
            if (buffer.length() == 0) {
                startSentence(buffer, ch);
            } else {
                startWord(buffer, ch);
            }

            // 后续字母，必为小写。
            int length = str.length();

            for (; index < length; index++) {
                ch = str.charAt(index);

                if (Character.isLowerCase(ch)) {
                    inWord(buffer, ch);
                } else {
                    break;
                }
            }

            return index - 1;
        }

        private int parseDigitWord(StringBuffer buffer, String str, int index) {
            char ch = str.charAt(index++);

            // 首字符，必然存在且为数字。
            if (buffer.length() == 0) {
                startDigitSentence(buffer, ch);
            } else {
                startDigitWord(buffer, ch);
            }

            // 后续字符，必为数字。
            int length = str.length();

            for (; index < length; index++) {
                ch = str.charAt(index);

                if (Character.isDigit(ch)) {
                    inDigitWord(buffer, ch);
                } else {
                    break;
                }
            }

            return index - 1;
        }

        protected boolean isDelimiter(char ch) {
            return !Character.isUpperCase(ch) && !Character.isLowerCase(ch) && !Character.isDigit(ch);
        }

        protected abstract void startSentence(StringBuffer buffer, char ch);

        protected abstract void startWord(StringBuffer buffer, char ch);

        protected abstract void inWord(StringBuffer buffer, char ch);

        protected abstract void startDigitSentence(StringBuffer buffer, char ch);

        protected abstract void startDigitWord(StringBuffer buffer, char ch);

        protected abstract void inDigitWord(StringBuffer buffer, char ch);

        protected abstract void inDelimiter(StringBuffer buffer, char ch);
    }

    private static final WordTokenizer UPPER_CASE_WITH_UNDERSCORES_TOKENIZER = new WordTokenizer() {
                                                                                 protected void startSentence(StringBuffer buffer, char ch) {
                                                                                     buffer.append(Character.toUpperCase(ch));
                                                                                 }

                                                                                 protected void startWord(StringBuffer buffer, char ch) {
                                                                                     if (!isDelimiter(buffer.charAt(buffer.length() - 1))) {
                                                                                         buffer.append(UNDERSCORE);
                                                                                     }

                                                                                     buffer.append(Character.toUpperCase(ch));
                                                                                 }

                                                                                 protected void inWord(StringBuffer buffer, char ch) {
                                                                                     buffer.append(Character.toUpperCase(ch));
                                                                                 }

                                                                                 protected void startDigitSentence(StringBuffer buffer, char ch) {
                                                                                     buffer.append(ch);
                                                                                 }

                                                                                 protected void startDigitWord(StringBuffer buffer, char ch) {
                                                                                     if (!isDelimiter(buffer.charAt(buffer.length() - 1))) {
                                                                                         buffer.append(UNDERSCORE);
                                                                                     }

                                                                                     buffer.append(ch);
                                                                                 }

                                                                                 protected void inDigitWord(StringBuffer buffer, char ch) {
                                                                                     buffer.append(ch);
                                                                                 }

                                                                                 protected void inDelimiter(StringBuffer buffer, char ch) {
                                                                                     buffer.append(ch);
                                                                                 }
                                                                             };

}
