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
public class CatalogUtil {

    private static final String TABLE_CAT = "TABLE_CAT";

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
            throw SysException.newException(DBError.QUERY_ERROR, "查询目标MySql数据源库列表时失败，请联系管理员");
        }
    }
}