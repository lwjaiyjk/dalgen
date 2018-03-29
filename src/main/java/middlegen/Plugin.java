package middlegen;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import com.atom.dalgen.utils.CfgUtils;

import middlegen.plugins.iwallet.config.IWalletConfigException;
import middlegen.plugins.iwallet.util.DalUtil;

/**
 * This is the baseclass for plugins. It can be subclassed to add additional
 * functionality, or it can be used "as-is".
 */
public class Plugin {

	/**
	 * @todo-javadoc Describe the field
	 */
	private Middlegen middlegen;

	/**
	 * @todo-javadoc Describe the column
	 */
	private final Map<DbColumn, ColumnDecorator> columnDecorators = new HashMap<DbColumn, ColumnDecorator>();
	/**
	 * @todo-javadoc Describe the column
	 */
	private final Map<String, TableDecorator> tableDecorators = new HashMap<String, TableDecorator>();
	/**
	 * @todo-javadoc Describe the column
	 */
	private final Class<?>[] columnDecoratorConstructorArgs = new Class<?>[] { Column.class };
	/**
	 * @todo-javadoc Describe the column
	 */
	private final Class<?>[] tableDecoratorConstructorArgs = new Class<?>[] { Table.class };

	/**
	 * @todo-javadoc Describe the column
	 */
	private File destinationDir;

	/** The name of the plugin */
	private String name;

	/**
	 * @todo-javadoc Describe the field
	 */
	private String mergedir;

	/**
	 * @todo-javadoc Describe the field
	 */
	private Map<String, FileProducer> fileProducers = new HashMap<String, FileProducer>();
	/**
	 * @todo-javadoc Describe the field
	 */
	private String displayName;

	/** Whether or not to use the schema prefix in generated code. */
	private boolean useSchemaPrefix = false;

	/** Constructor */
	public Plugin() {
	}

	/**
	 * Describe what the setUseSchemaPrefix constructor does
	 *
	 * @todo-javadoc Write javadocs for constructor
	 * @todo-javadoc Write javadocs for method parameter
	 * @param flag
	 *            Describe what the parameter does
	 */
	public void setUseSchemaPrefix(boolean flag) {
		useSchemaPrefix = flag;
	}

	/**
	 * Sets the Mergedir attribute of the Entity20Plugin object
	 *
	 * @param md
	 *            The new Mergedir value
	 */
	public void setMergedir(String md) {
		mergedir = md;
	}

	/**
	 * The root folder where the sources will be written. This value overrides
	 * the destination attribute specified on the Ant task level.
	 *
	 * @param dir
	 *            The new Destination value
	 */
	public void setDestination(File dir) {
		destinationDir = dir;
	}

	/**
	 * Sets the logical plugin name. Not intended to be called from Ant.
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the UseSchemaPrefix attribute of the Plugin object
	 *
	 * @return The UseSchemaPrefix value
	 */
	public boolean isUseSchemaPrefix() {
		return useSchemaPrefix;
	}

	/**
	 * Returns the name to be used in the relations. Can be overridden in
	 * subclasses
	 */
	// public String getRelationName(Table table) {
	// return table.getSqlName() + "-" + getName();
	// }

	/**
	 * Gets the Middlegen attribute of the Plugin object
	 *
	 * @return The Middlegen value
	 */
	public Middlegen getMiddlegen() {
		return middlegen;
	}

	/**
	 * Gets the DestinationDir attribute of the Plugin object
	 *
	 * @return The DestinationDir value
	 */
	public File getDestinationDir() {
		return destinationDir;
	}

	/**
	 * Gets the DisplayName attribute of the ClassType object
	 *
	 * @return The DisplayName value
	 */
	public final String getDisplayName() {
		return displayName;
	}

	/**
	 * Returns the name of the plugin.
	 *
	 * @return The Name value
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Gets the ColumnDecoratorClass attribute of the Plugin object
	 *
	 * @return The ColumnDecoratorClass value
	 */
	public Class<ColumnDecorator> getColumnDecoratorClass() {
		return ColumnDecorator.class;
	}

