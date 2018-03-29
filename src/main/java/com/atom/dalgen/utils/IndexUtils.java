/**
 * Author: obullxl@gmail.com
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.atom.dalgen.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 索引生成器
 * 
 * @author obullxl@gmail.com
 * @version $Id: IndexUtils.java, V1.0.1 2013年11月26日 下午3:25:29 $
 */
public class IndexUtils {
    private final AtomicInteger value = new AtomicInteger(0);

    public static IndexUtils fetch() {
        return new IndexUtils();
    }

    /**
     * 当前索引值
     */
    public int value() {
        return value.get();
    }

    /**
     * 下一个索引
     */
    public int next() {
        return value.getAndIncrement();
    }

}
