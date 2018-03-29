/*
 * Taobao.com Inc.
 * Copyright (c) 2000-2004 All Rights Reserved.
 */
package middlegen.plugins.iwallet;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.atom.dalgen.utils.CfgUtils;
import com.atom.dalgen.utils.LogUtils;

import Zql.ZDelete;
import Zql.ZInsert;
import Zql.ZQuery;
import Zql.ZUpdate;
import middlegen.Column;
import middlegen.Plugin;
import middlegen.Table;
import middlegen.Util;
import middlegen.javax.JavaPlugin;
import middlegen.javax.JavaTable;
import middlegen.plugins.iwallet.config.CopyConfig;
import middlegen.plugins.iwallet.config.IWalletConfig;
import middlegen.plugins.iwallet.config.IWalletConfigException;
import middlegen.plugins.iwallet.config.IWalletOperationConfig;
import middlegen.plugins.iwallet.config.IWalletResultMapConfig;
import middlegen.plugins.iwallet.config.IWalletSqlConfig;
import middlegen.plugins.iwallet.config.IWalletTableConfig;
import middlegen.plugins.iwallet.operation.IWalletDelete;
import middlegen.plugins.iwallet.operation.IWalletInsert;
import middlegen.plugins.iwallet.operation.IWalletSelect;
import middlegen.plugins.iwallet.operation.IWalletUnknown;
import middlegen.plugins.iwallet.operation.IWalletUpdate;
import middlegen.plugins.iwallet.util.DalUtil;

/**
 * A table decorator relates a database table and a set of dal sources.
 *
 * @author Cheng Li
 *
 * @version $Id: IWalletTable.java,v 1.3 2005/09/05 09:01:14 lusu Exp $
 */
public class IWalletTable extends JavaTable implements Comparable {

	public static String DO_PATTERN = "{0}Do";
	public static String DAO_PATTERN = "{0}Dao";
	public static String IBATIS_PATTERN = "Ibatis{0}Dao";
	public static String DO_PACKAGE = "dataobject";
	public static String DAO_PACKAGE = "daointerface";
	public static String IBATIS_PACKAGE = "ibatis";
	public static final String RESULT_MAP_PREFIX = "RM-";

	// add by yuanxiao
	public static final String UTILS_PACKAGE = "utils";
	public static final String UTILS_PATTERN = "{0}Utils";

	/** the table config corresponding to the table */
	private IWalletTableConfig tableConfig;

	/** a list of all result maps */
	private List resultMaps = new ArrayList();

	/** a map make look up result map by name quick */
	private Map resultMapIndex = new HashMap();

	/** a list of all operation decorators */
	private List operations = new ArrayList();

	/** a list of all dataobject imports */
	private Set<String> doImports = new HashSet<String>();

	/** a list of all dao imports */
	private Set<String> daoImports = new HashSet<String>();

	/** a list of all ibatis imports */
	private Set<String> ibatisImports = new HashSet<String>();

	/** a list of all ibatis imports */
	private Set<String> daoImplImports = new HashSet<String>();

	/**
	 * Constructor for IWalletTableDecorator.
	 */
	public IWalletTable(Table subject) {
		super(subject);
	}

	/**
	 * @param plugin
	 *
	 * @see middlegen.PreferenceAware#setPlugin(middlegen.Plugin)
	 */
	@Override
	public void setPlugin(Plugin plugin) {
		if (!(plugin instanceof IWalletPlugin)) {
			throw new IllegalArgumentException("The plugin must be an instance of IWalletPlugin.");
		}

		super.setPlugin(plugin);
	}

	/**
	 * Get the sub package of this table.
	 *
	 * @return
	 */
	public String getSubPackage() {
		return tableConfig.getSubPackage();
	}

	/**
	 * The package for the table is the concatenation of the main package (for
	 * the project) and the sub package for the table.
	 *
	 * @return
	 *
	 * @see middlegen.javax.JavaTable#getPackage()
	 */
	@Override
	public String getPackage() {
		if (StringUtils.isBlank(getSubPackage())) {
			return super.getPackage();
		} else {
			return super.getPackage() + "." + getSubPackage();
		}
	}

