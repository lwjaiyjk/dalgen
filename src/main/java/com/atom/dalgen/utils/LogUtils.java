/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.atom.dalgen.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具
 * 
 * @author shizihu
 * @version $Id: LogUtils.java, v 0.1 2012-8-20 下午09:09:56 shizihu Exp $
 */
public final class LogUtils {
    private static final Logger _log = LoggerFactory.getLogger("LOGGER");

    public static final Logger get() {
        return _log;
    }

    public static final void log(String msg) {
        _log.info(msg);
    }

    public static final void log(String msg, Throwable e) {
        _log.info(msg, e);
    }

}
