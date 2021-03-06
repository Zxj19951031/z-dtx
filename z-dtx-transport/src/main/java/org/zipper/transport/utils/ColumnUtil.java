package org.zipper.transport.utils;

import lombok.extern.slf4j.Slf4j;
import org.zipper.helper.exception.ErrorCode;
import org.zipper.helper.exception.HelperException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Column 操作
 * <p>
 * mysql:字段列
 *
 * @author zhuxj
 * @since 2020/5/12
 */
@Slf4j
public class ColumnUtil {
    /**
     * 获取MySql 字段列表
     *
     * @param conn    数据库连接
     * @param catalog schema数据库
     * @param table   数据表
     * @return list of columnName[columnType]
     */
    public static List<String> getMySqlColumns(Connection conn, String catalog, String table) {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet resultSet = metaData.getColumns(catalog, null, table, "%");
            List<String> columns = new ArrayList<>();
            while (resultSet.next()) {
                columns.add(String.format("%s[%s]", resultSet.getString("COLUMN_NAME"), resultSet.getString("TYPE_NAME")));
            }
            resultSet.close();
            conn.close();
            return columns;
        } catch (SQLException e) {
            log.error("A error caused when getting MySql Columns", e);
            throw HelperException.newException(ErrorCode.QUERY_DB_ERROR, "查询目标MySql数据源表列表时失败，请联系管理员");
        }
    }

    /**
     * 获取 Oracle 字段列表
     *
     * @param conn    数据库连接
     * @param catalog schema数据库
     * @param table   数据表
     * @return list of columnName[columnType]
     */
    public static List<String> getOracleColumns(Connection conn, String catalog, String table) {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, catalog, table, "%");
            List<String> columns = new ArrayList<>();
            while (resultSet.next()) {
                columns.add(String.format("%s[%s]", resultSet.getString("column_name"), resultSet.getString("type_name")));
            }
            resultSet.close();
            conn.close();
            return columns;
        } catch (SQLException e) {
            log.error("A error caused when getting Oracle Columns", e);
            throw HelperException.newException(ErrorCode.QUERY_DB_ERROR, "查询目标Oracle数据源表列表时失败，请联系管理员");
        }
    }
}
