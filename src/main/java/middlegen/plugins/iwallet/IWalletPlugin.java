/*
 * Taobao.com Inc.
 * Copyright (c) 2000-2004 All Rights Reserved.
 */
package middlegen.plugins.iwallet;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import middlegen.FileProducer;
import middlegen.MiddlegenException;
import middlegen.TableDecorator;
import middlegen.Util;
import middlegen.javax.JavaPlugin;
import middlegen.plugins.iwallet.config.IWalletConfig;
import middlegen.plugins.iwallet.config.IWalletConfigException;
import middlegen.plugins.iwallet.config.IWalletSeqConfig;
import middlegen.plugins.iwallet.util.DalUtil;

/**
 * Main middlegen plugin classes for iwallet dal generator.
 *
 * <p>
 * This plugin will generate the following iwallet dal source files:
 * <ol>
 * <li>dataobject
 * <li>dao interface
 * <li>ibatis-based dao implementation
 * <li>ibatis configuration
 * </ol>
 *
 * @author Cheng Li
 *
 * @version $Id: IWalletPlugin.java,v 1.4 2006/09/18 12:43:50 zhaoxu Exp $
 */
public class IWalletPlugin extends JavaPlugin {

	/** the class name of Money */
	public static final String MONEY_CLASS = "com.github.obullxl.lang.Money";

	/** the result map name for Money */
	public static final String MONEY_RESULT_MAP_ID = "RM-MONEY";

	/** full qualified class name for Paginator */
	public static final String PAGINATOR_CLASS = "com.github.obullxl.lang.Paginator";

	/** full qualified class name for DataAccessException */
	public static final String DATA_ACCESS_EXCEPTION_CLASS = "org.springframework.dao.DataAccessException";
	public static final String BASE_DO_CLASS = "BaseDTO";
	public static final String BASE_DAO_CLASS = "BaseDAO";

	/**
	 * the time when the source code was generated, may be used to tag source
	 * code
	 */
	private Date genTime;

	/** a list of all sequences (instance of IWalletSeq) */
	private List<IWalletSeq> seqs = new ArrayList<IWalletSeq>();

	/**
	 * Constructor for IWalletPlugin.
	 */
	public IWalletPlugin() {
		super();

		genTime = new Date();
	}

	/**
	 * @return
	 *
	 * @see middlegen.Plugin#getColumnDecoratorClass()
	 */
	@Override
	public Class getColumnDecoratorClass() {
		return IWalletColumn.class;
	}

	/**
	 * @return
	 *
	 * @see middlegen.Plugin#getTableDecoratorClass()
	 */
	@Override
	public Class getTableDecoratorClass() {
		return IWalletTable.class;
	}

	/**
	 * @return
	 */
	public Date getGenTime() {
		return genTime;
	}

	/**
	 * @throws middlegen.MiddlegenException
	 *
	 * @see middlegen.Plugin#validate()
	 */
	@Override
	public void validate() throws MiddlegenException {
		super.validate();

		List<IWalletSeqConfig> list = null;
		try {
			list = IWalletConfig.getInstance().getSeqConfigs();
		} catch (IWalletConfigException e) {
			throw new MiddlegenException(e.getMessage());
		}

		for (IWalletSeqConfig seq : list) {
			seqs.add(new IWalletSeq(seq));
		}
	}

	/**
	 * perform all iwallet specific configurations.
	 *
	 * @throws IWalletConfigException
	 */
	public void configAll() throws IWalletConfigException {

		for (FileProducer fileProducer : getFileProducers()) {
			setStaticIwalletTableInfo(fileProducer);
		}

		for (TableDecorator td : getTables()) {
			IWalletTable iwTable = (IWalletTable) td;

			if (DalUtil.inTabs(iwTable.getName())) {
				iwTable.configResultMaps();
				iwTable.configOperations();
			}
		}
	}

	/**
	 * @param iWalletTable
	 */
	private void setStaticIwalletTableInfo(FileProducer fileProducer) {

		String fileProducerName = fileProducer.getName();
		String pattern = StringUtils.substring(fileProducer.getDestinationFileName(),
				StringUtils.lastIndexOf(fileProducer.getDestinationFileName(), "/") + 1,
				StringUtils.lastIndexOf(fileProducer.getDestinationFileName(), "."));
		String packageUrl = parsePackageUrl(fileProducer.getDestinationFileName());
		if (StringUtils.equals(fileProducerName, FileProducer.DTO_FILE_PRODUCER)) {
			IWalletTable.DO_PATTERN = pattern;
			IWalletTable.DO_PACKAGE = packageUrl;
		} else if (StringUtils.equals(fileProducerName, FileProducer.DAO_FILE_PRODUCER)) {
			IWalletTable.DAO_PATTERN = pattern;
			IWalletTable.DAO_PACKAGE = packageUrl;
		} else if (StringUtils.equals(fileProducerName, FileProducer.DAO_IMPL_FILE_PRODUCER)) {
			IWalletTable.IBATIS_PATTERN = pattern;
			IWalletTable.IBATIS_PACKAGE = packageUrl;
		}

	}

