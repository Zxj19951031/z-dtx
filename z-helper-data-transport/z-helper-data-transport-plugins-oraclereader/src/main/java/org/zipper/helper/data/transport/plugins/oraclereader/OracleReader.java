package org.zipper.helper.data.transport.plugins.oraclereader;


import cn.hutool.core.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zipper.helper.data.transport.common.Reader;
import org.zipper.helper.data.transport.common.columns.*;
import org.zipper.helper.data.transport.common.errors.CommonError;
import org.zipper.helper.data.transport.common.errors.PluginError;
import org.zipper.helper.data.transport.common.records.DataRecord;
import org.zipper.helper.data.transport.common.records.Record;
import org.zipper.helper.data.transport.common.tunnels.RecordProducer;
import org.zipper.helper.exception.HelperException;
import org.zipper.helper.util.json.JsonObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * mysql 读取插件
 *
 * @author zhuxj
 */
public class OracleReader extends Reader {
    public static class Job extends Reader.Job {
        private static final Logger log = LoggerFactory.getLogger(Job.class);

        @Override
        public void init() {
            String jdbcUrl = this.getAllConfig().getString(Keys.JDBC_URL);
            String username = this.getAllConfig().getString(Keys.USERNAME);
            String password = this.getAllConfig().getString(Keys.PASSWORD);
            int loginTimeout = this.getAllConfig().getInt(Keys.LOGIN_TIMEOUT, 120);
            int queryTimeout = this.getAllConfig().getInt(Keys.QUERY_TIMEOUT, 60);
            Connection connection = null;
            Statement statement = null;
            ResultSet resultSet = null;
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                log.info("设置登录超时时间[{}]秒...", loginTimeout);
                DriverManager.setLoginTimeout(loginTimeout);

                log.info("尝试连接至目标数据源...");
                connection = DriverManager.getConnection(jdbcUrl, username, password);
                statement = connection.createStatement();

                log.info("设置查询超时时间[{}]秒...", queryTimeout);
                statement.setQueryTimeout(queryTimeout);

                log.debug("校验目标读取字段及表存在性...");
                String sql = String.format("select * from (%s) as tmp where 1=0", SqlUtil.buildQuerySql(getAllConfig()));
                log.debug("running sql {}", sql);
                resultSet = statement.executeQuery(sql);
            } catch (SQLException | ClassNotFoundException e) {
                log.error("插件初始化异常!!!", e);
                throw HelperException.newException(CommonError.PLUGIN_INIT_ERROR,
                        String.format("初始化插件失败，目标地址:[%s]，用户名：[%s],密码：[%s]",
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
        }

        @Override
        public List<JsonObject> split(int channel) {
            if (channel > 1) {
                log.info("开始对任务进行分片...");
                List<Integer> splitPrimary = SqlUtil.splitPrimary(getAllConfig(), channel);
                List<String> splitWhere = SqlUtil.splitWhere(getAllConfig(), splitPrimary);

                Assert.isTrue(splitWhere.size() == channel);

                List<JsonObject> configurations = new ArrayList<>();
                for (int i = 0; i < channel; i++) {
                    JsonObject clonedConfig = this.getAllConfig().clone();
                    clonedConfig.set(Keys.QUERY_SQL, splitWhere.get(i));
                    configurations.add(clonedConfig);
                }
                return configurations;
            } else {
                return Collections.singletonList(getAllConfig().clone());
            }
        }


        @Override
        public void destroy() {

        }
    }

    public static class Task extends Reader.Task {
        private static final Logger log = LoggerFactory.getLogger(Task.class);

        @Override
        public void init() {
        }

        @Override
        public void startRead(RecordProducer producer) {
            String username = this.getAllConfig().getString(Keys.USERNAME);
            String jdbcUrl = this.getAllConfig().getString(Keys.JDBC_URL);
            String password = this.getAllConfig().getString(Keys.PASSWORD);
            int loginTimeout = this.getAllConfig().getInt(Keys.LOGIN_TIMEOUT, 120);
            int queryTimeout = getAllConfig().getInt(Keys.QUERY_TIMEOUT, 60);

            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                log.info("设置登录超时时间[{}]秒...", loginTimeout);
                DriverManager.setLoginTimeout(loginTimeout);
                log.info("尝试连接至目标数据源...");
                connection = DriverManager.getConnection(jdbcUrl, username, password);
                connection.setAutoCommit(false);

                log.info("构建查询Sql...");
                String sql = SqlUtil.buildQuerySql(getAllConfig());
                log.info(sql);

                statement = connection.prepareStatement(sql,
                        ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                statement.setFetchSize(Integer.MIN_VALUE);
                log.info("设置查询超时时间[{}]秒...", queryTimeout);
                statement.setQueryTimeout(queryTimeout);

                resultSet = statement.executeQuery();
                ResultSetMetaData metaData = resultSet.getMetaData();
                while (resultSet.next()) {
                    Record record = new DataRecord();
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        switch (metaData.getColumnType(i)) {
                            case Types.CHAR:
                            case Types.NCHAR:
                            case Types.VARCHAR:
                            case Types.LONGVARCHAR:
                            case Types.NVARCHAR:
                            case Types.LONGNVARCHAR:
                            case Types.CLOB:
                            case Types.NCLOB:
                                record.addColumn(new StringColumn(resultSet.getString(i)));
                                break;
                            case Types.SMALLINT:
                            case Types.TINYINT:
                            case Types.INTEGER:
                            case Types.BIGINT:
                                record.addColumn(new LongColumn(resultSet.getString(i)));
                                break;
                            case Types.NUMERIC:
                            case Types.DECIMAL:
                                record.addColumn(new DoubleColumn(resultSet.getString(i)));
                                break;
                            case Types.FLOAT:
                            case Types.REAL:
                            case Types.DOUBLE:
                                record.addColumn(new DoubleColumn(resultSet.getString(i)));
                                break;
                            case Types.TIME:
                                record.addColumn(new DateColumn(resultSet.getTime(i)));
                                break;

                            // for mysql bug, see http://bugs.mysql.com/bug.php?id=35115
                            case Types.DATE:
                                if ("year".equalsIgnoreCase(metaData.getColumnTypeName(i))) {
                                    record.addColumn(new LongColumn(resultSet.getInt(i)));
                                } else {
                                    record.addColumn(new DateColumn(resultSet.getDate(i)));
                                }
                                break;

                            case Types.TIMESTAMP:
                                record.addColumn(new DateColumn(resultSet.getTimestamp(i)));
                                break;

                            case Types.BINARY:
                            case Types.VARBINARY:
                            case Types.BLOB:
                            case Types.LONGVARBINARY:
                                record.addColumn(new BytesColumn(resultSet.getBytes(i)));
                                break;

                            // warn: bit(1) -> Types.BIT 可使用BoolColumn
                            // warn: bit(>1) -> Types.VARBINARY 可使用BytesColumn
                            case Types.BOOLEAN:
                            case Types.BIT:
                                record.addColumn(new BoolColumn(resultSet.getBoolean(i)));
                                break;

                            case Types.NULL:
                                String stringData = null;
                                if (resultSet.getObject(i) != null) {
                                    stringData = resultSet.getObject(i).toString();
                                }
                                record.addColumn(new StringColumn(stringData));
                                break;
                            default:
                                throw HelperException.newException(PluginError.TASK_UNSUPPORTED_TYPE,
                                        String.format("类型 %s 暂不支持", metaData.getColumnTypeName(i)));
                        }
                    }
                    producer.produce(record);
                }

            } catch (SQLException e) {
                log.error("从目标数据源读取数据失败!!!", e);
                throw HelperException.newException(PluginError.TASK_READ_ERROR, e);
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
        }

        @Override
        public void destroy() {

        }


    }
}