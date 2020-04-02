package cn.com.citydo.plugin.drdswriter;

import cn.com.citydo.common.exceptions.SysException;
import cn.com.citydo.common.json.Configuration;
import cn.com.citydo.dtx.common.spi.Writer;
import cn.com.citydo.dtx.common.spi.columns.Column;
import cn.com.citydo.dtx.common.spi.errors.CommonError;
import cn.com.citydo.dtx.common.spi.errors.PluginError;
import cn.com.citydo.dtx.common.spi.records.Record;
import cn.com.citydo.dtx.common.spi.records.SkipRecord;
import cn.com.citydo.dtx.common.spi.tunnels.RecordConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DrdsWriter extends Writer {

    public static class Job extends Writer.Job {
        @Override
        public void init() {
            String jdbcUrl = this.getAllConfig().getString(Keys.JDBC_URL);
            String username = this.getAllConfig().getString(Keys.USERNAME);
            String password = this.getAllConfig().getString(Keys.PASSWORD);

            Connection connection = null;
            Statement statement = null;
            ResultSet resultSet = null;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                DriverManager.setLoginTimeout(120);
                connection = DriverManager.getConnection(jdbcUrl, username, password);
                log.info("校验数据源连通性ok");

                statement = connection.createStatement();
                statement.setQueryTimeout(60);
                String sql = String.format("select * from (%s) as tmp where 1=0", SqlUtil.buildQuerySql(getAllConfig()));
                log.debug("校验字段及表: " + sql);

                resultSet = statement.executeQuery(sql);
                log.info("校验查询ok");


            } catch (SQLException | ClassNotFoundException e) {
                log.error(e.getMessage(), e);
                throw SysException.newException(CommonError.PLUGIN_INIT_ERROR,
                        String.format("初始化插件失败，目标地址:[%s]，用户名：[%s],密码：[%s]",
                                jdbcUrl, username, password));
            } finally {
                try {
                    if (resultSet != null)
                        resultSet.close();
                    if (statement != null)
                        statement.close();
                    if (connection != null)
                        connection.close();
                } catch (SQLException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        @Override
        public List<Configuration> split(int channel) {
            List<Configuration> configurations = new ArrayList<>();
            for (int i = 0; i < channel; i++) {
                configurations.add(this.getAllConfig().clone());
            }
            return configurations;
        }

        @Override
        public void destroy() {
            log.info("destroy");
        }
    }

    public static class Task extends Writer.Task {

        private Triple<List<String>, List<Integer>, List<String>> resultSetMetaData;
        private int columnNumber = 0;
        private String sql;

        @Override
        public void init() {
            columnNumber = this.getAllConfig().getList(Keys.COLUMNS).size();
            sql = SqlUtil.buildInsertSql(getAllConfig());
        }

        @Override
        public void startWrite(RecordConsumer consumer) {
            String jdbcUrl = this.getAllConfig().getString(Keys.JDBC_URL);
            String username = this.getAllConfig().getString(Keys.USERNAME);
            String password = this.getAllConfig().getString(Keys.PASSWORD);
            int bufferSize = this.getAllConfig().getInt(Keys.BUFFER_SIZE, 1000);
            List<Record> buffer = new ArrayList<>(bufferSize);

            Connection connection = null;
            try {
                DriverManager.setLoginTimeout(120);
                connection = DriverManager.getConnection(jdbcUrl, username, password);
                connection.setAutoCommit(false);

                resultSetMetaData = SqlUtil.getColumnMetaData(connection, getAllConfig());

                Record record = null;
                while ((record = consumer.consume()) != null) {
                    if (record instanceof SkipRecord)
                        continue;
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
                log.error(e.getMessage(), e);
                throw SysException.newException(PluginError.TASK_READ_ERROR, e);
            } finally {
                try {
                    if (connection != null)
                        connection.close();
                } catch (SQLException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        @Override
        public void destroy() {

        }

        private void doBatchInsert(Connection connection, List<Record> buffer) throws SQLException {
            log.info("待执行的新增Sql：{}", sql);

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
                connection.rollback();
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
                        } catch (SysException e) {
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
                    } catch (SysException e) {
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
                    } catch (SysException e) {
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
                    throw SysException.newException(
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
                preparedStatement = connection
                        .prepareStatement(this.sql);

                for (Record record : buffer) {
                    try {
                        fillPreparedStatement(preparedStatement, record);
                        preparedStatement.execute();
                    } catch (SQLException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw SysException.newException(PluginError.TASK_WRITE_ERROR);
            } finally {
                if (preparedStatement != null) {
                    preparedStatement.clearParameters();
                    preparedStatement.close();
                }
            }
        }

    }
}
