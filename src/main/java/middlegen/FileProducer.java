package middlegen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.atom.dalgen.utils.CfgUtils;
import com.atom.dalgen.utils.LogUtils;
import com.atom.dalgen.utils.Utils;

import middlegen.plugins.iwallet.IWalletTable;
import middlegen.plugins.iwallet.config.IWalletOperationConfig;
import middlegen.plugins.iwallet.util.DalUtil;
import middlegen.validator.ErrorMessage;
import middlegen.validator.Validator;

/**
 * FileProducer objects hold all information required for the generation of one
 * file. Each FileProducer instance will generate one physical file.
 */
public final class FileProducer {
	private static final String VALIDATOR_ROOT_PATH = "middlegen.validator.impl.";

	// DTO 文件生成器
	public final static String DTO_FILE_PRODUCER = "DTO";

	// DAO 文件生成器
	public final static String DAO_FILE_PRODUCER = "DAO";

	// DAO 文件生成器
	public final static String DAO_IMPL_FILE_PRODUCER = "DAO_IMPL";

	// DAO 文件生成器
	public final static String SQL_MAP_FILE_PRODUCER = "SQL_MAP";

	private File _destinationDir;

	private String _destinationFileName;

	private URL _template;

	/** 文件生成器名称 */
	private String name;

	/** 是否覆盖，默认为TRUE */
	private boolean justNew = true;

	/**
	 * @todo-javadoc Describe the field
	 */
	private final Map<Object, Object> _contexMap = new HashMap<Object, Object>();

	/**
	 * @todo-javadoc Describe the field
	 */
	private Map _tableElements = new HashMap();

	/**
	 * @todo-javadoc Describe the field
	 */
	private String _id;

	/**
	 * @todo-javadoc Describe the field
	 */
	private boolean _isCustom;

	/**
	 * @todo-javadoc Describe the field 用来在生成后做验证用 add by 张汤 2010-1-01-21，
	 */
	private String _validator;

	public File getDestinationDir() {
		return _destinationDir;
	}

	public String getDestinationFileName() {
		return _destinationFileName;
	}

	public URL getTemplate() {
		return _template;
	}

	public Map getTableElements() {
		return _tableElements;
	}

	public boolean isCustom() {
		return _isCustom;
	}

	public String getValidator() {
		return _validator;
	}

	public void setValidator(String validator) {
		if (StringUtils.isNotBlank(validator)) {
			_validator = VALIDATOR_ROOT_PATH + StringUtils.capitalize(validator);
		}
	}

	/** Empty constructor. Used by Ant. */
	public FileProducer() {
		_isCustom = true;
	}

	/**
	 * Describe what the DefaultFileProducer constructor does
	 */
	public FileProducer(File destinationDir, String destinationFileName, URL template) {
		_isCustom = false;

		if (destinationDir == null) {
			throw new IllegalArgumentException("destinationDir can't be null");
		}

		if (destinationFileName == null) {
			throw new IllegalArgumentException("destinationFileName can't be null");
		}

		if (template == null) {
			throw new IllegalArgumentException("template can't be null");
		}

		setDestination(destinationDir);
		setFilename(destinationFileName);
		setTemplate(template);

		// Use the name of the template and strip away the gurba in front and
		// the extension.
		int lastSlash = template.toString().lastIndexOf("/");
		int lastDot = template.toString().lastIndexOf(".");
		String id = template.toString().substring(lastSlash + 1, lastDot);

		setId(id);
	}

	/**
	 * Sets the Id attribute of the FileProducer object
	 *
	 * @param id
	 *            The new Id value
	 */
	public void setId(String id) {
		_id = id;
	}

	/**
	 * Sets the Destination attribute of the DefaultFileProducer object
	 *
	 * @param destinationDir
	 *            The new Destination value
	 */
	public void setDestination(File destinationDir) {
		_destinationDir = destinationDir;
	}

	/**
	 * Sets the FileName attribute of the DefaultFileProducer object
	 *
	 * @param destinationFileName
	 *            The new FileName value
	 */
	public void setFilename(String destinationFileName) {
		_destinationFileName = destinationFileName;
	}

