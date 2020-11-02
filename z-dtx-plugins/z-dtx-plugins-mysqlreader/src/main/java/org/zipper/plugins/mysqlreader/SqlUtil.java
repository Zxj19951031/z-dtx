package org.zipper.plugins.mysqlreader;

import cn.hutool.core.collection.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zipper.plugins.common.errors.CommonError;
import org.zipper.helper.exception.HelperException;
import org.zipper.helper.util.json.JsonObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlUtil {

    private static final Logger log = LoggerFactory.getLogger(SqlUtil.class);

    public static List<Integer> splitPrimary(JsonObject config, int channel) {
        String primaryKey = config.getString(Keys.PRIMARY_KEY);
        if (StringUtils.isEmpty(primaryKey)) {
            throw HelperException.newException(CommonError.PLUGIN_SPLIT_ERROR,
                    "切分任务时需要提供必要的主键字段，且目前只支持整型类型");
        }
        List<Integer> result = new ArrayList<>();

        String jdbcUrl = config.getString(Keys.JDBC_URL);
        String username = config.getString(Keys.USERNAME);
        String password = config.getString(Keys.PASSWORD);
        int loginTimeout = config.getInt(Keys.LOGIN_TIMEOUT, 120);
        int queryTimeout = config.getInt(Keys.QUERY_TIMEOUT, 60);
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            DriverManager.setLoginTimeout(loginTimeout);
            connection = DriverManager.getConnection(jdbcUrl, username, password);
            statement = connection.createStatement();
            statement.setQueryTimeout(queryTimeout);

            String primaryKeySql =
                    String.format("select data_type from `information_schema`.columns where table_name='%s' and column_name='%s'",
                            getTableName(config), primaryKey);
            resultSet = statement.executeQuery(primaryKeySql);
            while (resultSet.next()) {
                String type = resultSet.getString(1);
                if (StringUtils.isEmpty(type)) {
                    throw HelperException.newException(CommonError.PLUGIN_SPLIT_ERROR,
                            String.format("目标表中不存在分割字段%s", primaryKey));
                }
                if (!"int".equals(type.toLowerCase())) {
                    throw HelperException.newException(CommonError.PLUGIN_SPLIT_ERROR,
                            String.format("当前只支持int类型的分割字段,不支持%s类型", type.toLowerCase()));
                }
            }

            log.info("查询计划读取记录数...");
            String countSql = String.format("select count(*) from (%s) as a", SqlUtil.buildQuerySql(config));
            resultSet = statement.executeQuery(countSql);
            while (resultSet.next()) {
                int count = resultSet.getInt(1);
                log.info("总记录数[{}]", count);
                int limit = count / channel;
                for (int i = 0; i < channel; i++) {
                    if ((i * limit) < count) {
                        result.add(i * limit);
                    }
                }
                if (result.size() < channel) {
                    result.add(count);
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            log.error("计算分片异常!!!", e);
            throw HelperException.newException(CommonError.PLUGIN_SPLIT_ERROR,
                    String.format("计算分片失败，目标地址:[%s]，用户名：[%s],密码：[%s]",
                            jdbcUrl, username, password));
        } finally {
            try {
                if (null != resultSet) {
                    resultSet.close();
                }
                if (null != statement) {
                    statement.close();
                }
                if (null != connection) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.error("关闭数据库连接失败!!!", e);
            }
        }

        return result;
    }

    public static String buildQuerySql(JsonObject config) {
        String querySql = config.getString(Keys.QUERY_SQL);

        if (StringUtils.isNotBlank(querySql)) {
            log.info("监测到配置了querySql参数");
            String format = "select %s from (%s) as qs";
            return String.format(format, getColumns(config, "qs"), config.getString(Keys.QUERY_SQL).replace(";", ""));
        } else {
            String format = "select %s from %s where %s";
            return String.format(format, getColumns(config), getTableName(config), getWhere(config));
        }
    }

    private static String getTableName(JsonObject config) {
        return config.getString(Keys.TABLE);
    }

    private static String getColumns(JsonObject config) {
        List<String> columns = config.getList(Keys.COLUMNS, String.class);
        return CollectionUtil.join(columns, ",");
    }

    private static String getColumns(JsonObject config, String alias) {
        List<String> columns = config.getList(Keys.COLUMNS, String.class);
        columns.forEach(column -> column = alias + "." + column);
        return CollectionUtil.join(columns, ",");
    }

    private static String getWhere(JsonObject config) {
        return config.getString(Keys.WHERE, "1=1");
    }

    public static List<String> splitWhere(JsonObject config, List<Integer> splitPrimary) {
        String primaryKey = config.getString(Keys.PRIMARY_KEY);
        String query = buildQuerySql(config);
        String sql = "select * from (" + query + ") as t where t." + primaryKey + " > %s";

        log.info("分割任务后计划执行以下查询sql");
        List<String> queryList = new ArrayList<>();
        for (int i = 0; i < splitPrimary.size(); i++) {
            if (i == splitPrimary.size() - 1) {
                queryList.add(String.format(sql, splitPrimary.get(i)));
            } else {
                queryList.add(String.format(sql + " and t." + primaryKey + " <= %s", splitPrimary.get(i), splitPrimary.get(i + 1)));
            }
            log.info(queryList.get(i));
        }
        return queryList;
    }
}