	/**
	 * @return
	 *
	 * @see middlegen.javax.JavaTable#getBaseClassName()
	 */
	@Override
	public String getBaseClassName() {
		if (StringUtils.isNotBlank(tableConfig.getDoName())) {
			return tableConfig.getDoName();
		} else {
			String theName = super.getBaseClassName();
			try {
				theName = DalUtil.removeTablePrefix(theName);
			} catch (IWalletConfigException e) {
				LogUtils.get().error(e.getMessage());
			}

			return theName;
		}
	}

	// ---add by gaoll
	public String getBaseDaoClass() {
		return CfgUtils.findValue("baseDaoClass", "com.taotaosou.foundation.base.data.BaseDao1");
	}

	public String getBaseDaoClassName() {
		String daoClass = getBaseDaoClass();
		String[] values = StringUtils.split(daoClass, '.');
		return values[values.length - 1];
	}
	// ---add by gaoll

	/**
	 * Gets the variable name.
	 *
	 * <p>
	 * The parent class has intentionally hide this method. However, we need the
	 * method to compose method signatures.
	 *
	 * @return The VariableName value
	 */
	protected String getVariableName() {
		return Util.decapitalise(getDestinationClassName());
	}

	public String getBeanName() {
		return Util.decapitalise(getBaseClassName());
	}

	/**
	 * Gets the SingularisedVariableName attribute of the JavaTable object
	 *
	 * <p>
	 * The parent class has intentionally hide this method. However, we need the
	 * method to compose method signatures.
	 *
	 * @return The SingularisedVariableName value
	 */
	public String getSingularisedVariableName() {
		if (getTableElement().getSingular() != null) {
			return getTableElement().getSingular();
		} else {
			return Util.singularise(getVariableName());
		}
	}

	public List<IWalletSqlConfig> getSqls() {
		return tableConfig.getSqls();
	}

	public List<CopyConfig> getCopys() {
		return tableConfig.getCopys();
	}

	/**
	 * Gets all operations
	 *
	 * @return
	 */
	public List getOperations() {
		return operations;
	}

	/**
	 * @return
	 */
	public IWalletTableConfig getTableConfig() {
		return tableConfig;
	}

	/**
	 * @return
	 */
	public Set<String> getDoImports() {
		return doImports;
	}

	/**
	 * @param type
	 */
	public void addDoImport(String type) {
		addImport(doImports, type);
	}

	/**
	 * @param type
	 */
	public void addDoImports(List<String> list) {
		addImports(doImports, list);
	}

	/**
	 * @param type
	 */
	public void addDaoImports(List<String> list) {
		addImports(daoImports, list);
	}

	/**
	 * @return
	 */
	public Set<String> getDaoImports() {
		return daoImports;
	}

	/**
	 * @param type
	 */
	public void addIbatisImport(String type) {
		addImport(ibatisImports, type);
	}

	public void addIbatisImports(List<String> list) {
		addImports(ibatisImports, list);
	}

	public Set<String> getIbatisImports() {
		return ibatisImports;
	}

	public Set<String> getDaoImplImports() {
		daoImplImports.addAll(daoImports);
		daoImplImports.addAll(ibatisImports);

		return daoImplImports;
	}

	/**
	 * @param type
	 */
	public void addDaoImport(String type) {
		addImport(daoImports, type);
	}

	protected void addImport(Set<String> list, String type) {
		if (middlegen.plugins.iwallet.util.DalUtil.isNeedImport(type)) {
			if (!list.contains(type)) {
				list.add(type);
			}
		}
	}

	protected void addImports(Set<String> list, List<String> typeList) {
		for (int i = 0; i < typeList.size(); i++) {
			addImport(list, typeList.get(i));
		}
	}

