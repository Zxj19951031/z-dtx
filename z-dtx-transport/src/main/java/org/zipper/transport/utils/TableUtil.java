package org.zipper.transport.utils;

import lombok.extern.slf4j.Slf4j;
import org.zipper.common.exceptions.SysException;
import org.zipper.common.exceptions.errors.DBError;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhuxj
 * @since 2020/5/12
 */
@Slf4j
public class TableUtil {

    private static final String TABLE_NAME = "TABLE_NAME";

    public static List<String> getMySqlTables(Connection conn, String schema) {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet resultSet = metaData.getTables(schema, null, null, null);
            List<String> tables = new ArrayList<>();
            while (resultSet.next()) {
                tables.add(resultSet.getString(TABLE_NAME));
            }
            resultSet.close();
            conn.close();
            return tables;
        } catch (SQLException e) {
            log.error("A error caused when getting MySql Tables", e);
            throw SysException.newException(DBError.QUERY_ERROR, "查询目标MySql数据源表列表时失败，请联系管理员");
        }
    }
}