	/**
	 * 解析包的url dao/impl/{0}DaoImpl
	 *
	 * @param destPath
	 * @return
	 */
	private String parsePackageUrl(String destPath) {
		int lastSepIndex = StringUtils.lastIndexOf(destPath, "/");
		String subPackagePath = StringUtils.substring(destPath, 0, lastSepIndex);
		String subPackageUrl = StringUtils.replace(subPackagePath, "/", ".");
		return subPackageUrl;
	}

	/**
	 * @see middlegen.Plugin#generate()
	 */
	@Override
	protected void generate() throws MiddlegenException {
		try {
			configAll();
		} catch (IWalletConfigException e) {
			throw new MiddlegenException(e.getMessage());
		}

		super.generate();
	}

	/**
	 * @return
	 */
	public String getSeqDAOPackage() {
		if (StringUtils.isNotBlank(IWalletTable.DAO_PACKAGE)) {
			return getPackage() + "." + IWalletTable.DAO_PACKAGE;
		} else {
			return getPackage();
		}
	}

	/**
	 * @return
	 */
	public String getQualifiedSeqDAOClassName() {
		return Util.getQualifiedClassName(getSeqDAOPackage(), getSeqDAOClassName());
	}

	/**
	 * @return
	 */
	public String getSeqDAOClassName() {
		return MessageFormat.format(IWalletTable.DAO_PATTERN, new Object[] { "Seq" });
	}

	/**
	 * @return
	 */
	public String getSeqDAOBeanName() {
		return Util.decapitalise(getSeqDAOClassName());
	}

	/**
	 * @return
	 */
	public String getSeqIbatisPackage() {
		if (StringUtils.isNotBlank(IWalletTable.IBATIS_PACKAGE)) {
			return getPackage() + "." + IWalletTable.IBATIS_PACKAGE;
		} else {
			return getPackage();
		}
	}

	/**
	 * @return
	 */
	public String getQualifiedSeqIbatisClassName() {
		return Util.getQualifiedClassName(getSeqIbatisPackage(), getSeqIbatisClassName());
	}

	/**
	 * @return
	 */
	public String getSeqIbatisClassName() {
		return MessageFormat.format(IWalletTable.IBATIS_PATTERN, new Object[] { "Seq" });
	}

	/**
	 * @return
	 */
	public List<IWalletSeq> getSequences() {
		return seqs;
	}

	/**
	 * @return
	 */
	public String getMoneyClass() {
		return MONEY_CLASS;
	}

	/**
	 * @return
	 */
	public String getMoneyResultMapId() {
		return MONEY_RESULT_MAP_ID;
	}

	/**
	 * @return
	 */
	public String getBaseDOPackage() {
		if (StringUtils.isNotBlank(IWalletTable.DO_PACKAGE)) {
			return getPackage() + "." + IWalletTable.DO_PACKAGE;
		} else {
			return getPackage();
		}
	}

	/**
	 * @return
	 */
	public String getBaseDAOPackage() {
		if (StringUtils.isNotBlank(IWalletTable.DAO_PACKAGE)) {
			return getPackage() + "." + IWalletTable.DAO_PACKAGE;
		} else {
			return getPackage();
		}
	}

	/**
	 * @return
	 */
	public String getBaseDOClassName() {
		return BASE_DO_CLASS;
	}

	/**
	 * @return
	 */
	public String getQualifiedBaseDOClassName() {
		if (StringUtils.isEmpty(getBaseDOPackage())) {
			return getBaseDOClassName();
		} else {
			return getBaseDOPackage() + "." + getBaseDOClassName();
		}
	}

	/**
	 * ȡ�����������б���б�.
	 *
	 * @return
	 */
	public List getSortedTables() {
		List sortedTables = new ArrayList();

		sortedTables.addAll(getTables());

		Collections.sort(sortedTables);

		return sortedTables;
	}

	/**
	 * ȡ�����������õı���
	 *
	 * @return
	 * @throws IWalletConfigException
	 */
	public List<String> getAllTableNames() throws IWalletConfigException {
		return IWalletConfig.getInstance().getAllTableNames();
	}

}