	/**
	 * Gets the TableDecoratorClass attribute of the Plugin object
	 *
	 * @return The TableDecoratorClass value
	 */
	public Class<TableDecorator> getTableDecoratorClass() {
		return TableDecorator.class;
	}

	/**
	 * Gets the Tables attribute of the Plugin object
	 *
	 * @return The Tables value
	 */
	public final Collection<TableDecorator> getTables() {
		return tableDecorators.values();
	}

	/**
	 * Gets the Table attribute of the Plugin object
	 *
	 * @todo-javadoc Write javadocs for method parameter
	 * @param sqlName
	 *            Describe what the parameter does
	 * @return The Table value
	 */
	public final TableDecorator getTable(String sqlName) {
		return tableDecorators.get(sqlName);
	}

	/**
	 * Gets the Mergedir attribute of the Entity20Plugin object
	 *
	 * @return The Mergedir value
	 */
	public String getMergedir() {
		return mergedir;
	}

	/**
	 * Adds a file producer. If the file producer's file name contains the
	 * String {0}, Middlegen will assume this is a per-table file producer, and
	 * one instance for each table will be created. This method can be called
	 * from Ant or from subclasses. <BR>
	 *
	 *
	 * @param fileProducer
	 *            the FileProducer to add.
	 */
	public void addConfiguredFileproducer(FileProducer fileProducer) {
		fileProducer.validate();
		String id = fileProducer.getId();
		if (id == null) {
			// YUK. Magic id :-(
			fileProducer.setId("__custom_" + fileProducers.size());
		}

		FileProducer customFileProducer = fileProducers.get(id);
		if (customFileProducer != null) {
			// A custom file producer has been specified in Ant. Override the
			// destination.
			customFileProducer.copyPropsFrom(fileProducer);
		} else {
			// use the added file producer, but perform some sanity checks
			// first.
			fileProducers.put(fileProducer.getId(), fileProducer);
		}
	}

	/**
	 * Creates and caches decorators for all Tables and Columns.
	 */
	public final void decorateAll(Collection<DbTable> tables) {
		// loop over all tables
		for (DbTable table : tables) {
			// decorate table
			TableDecorator tableDecorator = createDecorator(table);
			tableDecorator.setPlugin(this);

			// cache it using subject as key. will be by clients as argument to
			// decorate()
			tableDecorators.put(table.getSqlName(), tableDecorator);

			// decorate columns and store refs in newly created TableDecorator
			DbColumn pkColumn = (DbColumn) table.getPkColumn();
			if (pkColumn != null) {
				ColumnDecorator pkColumnDecorator = createDecorator(pkColumn);
				pkColumnDecorator.setTableDecorator(tableDecorator);
				tableDecorator.setPkColumnDecorator(pkColumnDecorator);
				columnDecorators.put(pkColumn, pkColumnDecorator);
			}

			Collection<ColumnDecorator> colnDecorators = new ArrayList<ColumnDecorator>(
					table.getColumns().size());
			for (Column coltmp : table.getColumns()) {
				ColumnDecorator columnDecorator = createDecorator((DbColumn) coltmp);
				columnDecorator.setPlugin(this);
				columnDecorator.setTableDecorator(tableDecorator);
				colnDecorators.add(columnDecorator);

				columnDecorators.put((DbColumn) coltmp, columnDecorator);
			}

			tableDecorator.setColumnDecorators(colnDecorators);
		}
		// now that everything is properly set up, call init on all decorators.
		for (TableDecorator tbdecorator : tableDecorators.values()) {
			tbdecorator.init();
		}

		for (ColumnDecorator coldecorator : columnDecorators.values()) {
			coldecorator.init();
		}
	}

	/**
	 * Validates that the plugin is correctly configured
	 *
	 * @exception MiddlegenException
	 *                if the state is invalid
	 */
	public void validate() throws MiddlegenException {
		if (destinationDir == null) {
			throw new MiddlegenException("destination must be specified in <" + getName() + ">");
		}
	}

