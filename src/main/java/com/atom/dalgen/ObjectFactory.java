/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.atom.dalgen;

/**
 * 对象工厂
 * 
 * @author shizihu
 * @version $Id: ObjectFactory.java, v 0.1 2012-8-24 下午07:07:18 shizihu Exp $
 */
public final class ObjectFactory {
    /** 锁对象 */
    private static final Object lock = new Object();

    public static final Object getLock() {
        return lock;
    }

    private static String tabs;

    public static final void setTabs(String _tabs) {
        tabs = _tabs;
    }

    public static final String getTabs() {
        return tabs;
    }

}