	/**
	 * Sets the Template attribute of the DefaultFileProducer object
	 *
	 * @param template
	 *            The new Template value
	 */
	public void setTemplate(File template) {
		try {
			setTemplate(template.toURI().toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Gets the Id attribute of the FileProducer object
	 *
	 * @return The Id value
	 */
	public String getId() {
		return _id;
	}

	/**
	 * Returns a copy of this FileProducer.
	 *
	 * @todo-javadoc Write javadocs for method parameter
	 * @todo-javadoc Write javadocs for method parameter
	 * @return a FileProducer with the *real* name
	 */

	/*
	 * public FileProducer copy(TableDecorator tableDecorator) { String
	 * destinationFileName = getDestinationFileName(); if
	 * (destinationFileName.indexOf("{0}") != -1) { destinationFileName =
	 * MessageFormat.format(destinationFileName, new
	 * String[]{tableDecorator.getReplaceName()}); } / possibly use a deeper
	 * destination dir (typically for java classes) File destinationDir = new
	 * File(getDestinationDir(), tableDecorator.getSubDirPath()); FileProducer
	 * result = new FileProducer(destinationDir, destinationFileName,
	 * getTemplate()); return result; }
	 */
	public boolean isGenerationPerTable() {
		return _destinationFileName.indexOf("{0}") != -1;
	}

	/**
	 * Describe what the method does
	 */
	public void validate() throws IllegalStateException {
		if (_template == null) {
			String msg = "Please specify the template attribute in the fileproducer.";

			LogUtils.get().error(msg);
			throw new IllegalStateException(msg);
		}

		if (_isCustom) {
			// Specified in Ant
			if (getId() != null) {
				// Overriding an existing template
				if (_destinationDir != null) {
					String msg = "In fileproducer with id=\"" + getId()
							+ "\", destination should *not* be specified. "
							+ "The fileproducer is overriding an existing template in the plugin, "
							+ "but the plugin should still decide where to store the generated file. "
							+ _destinationDir.getAbsolutePath();

					LogUtils.get().error(msg);
					throw new IllegalStateException(msg);
				}

				if (_destinationFileName != null) {
					String msg = "In fileproducer with id=\"" + getId()
							+ "\", filename should *not* be specified. "
							+ "The fileproducer is overriding an existing template in the plugin, "
							+ "but the plugin should still decide how to name the generated file. "
							+ _destinationFileName;

					LogUtils.get().error(msg);
					throw new IllegalStateException(msg);
				}
			} else {
				// Not overriding an existing template
				if (_destinationDir == null) {
					String msg = "Please specify the destination attribute in the fileproducer.";

					LogUtils.get().error(msg);
					throw new IllegalStateException(msg);
				}

				if (_destinationDir == null) {
					String msg = "Please specify the filename attribute in the fileproducer.";

					LogUtils.get().error(msg);
					throw new IllegalStateException(msg);
				}
			}
		} else {
			// Created by a plugin class
		}
	}

	/**
	 * Describe the method
	 *
	 * @todo-javadoc Describe the method
	 * @todo-javadoc Describe the method parameter
	 * @param tableElement
	 *            Describe the method parameter
	 */
	public void addConfiguredTable(TableElement tableElement) {
		// actually we only care about the keys.
		_tableElements.put(tableElement.getName(), tableElement);
	}

	/**
	 * Describe what the method does
	 */
	public void generateForTable(VelocityEngine velocityEngine, TableDecorator tableDecorator)
			throws MiddlegenException {
		// possibly use a deeper destination dir (typically for java classes)
		File destinationDir = new File(_destinationDir, tableDecorator.getSubDirPath());
		if (tableDecorator instanceof IWalletTable) {
			String conf = ((IWalletTable) tableDecorator).getTableConfig().getConfidentiality();
			String integ = ((IWalletTable) tableDecorator).getTableConfig().getIntegrity();

			getMethod(tableDecorator, conf, integ);

		}
		String destinationFileName = MessageFormat.format(_destinationFileName,
				new Object[] { tableDecorator.getReplaceName() });
		File outputFile = new File(destinationDir, destinationFileName);

		/*// 获取子包路径：filename="ibatis/Ibatis{0}Dao.java"
		int lastSepIndex = StringUtils.lastIndexOf(_destinationFileName, "/");
		String subPackagePath = StringUtils.substring(_destinationFileName, 0, lastSepIndex);
		String subPackageStr = StringUtils.replace(subPackagePath, "/", ".");
		JavaTable javaTableDecorator = (JavaTable) tableDecorator;
		String originPackage = javaTableDecorator.getPackage();
		javaTableDecorator.setPackage(originPackage.concat("." + subPackagePath));*/

		getContextMap().put("table", tableDecorator);

		generate(velocityEngine, outputFile);

	}

	/**
	 * 为SOFA提供
	 */
	public void generateTableForSofa(VelocityEngine velocityEngine, TableDecorator tableDecorator)
			throws MiddlegenException {
		String destinationFileName = MessageFormat.format(_destinationFileName,
				new Object[] { tableDecorator.getReplaceName() });

		File outputFile = new File(_destinationDir, destinationFileName);
		if (tableDecorator instanceof IWalletTable) {
			String conf = ((IWalletTable) tableDecorator).getTableConfig().getConfidentiality();
			String integ = ((IWalletTable) tableDecorator).getTableConfig().getIntegrity();
			getMethod(tableDecorator, conf, integ);
		}

		getContextMap().put("table", tableDecorator);
		generate(velocityEngine, outputFile);

	}

	/**
	 * Describe what the method does
	 *
	 * @todo-javadoc Write javadocs for exception
	 * @todo-javadoc Write javadocs for method
	 * @todo-javadoc Write javadocs for method parameter
	 * @todo-javadoc Write javadocs for method parameter
	 * @param velocityEngine
	 *            Describe what the parameter does
	 * @param tableDecorators
	 *            Describe what the parameter does
	 * @exception MiddlegenException
	 *                Describe the exception
	 */
	public void generateForTables(VelocityEngine velocityEngine, Collection tableDecorators)
			throws MiddlegenException {
		File outputFile = new File(_destinationDir, _destinationFileName);

		ArrayList<IWalletTable> list = (ArrayList<IWalletTable>) tableDecorators;

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof IWalletTable) {
				String conf = list.get(i).getTableConfig().getConfidentiality();
				String integ = list.get(i).getTableConfig().getIntegrity();
				if ((conf != null && conf.length() != 0)
						|| (integ != null && integ.length() != 0)) {
					getMethod(list.get(i), conf, integ);
				}
			}
		}
		getContextMap().put("tables", tableDecorators);
		generate(velocityEngine, outputFile);
	}

	/**
	 * Sets the Template attribute of the DefaultFileProducer object
	 *
	 * @param template
	 *            The new Template value
	 */
	void setTemplate(URL template) {
		_template = template;
	}

	/**
	 * Gets the ContextMap attribute of the FileProducer object
	 *
	 * @return The ContextMap value
	 */
	public Map<Object, Object> getContextMap() {
		return _contexMap;
	}

	/**
	 * Copies destination props from another instance
	 *
	 * @todo-javadoc Write javadocs for method parameter
	 * @param other
	 *            Describe what the parameter does
	 */
	void copyPropsFrom(FileProducer other) {
		_destinationDir = other._destinationDir;
		_destinationFileName = other._destinationFileName;
	}

	/**
	 * Describe what the method does
	 *
	 * @todo-javadoc Write javadocs for method
	 * @todo-javadoc Write javadocs for method parameter
	 * @todo-javadoc Write javadocs for return value
	 * @param tableDecorator
	 *            Describe what the parameter does
	 * @return Describe the return value
	 */
	boolean accept(TableDecorator tableDecorator) {
		if (_tableElements.size() == 0) {
			// accept all tables if none are explicitly set.
			return true;
		} else {
			return _tableElements.containsKey(tableDecorator.getSqlName());
		}
	}

	/**
	 * @todo reuse FileProducers and introduce a generateForTable method and a
	 *       generateForTables method.
	 */
	private void generate(VelocityEngine velocityEngine, File outputFile)
			throws MiddlegenException {
		try {
			// Make a context from the map
			VelocityContext context = new VelocityContext(getContextMap());

			for (Map.Entry<String, Object> entry : Utils.findUtils().entrySet()) {
				context.put(entry.getKey(), entry.getValue());
			}

			// Generate in a temporary place first
			File tempFile = File.createTempFile("middlegen", "tmp");

			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

			// The template
			Reader templateReader = new BufferedReader(
					new InputStreamReader(_template.openStream()));

			LogUtils.get().info("Generating " + outputFile.getAbsolutePath()
					+ " using template from " + _template.toString());

			// Run Velocity
			boolean success = velocityEngine.evaluate(context, writer, "middlegen", templateReader);

			writer.flush();
			writer.close();

			if (!success) {
				throw new MiddlegenException("Velocity failed");
			}

			// sql注入漏洞验证
			// sqlInjectionValidate(tempFile);

			LogUtils.get().info("[文件生成]-文件[" + outputFile + "], 覆盖标志[" + justNew + "].");

			String content = FileUtils.readFileToString(tempFile);
			tempFile.delete();

			Set<String> filterKeys = CfgUtils.findFilterKeys();
			if (!filterKeys.isEmpty()) {
				for (String key : filterKeys) {
					String fkey = CfgUtils.findValue(key);
					String fvalue = CfgUtils.findValue(key + ".value");

					LogUtils.get().info("[文件生成]-文件[{}]-FKey[{}]-FValue[{}].", outputFile, fkey,
							fvalue);

					if (StringUtils.contains(content, fkey)) {
						content = StringUtils.replace(content, fkey, fvalue);
					}
				}
			}

			if (!outputFile.exists()) {
				outputFile.getParentFile().mkdirs();
				// FileUtils.copyFile(tempFile, outputFile);
				FileUtils.writeStringToFile(outputFile, content);
			} else if (justNew) {
				if (!DalUtil.contentEquals(tempFile, outputFile)) {
					// 生成后执行校验
					generateAfterValidate(tempFile, outputFile);
					outputFile.delete();
					// FileUtils.copyFile(tempFile, outputFile);
					FileUtils.writeStringToFile(outputFile, content);
				}
			} else {
				LogUtils.get().info("[文件生成]-文件[" + outputFile + "]已经存在, 忽略生成.");
			}

		} catch (IOException e) {
			LogUtils.get().error(e.getMessage(), e);
			throw new MiddlegenException(e.getMessage());
		} catch (ParseErrorException e) {
			LogUtils.get().error(e.getMessage(), e);
			throw new MiddlegenException(e.getMessage());
		} catch (MethodInvocationException e) {
			LogUtils.get().error(e.getMessage(), e);
			throw new MiddlegenException(e.getMessage());
		} catch (ResourceNotFoundException e) {
			LogUtils.get().error(e.getMessage(), e);
			e.printStackTrace();
			throw new MiddlegenException(e.getMessage());
		}
	}

	private void generateAfterValidate(File generatedFile, File replacedFile)
			throws MiddlegenException {
		if (StringUtils.isNotBlank(_validator)) {
			Validator validator = null;
			try {
				Class cls = Class.forName(_validator);
				validator = (Validator) cls.newInstance();
			} catch (Exception e) {
				// 删除生成的临时文件
				generatedFile.delete();

				LogUtils.get().error(e.getMessage(), e);
				e.printStackTrace();
				throw new MiddlegenException("不能创建" + _validator + "的实例,请检查build.xml配置是否正确，确保"
						+ _validator + "存在,详细信息：" + e.getMessage());
			}

			List<ErrorMessage> validateResults = validator.validateAfterGenerate(generatedFile,
					replacedFile, this);
			if (validateResults != null && validateResults.size() > 0) {
				// 删除生成的临时文件
				generatedFile.delete();

				StringBuffer messages = new StringBuffer();
				for (ErrorMessage msg : validateResults) {
					messages.append("\n").append(msg);
				}
				LogUtils.get().error(messages.toString());
				throw new MiddlegenException(
						"执行" + _validator + "验证失败,详细信息：" + messages.toString());
			}
		}
	}

	/**
	 * 为了防止sql注入漏洞增加的验证
	 * <p>
	 * 原理很简单直接判断生成后的文件中是否包含美元符($)，当然只对sqlmap-mapping进行判断
	 *
	 * @param tmpFile
	 * @throws MiddlegenException
	 */
	// private void sqlInjectionValidate(File tmpFile) throws MiddlegenException
	// {
	// try {
	// if (this.getDestinationFileName().endsWith("mapping.xml")) {
	// BufferedReader br = new BufferedReader(new FileReader(tmpFile));
	// String line = null;
	// for (line = br.readLine(); line != null; line = br.readLine()) {
	// if (StringUtils.contains(line, '$')) {
	// String msg =
	// "生成sqlmap不允许出现\"$\"符,模糊查询oracle中请写成'%'||#变量名#||‘%’,mysql中请写成concat('%',#name
	// #,'%')";
	// throw new MiddlegenException(msg);
	// }
	// }
	// }
	// } catch (Exception e) {
	// throw new MiddlegenException("sql注入漏洞验证异常：" + e.getLocalizedMessage());
	// }
	// }

	private void getMethod(TableDecorator tableDecorator, String conf, String integ) {
		// 对操作的每一条进行判定,用于select count(*)的情况
		List<IWalletOperationConfig> list = ((IWalletTable) tableDecorator).getTableConfig()
				.getOperations();
		for (int i = 0; i < list.size(); i++) {
			String sql = list.get(0).getSql();
			int indexFrom = StringUtils.indexOfAny(sql, new String[] { "from", "FROM" });
			String sqlFrom = StringUtils.substring(sql, 0, indexFrom);
			int indexCount = StringUtils.indexOfAny(sqlFrom,
					new String[] { "count(*)", "COUNT(*)" });
			if (indexCount > 0) {
				getContextMap().put("countall", true);
			} else
				getContextMap().put("countall", false);
		}

		char oldC = 0;
		char oldI = 0;
		String finalRet = null;
		String finalI = null;
		if (conf != null && conf.length() != 0) {
			oldC = conf.charAt(0);
		}
		if (integ != null && integ.length() != 0) {
			oldI = integ.charAt(0);
		}
		Character ch = new Character(oldC);
		Character ci = new Character(oldC);
		char newC = ch.toUpperCase(oldC);
		char newI = ci.toUpperCase(oldI);
		if (conf != null && conf.length() != 0) {
			finalRet = conf.replace(oldC, newC);
		}
		if (integ != null && integ.length() != 0) {
			finalI = integ.replace(oldI, newI);
		}
		StringBuffer sb1 = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();

		String method_getConf = sb1.append(finalRet).append("Confidentiality").toString();
		String method2_getInteg = sb2.append(finalI).append("Integrity").toString();
		String confForUpdate = new StringBuffer().append(conf).append("Confidentiality").toString();
		String integForUpdate = new StringBuffer().append(integ).append("Integrity").toString();

		getContextMap().put("finalRet", finalRet);
		getContextMap().put("finalI", finalI);
		getContextMap().put("method_getConf", method_getConf);
		getContextMap().put("method2_getInteg", method2_getInteg);
		getContextMap().put("confForUpdate", confForUpdate);
		getContextMap().put("integForUpdate", integForUpdate);
	}

	public boolean isJustNew() {
		return justNew;
	}

	public void setJustNew(boolean justNew) {
		LogUtils.get().info("[生成设置]-强制覆盖标志-" + justNew);
		this.justNew = justNew;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
