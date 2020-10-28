package org.zipper.helper.data.transport.plugins.mysqlwriter;

import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zipper.helper.data.transport.common.Writer;
import org.zipper.helper.data.transport.common.columns.Column;
import org.zipper.helper.data.transport.common.errors.CommonError;
import org.zipper.helper.data.transport.common.errors.PluginError;
import org.zipper.helper.data.transport.common.records.Record;
import org.zipper.helper.data.transport.common.records.SkipRecord;
import org.zipper.helper.data.transport.common.tunnels.RecordConsumer;
import org.zipper.helper.exception.HelperException;
import org.zipper.helper.util.json.JsonObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * mysql 写插件
 *
 * @author zhuxj
 */
public class MysqlWriter extends Writer {

    public static class Job extends Writer.Job {

        private static final Logger log = LoggerFactory.getLogger(MysqlWriter.Job.class);

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
                Class.forName("com.mysql.cj.jdbc.Driver");
                log.info("设置登录超时时间[{}]秒...", loginTimeout);
                DriverManager.setLoginTimeout(loginTimeout);
                log.info("尝试连接至目标数据源...");
                connection = DriverManager.getConnection(jdbcUrl, username, password);

                statement = connection.createStatement();
                log.info("设置查询超时时间[{}]秒...", queryTimeout);
                statement.setQueryTimeout(queryTimeout);

                log.info("校验目标读取字段及表存在性...");
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
            List<JsonObject> configurations = new ArrayList<>();
            for (int i = 0; i < channel; i++) {
                configurations.add(this.getAllConfig().clone());
            }
            return configurations;
        }

