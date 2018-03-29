/*
 * Copyright (c) 2001, Aslak Helles酶y, BEKK Consulting
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * - Neither the name of BEKK Consulting nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */
package middlegen;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import middlegen.plugins.iwallet.IWalletPlugin;

import com.atom.dalgen.utils.LogUtils;

/**
 * This class implements the core engine that will initialize and invoke all the plugins.
 */
public class Middlegen {

    /** 数据库信息 */
    private DatabaseInfo                    databaseInfo;

    /** 生成任务 */
    private MiddlegenTask                   middlegenTask;

    private final Map<String, TableElement> tableElements = new HashMap<String, TableElement>();

    private final Map<String, DbTable>      dbTables      = new HashMap<String, DbTable>();

    /** All plugins */
    private final List<Plugin>              plugins       = new LinkedList<Plugin>();

    /** Maps logical name to plugin class */
    private final Map<String, Class<?>>     pluginClasses = new HashMap<String, Class<?>>();

    public static final String              NL            = System.getProperty("line.separator");

    /**
     * Creates a new Middlegen object
     */
    public Middlegen(MiddlegenTask middlegenTask) {
        this.middlegenTask = middlegenTask;

        LogUtils.log("系统启动，注册[IWalletPlugin]插件...");

        this.registerPlugin("iwallet", IWalletPlugin.class);
    }

    public DatabaseInfo getDatabaseInfo() {
        return this.databaseInfo;
    }

    public Map<String, TableElement> getTableElements() {
        return tableElements;
    }

    public MiddlegenTask getMiddlegenTask() {
        return this.middlegenTask;
    }

    public Collection<DbTable> getTables() {
        return dbTables.values();
    }

    /**
     * Gets all the tables. The returned tables are decorated by the plugin.
     */
    public Collection<Table> getTables(Plugin plugin) {
        Collection<Table> result = new LinkedList<Table>();

        for (DbTable table : this.getTables()) {
            result.add(plugin.decorate(table));
        }

        return result;
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }

    public Plugin getPlugin(String name) {
        for (Plugin plugin : this.getPlugins()) {
            if (StringUtils.equals(plugin.getName(), name)) {
                return plugin;
            }
        }

        return null;
    }

    public DbTable getTable(String tableSqlName) {
        DbTable result = dbTables.get(StringUtils.lowerCase(tableSqlName));

        if (result == null) {
            throw new IllegalArgumentException("Couldn't find any table named " + tableSqlName + ". Check the spelling and make sure it figures among the declared tables.");
        }

        return result;
    }

    public Class<?> getPluginClass(String name) {
        return pluginClasses.get(name);
    }

    /**
     * Returns true if Middlegen contains the table
     */
    public boolean containsTable(String tableSqlName) {
        return dbTables.containsKey(StringUtils.lowerCase(tableSqlName));
    }

    /**
     * Adds a plugin
     */
    public void addPlugin(Plugin plugin) {
        plugins.add(plugin);
        plugin.setMiddlegen(this);
    }

    public void addTableElement(TableElement tableElement) {
        tableElements.put(tableElement.getName(), tableElement);
    }

    public void clear() {
        dbTables.clear();
    }

    /**
     * Adds a table
     */
    public void addTable(DbTable table) {
        dbTables.put(StringUtils.lowerCase(table.getSqlName()), table);
    }

    public void registerPlugin(String name, Class<?> clazz) {
        LogUtils.log("注册插件： " + name + "->" + clazz.getName());
        pluginClasses.put(name, clazz);
    }

    /**
     * Tells all file types to decorate all columns and tables. Called by ant
     * task before gui is shown and the generation begins.
     */
    public void decorateAll() {
        for (Plugin plugin : this.getPlugins()) {
            plugin.decorateAll(this.getTables());
        }
    }

    public void validate() throws MiddlegenException {
        // verify that we don't already have a plugin with the same name
        Set<String> pluginNames = new HashSet<String>();

        for (Plugin plugin : getPlugins()) {
            if (pluginNames.contains(plugin.getName())) {
                String msg = "插件已存在[" + plugin.getName() + "]，请使用其它插件名！";
                LogUtils.log(msg);

                throw new MiddlegenException(msg);
            }

            pluginNames.add(plugin.getName());

            LogUtils.log("验证插件：" + plugin.getName());
            plugin.validate();
        }
    }

    /**
     * Generates source files for all registered file types
     */
    public void writeSource() throws MiddlegenException {
        for (Plugin plugin : plugins) {
            LogUtils.log("执行插件生成代码：" + plugin.getName());
            plugin.generate();
        }
    }

    public void setDatabaseInfo(DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
    }
    
}
