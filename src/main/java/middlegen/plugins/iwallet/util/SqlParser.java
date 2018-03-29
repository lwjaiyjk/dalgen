package middlegen.plugins.iwallet.util;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import Zql.ParseException;
import Zql.ZConstant;
import Zql.ZDelete;
import Zql.ZExp;
import Zql.ZExpression;
import Zql.ZFromItem;
import Zql.ZInsert;
import Zql.ZQuery;
import Zql.ZSelectItem;
import Zql.ZStatement;
import Zql.ZUpdate;
import Zql.ZqlParser;

import com.atom.dalgen.utils.LogUtils;

/**
 * A utility class used to parse sql statements.
 */
public class SqlParser {
    /** the sql statement to be parsed */
    private String     sql;

    /** the parsed sql statement */
    private ZStatement zst;

    /** all parameters for this sql statement */
    private List       params;

    /**
     * Constructor for SqlParser.
     */
    public SqlParser(String sql) {
        LogUtils.get().debug("The sql parameter is " + sql + ".");

        if (sql == null) {
            sql = "";
        }

        this.sql = sql;

        if (!sql.endsWith(";")) {
            sql = sql + ";";
        }

        ZqlParser parser = new ZqlParser();
        parser.addCustomFunction("str2numlist", 1);
        parser.addCustomFunction("instr", 4);
        parser.addCustomFunction("to_char", 2);
        parser.initParser(new ByteArrayInputStream(sql.getBytes()));

        try {
            zst = parser.readStatement();
        } catch (ParseException pe) {
            LogUtils.get().error("Failed to parse sql statement " + sql + " due to exception.", pe);
        }

        LogUtils.get().debug("The parsed statement is " + zst + ".");

        // parse all parameters
        params = new ArrayList();

        if (zst != null) {
            if (zst instanceof ZQuery) {
                // a select statement
                params.addAll(getParams(((ZQuery) zst).getWhere()));
            } else if (zst instanceof ZDelete) {
                params.addAll(getParams(((ZDelete) zst).getWhere()));
            } else if (zst instanceof ZUpdate) {
                // IMPORTANT: To ensure the corrent ordering of parameters,
                //            we can't manipulate on ZUpdate.getSet() to extract
                //            a list of parameters.
                int count = ((ZUpdate) zst).getColumnUpdateCount();

                for (int i = 1; i <= count; i++) {
                    // TODO: an overly simplified approach
                    if (((ZUpdate) zst).getColumnUpdate(i).toString().indexOf("?") >= 0) {
                        params.add(((ZUpdate) zst).getColumnUpdateName(i));
                    }
                }

                params.addAll(getParams(((ZUpdate) zst).getWhere()));
            } else if (zst instanceof ZInsert) {
                Vector columns = ((ZInsert) zst).getColumns();
                Vector values = ((ZInsert) zst).getValues();

                for (int i = 0; i < columns.size(); i++) {
                    // TODO: an overly simplified approach
                    if (values.get(i).toString().indexOf("?") >= 0) {
                        params.add(columns.get(i));
                    }
                }
            }
        }
    }

    /**
     * @return
     */
    public String getSql() {
        return sql;
    }

    /**
     * Returns the zstatement object, for test purpose.
     *
     * @return
     */
    public ZStatement getZst() {
        return zst;
    }

    /**
     * Return the formatted sql statement.
     */
    public String getParsedSql() {
        return zst.toString();
    }

    /**
     * Extract the table name from the sql statement.
     */
    public String getTableName() {
        String tableName = null;

        if (zst instanceof ZQuery) {
            // a select statement
            Vector from = ((ZQuery) zst).getFrom();
            ZFromItem fromItem = (ZFromItem) from.get(0);

            tableName = fromItem.getTable();
        } else if (zst instanceof ZUpdate) {
            tableName = ((ZUpdate) zst).getTable();

            // an update statement
        } else if (zst instanceof ZDelete) {
            tableName = ((ZDelete) zst).getTable();

            // a delete statement 
        } else if (zst instanceof ZInsert) {
            tableName = ((ZInsert) zst).getTable();

            // an insert statement
        }

        return tableName;
    }

    protected List getParams(ZExp exp) {
        List params = new ArrayList();

        if (exp instanceof ZQuery) {
            params.addAll(getParams(((ZQuery) exp).getWhere()));
        } else if (exp instanceof ZExpression) {
            ZExpression expression = (ZExpression) exp;
            int opNum = expression.nbOperands();

            LogUtils.get().debug("The number of operands for expression \"" + expression + "\" is " + opNum);

            if (opNum == 1) {
                params.addAll(getParams(expression.getOperand(0)));
            } else if (opNum == 2) {
                ZExp ol = expression.getOperand(0);
                ZExp or = expression.getOperand(1);

                LogUtils.get().debug("The left operand is " + ol + " with type " + ol.getClass() + ".");
                LogUtils.get().debug("The right operand is " + or + " with type " + or.getClass() + ".");

                if ((ol instanceof ZConstant) && (or instanceof ZExpression)) {
                    ZConstant cl = (ZConstant) ol;
                    ZExpression cr = (ZExpression) or;

                    LogUtils.get().debug("The type of the left constant is " + cl.getType());

                    if (cl.getType() == ZConstant.COLUMNNAME) {
                        // TODO: an overly simplified approach, need more processing 
                        if (cr.toString().indexOf("?") >= 0) {
                            if (expression.getOperator().equalsIgnoreCase("in")) {
                                params.add(cl.getValue() + "_list");
                            } else {
                                params.add(cl.getValue());
                            }
                        }
                    }
                } else {
                    params.addAll(getParams(ol));
                    params.addAll(getParams(or));
                }
            } else {
                // TODO: over simplified?
                for (int i = 0; i < opNum; i++) {
                    params.addAll(getParams(expression.getOperand(i)));
                }
            }

            // TODO: add support for more operand number
        } else if (exp instanceof ZConstant) {
            // do nothing
        }

        return params;
    }

    public List getParams() {
        return params;
    }

    public boolean isSelectItemSingle() {
        if (zst instanceof ZQuery) {
            Vector items = ((ZQuery) zst).getSelect();

            if (items.size() == 1) {
                ZSelectItem item = (ZSelectItem) items.get(0);

                if ((item.getAggregate() == null) && item.isWildcard()) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public ZSelectItem getSelectItem() {
        if (isSelectItemSingle()) {
            return (ZSelectItem) ((ZQuery) zst).getSelect().get(0);
        } else {
            return null;
        }
    }
}
