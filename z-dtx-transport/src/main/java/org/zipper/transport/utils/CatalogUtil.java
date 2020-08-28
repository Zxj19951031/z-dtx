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
 * Catalog操作
 * <p>
 * mysql ：schema
 *
 * @author zhuxj
 * @since 2020/5/12
 */
@Slf4j
public class CatalogUtil {

    private static final String TABLE_CAT = "TABLE_CAT";

    /**
     * 获取MySql数据库的Schema列表
     * @param conn 数据库连接
     * @return list of schema
     */
    public static List<String> getMySqlCatalogs(Connection conn) {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet resultSet = metaData.getCatalogs();
            List<String> catalogs = new ArrayList<>();
            while (resultSet.next()) {
                catalogs.add(resultSet.getString(TABLE_CAT));
            }
            resultSet.close();
            conn.close();
            return catalogs;
        } catch (SQLException e) {
            log.error("A error caused when getting MySql schemas", e);
            throw HelperException.newException(ErrorCode.QUERY_ERROR, "查询目标MySql数据源库列表时失败，请联系管理员");
        }
    }
}
