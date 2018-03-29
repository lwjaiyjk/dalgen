/*
 * Taobao.com Inc.
 * Copyright (c) 2000-2004 All Rights Reserved.
 */
package middlegen.plugins.iwallet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import middlegen.plugins.iwallet.config.IWalletColumnConfig;
import middlegen.plugins.iwallet.config.IWalletResultMapConfig;

/**
 * An implementation of ResultMap interface.
 * 
 * @author Cheng Li
 * 
 * @version $Id: IWalletResultMap.java,v 1.1 2004/12/24 07:34:20 baobao Exp $
 */
public class IWalletResultMap implements ResultMap {
	/** the table to which the result map belongs */
	protected IWalletTable table;
	
	protected String idAttr;
	
	protected String classAttr;
	
	protected List results = new ArrayList();
	
	public IWalletResultMap(IWalletTable table, IWalletResultMapConfig config) {
		if (config == null) {
			// construct the default result map
			idAttr = table.getResultMapId();
			classAttr = table.getQualifiedDOClassName();
			
			Iterator i = table.getColumns().iterator();
			while (i.hasNext()) {
				results.add(new IWalletResultMapResult((IWalletColumn) i.next()));
			}
		} else {
			idAttr = config.getName();
			/*
			 * TODO: make the class attribute customizable
			 */
			classAttr = table.getQualifiedDOClassName();
			
			Iterator i = config.getColumns().iterator();
			while (i.hasNext()) {
				results.add(new IWalletResultMapResult((IWalletColumn) table.getColumn(((IWalletColumnConfig) i.next()).getName())));
			}
		}
	}

	/**
	 * @return
	 * 
	 * @see middlegen.plugins.iwallet.ResultMap#getIdAttr()
	 */
	public String getIdAttr() {
		return idAttr;
	}

	/**
	 * @return
	 * 
	 * @see middlegen.plugins.iwallet.ResultMap#getClassAttr()
	 */
	public String getClassAttr() {
		return classAttr;
	}

	/**
	 * @return
	 * 
	 * @see middlegen.plugins.iwallet.ResultMap#getResults()
	 */
	public List getResults() {
		return results;
	}

}