        @Override
        public void destroy() {
        }
    }

    public static class Task extends Writer.Task {
        private static final Logger log = LoggerFactory.getLogger(MysqlWriter.Task.class);

        private Triple<List<String>, List<Integer>, List<String>> resultSetMetaData;
        private int columnNumber = 0;
        private String sql;

        @Override
        public void init() {
            log.info("统计计划写入目标数据源字段数...");
            columnNumber = this.getAllConfig().getList(Keys.COLUMNS).size();
            log.info("columnNumber:[{}]", columnNumber);

            log.info("构建插入Sql...");
            sql = SqlUtil.buildInsertSql(getAllConfig());
            log.info(sql);
        }

        @Override
        public void startWrite(RecordConsumer consumer) {
            String jdbcUrl = this.getAllConfig().getString(Keys.JDBC_URL);
            String username = this.getAllConfig().getString(Keys.USERNAME);
            String password = this.getAllConfig().getString(Keys.PASSWORD);
            int bufferSize = this.getAllConfig().getInt(Keys.BUFFER_SIZE, 1000);
            int loginTimeout = this.getAllConfig().getInt(Keys.LOGIN_TIMEOUT, 120);

            log.info("设置缓冲区大小，单次最大提交[{}]条记录...", bufferSize);
            List<Record> buffer = new ArrayList<>(bufferSize);

            Connection connection = null;
            try {
                log.info("设置登录超时时间[{}]秒...", loginTimeout);
                DriverManager.setLoginTimeout(loginTimeout);
                connection = DriverManager.getConnection(jdbcUrl, username, password);
                connection.setAutoCommit(false);

                resultSetMetaData = SqlUtil.getColumnMetaData(connection, getAllConfig());

                Record record = null;
                while ((record = consumer.consume()) != null) {
                    if (record instanceof SkipRecord) {
                        continue;
                    }
                    buffer.add(record);
                    if (buffer.size() >= bufferSize) {
                        doBatchInsert(connection, buffer);
                        buffer.clear();
                    }
                }
                if (buffer.size() > 0) {
                    doBatchInsert(connection, buffer);
                }
            } catch (SQLException e) {
                log.error("向目标数据源插入数据异常", e);
                throw HelperException.newException(PluginError.TASK_READ_ERROR, e);
            } finally {
                try {
                    if (null != connection) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    log.error("关闭数据源连接失败!!!", e);
                }
            }
        }

        @Override
        public void destroy() {

        }

        private void doBatchInsert(Connection connection, List<Record> buffer) throws SQLException {

            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement(sql);

                for (Record record : buffer) {
                    fillPreparedStatement(preparedStatement, record);
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                log.error("向目标数据源批量插入数据异常，尝试回滚并单条插入...", e);
                connection.rollback();
                this.getTaskPluginCollector().decrWriteCnt(buffer.size());
                doOneInsert(connection, buffer);
            } finally {
                if (preparedStatement != null) {
                    preparedStatement.clearParameters();
                    preparedStatement.close();
                }
            }
        }

        private void fillPreparedStatement(PreparedStatement preparedStatement, Record record) throws SQLException {
            for (int i = 0; i < this.columnNumber; i++) {
                int columnSqlType = this.resultSetMetaData.getMiddle().get(i);
                fillPreparedStatementColumnType(preparedStatement, i, columnSqlType, record.getColumn(i));
            }

        }

        private void fillPreparedStatementColumnType(PreparedStatement preparedStatement, int columnIndex, int columnSqltype, Column column) throws SQLException {
            java.util.Date utilDate;
            switch (columnSqltype) {
                case Types.CHAR:
                case Types.NCHAR:
                case Types.CLOB:
                case Types.NCLOB:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                case Types.NVARCHAR:
                case Types.LONGNVARCHAR:
                    preparedStatement.setString(columnIndex + 1, column.asString());
                    break;

                case Types.SMALLINT:
                case Types.INTEGER:
                case Types.BIGINT:
                case Types.NUMERIC:
                case Types.DECIMAL:
                case Types.FLOAT:
                case Types.REAL:
                case Types.DOUBLE:
                    String strValue = column.asString();

                    preparedStatement.setString(columnIndex + 1, strValue);
                    break;

                //tinyint is a little special in some database like mysql {boolean->tinyint(1)}
                case Types.TINYINT:
                    Long longValue = column.asLong();
                    if (null == longValue) {
                        preparedStatement.setString(columnIndex + 1, null);
                    } else {
                        preparedStatement.setString(columnIndex + 1, longValue.toString());
                    }
                    break;

                // for mysql bug, see http://bugs.mysql.com/bug.php?id=35115
                case Types.DATE:
                    if ("year".equalsIgnoreCase(this.resultSetMetaData.getRight().get(columnIndex))) {
                        if (column.asBigInteger() == null) {
                            preparedStatement.setString(columnIndex + 1, null);
                        } else {
                            preparedStatement.setInt(columnIndex + 1, column.asBigInteger().intValue());
                        }
                    } else {
                        Date sqlDate = null;
                        try {
                            utilDate = column.asDate();
                        } catch (HelperException e) {
                            throw new SQLException(String.format(
                                    "Date 类型转换错误：[%s]", column));
                        }

                        if (null != utilDate) {
                            sqlDate = new Date(utilDate.getTime());
                        }
                        preparedStatement.setDate(columnIndex + 1, sqlDate);
                    }
                    break;

                case Types.TIME:
                    Time sqlTime = null;
                    try {
                        utilDate = column.asDate();
                    } catch (HelperException e) {
                        throw new SQLException(String.format("TIME 类型转换错误：[%s]", column));
                    }

                    if (null != utilDate) {
                        sqlTime = new Time(utilDate.getTime());
                    }
                    preparedStatement.setTime(columnIndex + 1, sqlTime);
                    break;

                case Types.TIMESTAMP:
                    Timestamp sqlTimestamp = null;
                    try {
                        utilDate = column.asDate();
                    } catch (HelperException e) {
                        throw new SQLException(String.format(
                                "TIMESTAMP 类型转换错误：[%s]", column));
                    }

                    if (null != utilDate) {
                        sqlTimestamp = new Timestamp(
                                utilDate.getTime());
                    }
                    preparedStatement.setTimestamp(columnIndex + 1, sqlTimestamp);
                    break;

                case Types.BINARY:
                case Types.VARBINARY:
                case Types.BLOB:
                case Types.LONGVARBINARY:
                    preparedStatement.setBytes(columnIndex + 1, column
                            .asBytes());
                    break;

                case Types.BOOLEAN:
                    preparedStatement.setString(columnIndex + 1, column.asString());
                    break;

                // warn: bit(1) -> Types.BIT 可使用setBoolean
                // warn: bit(>1) -> Types.VARBINARY 可使用setBytes
                case Types.BIT:
                    preparedStatement.setBoolean(columnIndex + 1, column.asBoolean());
                    break;
                default:
                    throw HelperException.newException(
                            PluginError.TASK_UNSUPPORTED_TYPE,
                            String.format(
                                    "您的配置文件中的列配置信息有误. 不支持数据库写入这种字段类型. 字段名:[%s], 字段类型:[%d], 字段Java类型:[%s]. 请修改表中该字段的类型或者不同步该字段.",
                                    this.resultSetMetaData.getLeft()
                                            .get(columnIndex),
                                    this.resultSetMetaData.getMiddle()
                                            .get(columnIndex),
                                    this.resultSetMetaData.getRight()
                                            .get(columnIndex)));
            }
        }

        private void doOneInsert(Connection connection, List<Record> buffer) throws SQLException {
            PreparedStatement preparedStatement = null;
            try {
                connection.setAutoCommit(true);
                preparedStatement = connection.prepareStatement(this.sql);

                for (Record record : buffer) {
                    try {
                        fillPreparedStatement(preparedStatement, record);
                        preparedStatement.execute();
                        this.getTaskPluginCollector().incrWriteCnt(1);
                    } catch (SQLException e) {
                        log.warn("将缓冲区记录插入至目标数据源失败，跳过该条记录[{}]，错误原因[{}]", record.toString(), e.getMessage());
                        this.getTaskPluginCollector().incrErrorCnt(1);
                    }
                }
            } catch (Exception e) {
                log.error("向目标数据源插入记录异常!!!", e);
                throw HelperException.newException(PluginError.TASK_WRITE_ERROR);
            } finally {
                if (null != preparedStatement) {
                    preparedStatement.clearParameters();
                    preparedStatement.close();
                }
            }
        }

    }
}