	/**
	 * @see middlegen.PreferenceAware#init()
	 */
	@Override
	protected void init() {
		super.init();

		try {
			tableConfig = IWalletConfig.getInstance().getTableConfig(getSqlName());
		} catch (IWalletConfigException e) {
			LogUtils.get().error(e.getMessage());
		}

		LogUtils.get().debug("Initialize table " + getSqlName());

		if (tableConfig == null) {
			LogUtils.get().error("Can't get table configuration for table " + getSqlName() + ".");
		}
	}

	/**
	 * @return
	 *
	 * @see middlegen.javax.JavaTable#getQualifiedBaseClassName()
	 */
	@Override
	public String getQualifiedDestinationClassName() {
		String pakkage = ((JavaPlugin) getPlugin()).getPackage();

		return Util.getQualifiedClassName(pakkage + ".dataobject", getDestinationClassName());
	}

	/**
	 * Configure all resultMaps.
	 */
	public void configResultMaps() {
		resultMaps = new ArrayList();

		// the default resultmap
		resultMaps.add(new IWalletResultMap(this, null));

		// additional resultmaps
		Iterator i = tableConfig.getResultMaps().iterator();

		while (i.hasNext()) {
			IWalletResultMap resultMap = new IWalletResultMap(this,
					(IWalletResultMapConfig) i.next());

			resultMaps.add(resultMap);
			resultMapIndex.put(resultMap.getIdAttr(), resultMap);
		}
	}

	/**
	 * Config all operations.
	 */
	public void configOperations() {
		operations = new ArrayList();

		Iterator iop = tableConfig.getOperations().iterator();

		while (iop.hasNext()) {
			IWalletOperationConfig opConfig = (IWalletOperationConfig) iop.next();

			IWalletOperation op;

			if (opConfig.getZst() instanceof ZInsert) {
				op = new IWalletInsert(opConfig);
			} else if (opConfig.getZst() instanceof ZQuery) {
				op = new IWalletSelect(opConfig);
			} else if (opConfig.getZst() instanceof ZUpdate) {
				op = new IWalletUpdate(opConfig);
			} else if (opConfig.getZst() instanceof ZDelete) {
				op = new IWalletDelete(opConfig);
			} else {
				op = new IWalletUnknown(opConfig);
			}

			op.setPlugin(getPlugin());

			op.setTable(this);

			operations.add(op);
		}
	}

	/**
	 * Get the name of the result map corresponding to this table and
	 * dataobject.
	 *
	 * @return
	 */
	public String getResultMapId() {
		return RESULT_MAP_PREFIX
				+ middlegen.plugins.iwallet.util.DalUtil.toUpperCaseWithDash(getBaseClassName());
	}

	/**
	 *
	 * @return
	 */
	public String getDOClassName() {
		return MessageFormat.format(DO_PATTERN, new String[] { getBaseClassName() });
	}

	public String getUtilClassName() {
		return MessageFormat.format(UTILS_PATTERN, new String[] { getBaseClassName() });
	}

	/**
	 *
	 * @return
	 */
	public String getDAOClassName() {
		return MessageFormat.format(DAO_PATTERN, new String[] { getBaseClassName() });
	}

	/**
	 * dao 对应的bean name,就是类名的首字母小写
	 * 
	 * @return
	 */
	public String getDAOBeanName() {
		String baseClassName = getBaseClassName();
		baseClassName = baseClassName.substring(0, 1).toLowerCase() + baseClassName.substring(1);
		return MessageFormat.format(DAO_PATTERN, new String[] { baseClassName });
	}

	/**
	 *
	 * @return
	 */
	public String getIbatisClassName() {
		return MessageFormat.format(IBATIS_PATTERN, new String[] { getBaseClassName() });
	}

	/**
	 *
	 * @return
	 */
	public String getDOPackage() {
		if (StringUtils.isNotBlank(DO_PACKAGE)) {
			return getPackage() + "." + DO_PACKAGE;
		} else {
			return getPackage();
		}
	}

