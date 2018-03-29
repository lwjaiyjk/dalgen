/*
 * Taobao.com Inc.
 * Copyright (c) 2000-2004 All Rights Reserved.
 */
package middlegen.plugins.iwallet;

import java.util.List;

/**
 * An interface for ibatis ResultMap generation.
 * 
 * @author Cheng Li
 * 
 * @version $Id: ResultMap.java,v 1.1 2004/12/24 07:34:20 baobao Exp $
 */
public interface ResultMap {
	/**
	 * Get the "id" attribute of the ResultMap.
	 * 
	 * @return
	 */
	public String getIdAttr();
	
	/**
	 * Get the "class" attribute of the ResultMap.
	 * 
	 * @return
	 */
	public String getClassAttr();
	
	/**
	 * Get a list of "result" elements, each one is an instance of <tt>ResultMapResult</tt>.
	 * 
	 * @return
	 */
	public List getResults();
}
