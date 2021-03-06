/*
 * Copyright (c) 2014 All Rights Reserved.
 */
package com.taotaosou.main.dao;

// auto generated imports
import com.taotaosou.main.model.InducfResourceArticleDto;

/**
 * A dao interface provides methods to access database table <tt>inducf_resource_article</tt>.
 *
 * This file is generated by <tt>ibatis-dalgen</tt>, a DAL (Data Access Layer)
 * code generation utility specially developed for <tt>ibatis</tt> project.
 * 
 * PLEASE DO NOT MODIFY THIS FILE MANUALLY, or else your modification may
 * be OVERWRITTEN by someone else. To modify the file, you should go to 
 * directory <tt>(project-home)/biz/dal/src/conf/dalgen</tt>, and 
 * find the corresponding configuration file (<tt>tables/inducf_resource_article.xml</tt>). 
 * Modify the configuration file according to your needs, then run <tt>ibatis-dalgen</tt> 
 * to generate this file.
 *
 * @author dalgen
 */
public interface InducfResourceArticleDao {
	/**
	 *  Query DB table <tt>inducf_resource_article</tt> for records.
	 *
   	 *  <p>
   	 *  Description for this operation is<br>
   	 *  <tt></tt>
	 *  <p>
	 *  The sql statement for this operation is <br>
	 *  <tt>select * from inducf_resource_article where (id = ?)</tt>
	 *
	 *	@param id
	 *	@return InducfResourceArticleDto
	 *	@throws DataAccessException
	 */	 
    public InducfResourceArticleDto selectById(Integer id);

}