	// add by yuanxiao
	public String getUtilsPackage() {
		if (StringUtils.isNotBlank(UTILS_PACKAGE)) {
			return getPackage() + "." + UTILS_PACKAGE;
		} else {
			return getPackage();
		}
	}

	/**
	 *
	 * @return
	 */
	public String getDAOPackage() {
		if (StringUtils.isNotBlank(DAO_PACKAGE)) {
			return getPackage() + "." + DAO_PACKAGE;
		} else {
			return getPackage();
		}
	}

	/**
	 *
	 * @return
	 */
	public String getIbatisPackage() {
		if (StringUtils.isNotBlank(IBATIS_PACKAGE)) {
			return getPackage() + "." + IBATIS_PACKAGE;
		} else {
			return getPackage();
		}
	}

	/**
	 * @return
	 */
	public String getQualifiedDOClassName() {
		return Util.getQualifiedClassName(getDOPackage(), getDOClassName());
	}

	/**
	 * @return
	 */
	public String getQualifiedDAOClassName() {
		return Util.getQualifiedClassName(getDAOPackage(), getDAOClassName());
	}

	/**
	 * @return
	 */
	public String getQualifiedIbatisClassName() {
		return Util.getQualifiedClassName(getIbatisPackage(), getIbatisClassName());
	}

	/**
	 * @return
	 */
	public String getSequence() {
		return tableConfig.getSequence();
	}

	// add by yuanxiao
	public String getConfidentiality() {
		return tableConfig.getConfidentiality();
	}

	public String getIntegrity() {
		return tableConfig.getIntegrity();
	}

	public String getEncodekeyname() {
		return tableConfig.getEncodekeyname();
	}

	public String getAbstractkeyname() {
		return tableConfig.getAbstractkeyname();
	}

	public boolean getDrmConfig() {
		return tableConfig.getDrmConfig();
	}

	public boolean isTicket() {
		return tableConfig.isTicket();
	}

	/**
	 * TB-${table.baseClassName}-ID
	 */
	public String getTicketName() {
		String ticketName = tableConfig.getTicketName();
		if (StringUtils.isBlank(ticketName)) {
			ticketName = "TB-" + getBaseClassName() + "-ID";
		}

		return ticketName;
	}

	public boolean isFmtNo() {
		return tableConfig.isFmtNo();
	}

	public String getFmtNoName() {
		String fmtNoName = tableConfig.getFmtNoName();
		if (StringUtils.isBlank(fmtNoName)) {
			fmtNoName = "com.github.obullxl.ticket.support.DefaultTicketEncode";
		}

		return fmtNoName;
	}

	public boolean isValve() {
		return tableConfig.isValve();
	}

	/**
	 * @return
	 */
	public boolean isHasSequence() {
		return StringUtils.isNotBlank(getSequence());
	}

	/**
	 * @return
	 */
	public List getResultMaps() {
		return resultMaps;
	}

	/**
	 * @param id
	 * @return
	 */
	public IWalletResultMap getResultMap(String id) {
		return (IWalletResultMap) resultMapIndex.get(id);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object o) {
		if (o instanceof IWalletTable) {
			return getBeanName().compareTo(((IWalletTable) o).getBeanName());
		} else {
			return 0;
		}
	}

	/**
	 * ȡ���Ƿ�Ϊ�Զ��л����Դ add by zhaoxu 20061225
	 *
	 * @return
	 */
	public boolean getIsAutoSwitchDataSrc() {
		return tableConfig.isAutoSwitchDataSrc();
	}

	public Column getIwPkColumn() {
		Column pkColumn = getPkColumn();
		String dummyPk = tableConfig.getDummyPk();
		if (pkColumn == null && StringUtils.isNotBlank(dummyPk)) {
			pkColumn = getColumn(dummyPk);
		}
		return pkColumn;
	}

	/**
	 * Gets the SimplePk attribute of the Entity11DbTable object
	 *
	 * @return The SimplePk value
	 */
	@Override
	public boolean isSimplePk() {
		return getIwPkColumn() != null;
	}
}
