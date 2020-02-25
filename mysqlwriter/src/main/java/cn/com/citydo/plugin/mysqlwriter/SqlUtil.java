package cn.com.citydo.plugin.mysqlwriter;

import cn.com.citydo.consts.exceptions.SysException;
import cn.com.citydo.consts.json.Configuration;
import cn.com.citydo.dtx.common.spi.errors.CommonError;
import cn.com.citydo.dtx.common.spi.errors.PluginError;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SqlUtil {


    private static String format = "select %s from %s where 1=1";
    private static String insertMode = "insert into %s(%s) values (%s)";
    private static String updateMode = insertMode + " on duplicate key update %s";
    private static String replaceMode = "replace into %s(%s) values (%s)";

    public static String buildQuerySql(Configuration config) {
        return String.format(format, getColumns(config), getTableName(config));
    }

    private static String getTableName(Configuration config) {
        return config.getString(Keys.TABLE);
    }

    private static String getColumns(Configuration config) {
        List<String> columns = config.getList(Keys.COLUMNS, String.class);
        return CollectionUtil.join(columns, ",");
    }

    public static String buildInsertSql(Configuration config) {
        List<String> columns = config.getList(Keys.COLUMNS, String.class);
        columns.replaceAll(a -> "?");
        String unknown = CollectionUtil.join(columns, ",");

        switch (config.getString(Keys.METHOD)) {
            case "insert":
                return String.format(insertMode, getTableName(config), getColumns(config), unknown);
            case "update":
                List<String> cols = config.getList(Keys.COLUMNS, String.class);
                cols.replaceAll(str -> str + "=values(" + str + ")");
                String val = CollectionUtil.join(cols, ",");
                return String.format(updateMode, getTableName(config), getColumns(config), unknown, val);
            case "replace":
                return String.format(replaceMode, getTableName(config), getColumns(config), unknown);
            default:
                throw SysException.newException(CommonError.CONFIG_ERROR,
                        String.format("不支持插入方式：%s,可供选择的类型有 insert， update，replace", config.getString(Keys.METHOD)));
        }
    }

    public static Triple<List<String>, List<Integer>, List<String>> getColumnMetaData(Connection conn, Configuration config) throws SQLException {
        Statement statement = null;
        ResultSet rs = null;

        Triple<List<String>, List<Integer>, List<String>> columnMetaData = new ImmutableTriple<List<String>, List<Integer>, List<String>>(
                new ArrayList<String>(), new ArrayList<Integer>(),
                new ArrayList<String>());
        try {
            statement = conn.createStatement();
            String queryColumnSql = "select " + getColumns(config) + " from " + getTableName(config)
                    + " where 1=2";

            rs = statement.executeQuery(queryColumnSql);
            ResultSetMetaData rsMetaData = rs.getMetaData();
            for (int i = 0, len = rsMetaData.getColumnCount(); i < len; i++) {

                columnMetaData.getLeft().add(rsMetaData.getColumnName(i + 1));
                columnMetaData.getMiddle().add(rsMetaData.getColumnType(i + 1));
                columnMetaData.getRight().add(
                        rsMetaData.getColumnTypeName(i + 1));
            }
            return columnMetaData;

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw SysException.newException(PluginError.GET_COLUMN_INFO_FAILED,
                    String.format("获取表:%s 的字段的元信息时失败. 请联系 DBA 核查该库、表信息，错误 %s", getTableName(config), e.getMessage()));
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (rs != null) {
                rs.close();
            }

        }
    }


}