	/**
	 * Describe what the method does
	 *
	 * @todo-javadoc Write javadocs for method
	 * @todo-javadoc Write javadocs for method parameter
	 * @todo-javadoc Write javadocs for return value
	 * @param mergeFile
	 *            Describe what the parameter does
	 * @return Describe the return value
	 */
	public boolean mergeFileExists(String mergeFile) {
		return new File(getMergedir(), mergeFile).exists();
	}

	/**
	 * Sets the DisplayName attribute of the Plugin object
	 *
	 * @param s
	 *            The new DisplayName value
	 */
	protected final void setDisplayName(String s) {
		displayName = s;
	}

	/**
	 * Describe what the method does
	 *
	 * @todo-javadoc Write javadocs for method
	 */
	protected void registerFileProducers() {
	}

	/**
	 * Describe what the method does
	 */
	protected void generate() throws MiddlegenException {
		registerFileProducers();

		VelocityEngine velocityEngine = getEngine();

		doIt(velocityEngine);
	}

	/**
	 * Sets the Middlegen attribute of the Plugin object
	 *
	 * @param middlegen
	 *            The new Middlegen value
	 */
	public void setMiddlegen(Middlegen middlegen) {
		this.middlegen = middlegen;
	}

	/**
	 * Describe what the method does
	 *
	 * @todo-javadoc Write javadocs for method
	 * @todo-javadoc Write javadocs for method parameter
	 * @todo-javadoc Write javadocs for return value
	 * @param column
	 *            Describe what the parameter does
	 * @return Describe the return value
	 */
	final Column decorate(Column column) {
		if (column.getClass() != DbColumn.class) {
			throw new IllegalArgumentException(
					"column must be of class " + DbColumn.class.getName());
		}

		ColumnDecorator result = columnDecorators.get(column);
		if (result == null) {
			throw new IllegalArgumentException("result can't be null!");
		}

		return result;
	}

	/**
	 * Describe what the method does
	 *
	 * @todo-javadoc Write javadocs for method
	 * @todo-javadoc Write javadocs for method parameter
	 * @todo-javadoc Write javadocs for return value
	 * @param table
	 *            Describe what the parameter does
	 * @return Describe the return value
	 */
	public final Table decorate(Table table) {
		if (!table.getClass().equals(DbTable.class)) {
			throw new IllegalArgumentException("table must be of class " + DbTable.class.getName());
		}

		TableDecorator result = tableDecorators.get(table.getSqlName());
		if (result == null) {
			throw new IllegalArgumentException("result can't be null!");
		}
		return result;
	}

	/**
	 * Returns all the tabledecorators' file producers. Override this method if
	 * you want different behaviour.
	 *
	 * @return The FileProducers value
	 */
	protected final Collection<FileProducer> getFileProducers() {
		return fileProducers.values();
	}

