/*
 * Taobao.com Inc.
 * Copyright (c) 2000-2004 All Rights Reserved.
 */
package middlegen.plugins.iwallet;

/**
 * TODO Brief functionality specification.
 * 
 * @author Cheng Li
 * 
 * @version $Id: ResultMapResult.java,v 1.1 2004/12/24 07:34:20 baobao Exp $
 */
public interface ResultMapResult {
	/**
	 * 
	 * @return
	 */
	public String getProperty();
	
	/**
	 * 
	 * @return
	 */
	public String getColumn();
	
	/**
	 * 
	 * @return
	 */
	public int getColumnIndex();
	
	/**
	 * 
	 * @return
	 */
	public String getJavaType();
	
	/**
	 * 
	 * @return
	 */
	public String getJdbcType();
	
	/**
	 * 
	 * @return
	 */
	public String getNullValue();
	
	/**
	 * 
	 * @return
	 */
	public boolean isHasNullValue();
	
	/**
	 * 
	 * @return
	 */
	public String getSelect();
}