	/**
	 * Gets the Engine attribute of the Middlegen object
	 *
	 * @todo-javadoc Write javadocs for method parameter
	 * @todo-javadoc Write javadocs for exception
	 * @return The Engine value
	 * @throws Exception
	 */
	private VelocityEngine getEngine() throws MiddlegenException {
		Properties props = new Properties();
		props.setProperty(Velocity.INPUT_ENCODING,
				CfgUtils.findValue(Velocity.INPUT_ENCODING, "GBK"));
		props.setProperty(Velocity.OUTPUT_ENCODING,
				CfgUtils.findValue(Velocity.OUTPUT_ENCODING, "GBK"));

		// only load templates from file we don't have access to the jar and use
		// a workaround for that
		props.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
		// use a resource loader that won't throw an exception if a resource
		// (file) isn't found
		props.setProperty("file.resource.loader.class", KindFileResourceLoader.class.getName());
		// tell velocity where merge files are located
		if (getMergedir() != null) {
			props.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, getMergedir());
		}
		// use our own log system that doesn't close the appenders upon gc()
		// (the velocity one does)
		props.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
				DontCloseLog4JLogSystem.class.getName());
		try {
			VelocityEngine velocityEngine = new VelocityEngine();
			velocityEngine.init(props);

			return velocityEngine;
		} catch (Exception e) {
			// Hmm, throwning Exception is bad API, Velocity guys ;-)
			e.printStackTrace();
			throw new MiddlegenException(e.getMessage());
		}
	}

	/**
	 * Adds additional file producers. This method is called right before the
	 * generation starts. Depending on the fileName and tableDecorators
	 * parameters, several things can happen:
	 * <p>
	 *
	 * If fileName contains {0}, a copy of each of these file producers is
	 * created, substituting the {0} with the table name, and the original one
	 * is removed.
	 */
	private void doIt(VelocityEngine engine) throws MiddlegenException {
		for (FileProducer fileProducer : getFileProducers()) {
			if (fileProducer.isGenerationPerTable()) {
				// explode this file producer in multiple instances, potentially
				// one for every table (or less, if the file producer knows
				// exactly
				// what tables it cares about).
				for (TableDecorator tableDecorator : getTables()) {
					// ȡ�õ�ǰ����
					String tableName = tableDecorator.getName();

					// Check if we should generate for the table.
					try {
						if (DalUtil.inTabs(tableName)
								&& tableDecorator.getTableElement().isGenerate()) {
							// Check whether the file producer accepts this
							// table
							if (tableDecorator.isGenerate()
									&& fileProducer.accept(tableDecorator)) {
								fileProducer.getContextMap().put("plugin", this);
								fileProducer.generateTableForSofa(engine, tableDecorator);
							}
						}
					} catch (IWalletConfigException e) {
						throw new MiddlegenException(e.getMessage());
					}
				}
			} else {
				// This file producer will take a collection of table decorators
				// in stead of
				// one single table. Let's see if it wants all or only a subset.
				List<TableDecorator> acceptedTableDecorators = new ArrayList<TableDecorator>();
				for (TableDecorator tableDecorator : getTables()) {
					if (tableDecorator.getTableElement().isGenerate()) {
						if (tableDecorator.isGenerate() && fileProducer.accept(tableDecorator)) {
							acceptedTableDecorators.add(tableDecorator);
						}
					}
				}

				fileProducer.getContextMap().put("plugin", this);
				fileProducer.generateForTables(engine, acceptedTableDecorators);
			}
		}
	}

	/**
	 * Describe what the method does
	 *
	 * @todo-javadoc Write javadocs for return value
	 * @todo-javadoc Write javadocs for method
	 * @todo-javadoc Write javadocs for method parameter
	 * @param column
	 *            Describe what the parameter does
	 * @return Describe the return value
	 */
	private final ColumnDecorator createDecorator(DbColumn column) {
		Object decorator = columnDecorators.get(column);

		if (decorator == null) {
			decorator = createDecorator(column, getColumnDecoratorClass(),
					columnDecoratorConstructorArgs);
		}

		return (ColumnDecorator) decorator;
	}

	/**
	 * Describe what the method does
	 */
	private final TableDecorator createDecorator(DbTable table) {
		Object decorator = tableDecorators.get(table.getSqlName());

		if (decorator == null) {
			decorator = createDecorator(table, getTableDecoratorClass(),
					tableDecoratorConstructorArgs);
		}

		return (TableDecorator) decorator;
	}

	/**
	 * Describe what the method does
	 */
	private final Object createDecorator(Object subject, Class<?> decoratorClass,
			Class<?>[] decoratorConstructorArgs) {
		Object decorator = null;
		String invokedConstructor = decoratorClass.getName() + "(" + subject.getClass().getName()
				+ ")";
		try {
			Constructor<?> constructor = decoratorClass.getConstructor(decoratorConstructorArgs);
			decorator = constructor.newInstance(new Object[] { subject });
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException("Couldn't invoke constructor " + invokedConstructor);
		}

		return decorator;
	}
}
