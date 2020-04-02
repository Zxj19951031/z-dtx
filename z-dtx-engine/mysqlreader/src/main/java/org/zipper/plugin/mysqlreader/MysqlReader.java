package org.zipper.plugin.mysqlreader;

import org.zipper.common.exceptions.SysException;
import org.zipper.common.json.Configuration;
import org.zipper.dtx.common.spi.Reader;
import org.zipper.dtx.common.spi.columns.*;
import org.zipper.dtx.common.spi.errors.CommonError;
import org.zipper.dtx.common.spi.errors.PluginError;
import org.zipper.dtx.common.spi.records.DataRecord;
import org.zipper.dtx.common.spi.records.Record;
import org.zipper.dtx.common.spi.tunnels.RecordProducer;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MysqlReader extends Reader {
    public static class Job extends Reader.Job {
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

    public static class Task extends Reader.Task {

        @Override
        public void init() {

        }

        @Override
        public void startRead(RecordProducer producer) {
            String username = this.getAllConfig().getString(Keys.USERNAME);
            String jdbcUrl = this.getAllConfig().getString(Keys.JDBC_URL);
            String password = this.getAllConfig().getString(Keys.PASSWORD);
            int fetchSize = Integer.MIN_VALUE;
            int queryTimeout = getAllConfig().getInt(Keys.QUERY_TIMEOUT, 120);

            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                DriverManager.setLoginTimeout(120);
                connection = DriverManager.getConnection(jdbcUrl, username, password);
                connection.setAutoCommit(false);
                log.info("构建数据库链接ok");

                String sql = SqlUtil.buildQuerySql(getAllConfig());
                log.info("待处理Sql:{}", sql);

                statement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_READ_ONLY);
                statement.setFetchSize(fetchSize);
                statement.setQueryTimeout(queryTimeout);
                log.info("构建预处理ok");

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
                                throw SysException.newException(PluginError.TASK_UNSUPPORTED_TYPE,
                                        String.format("类型 %s 暂不支持", metaData.getColumnTypeName(i)));
                        }
                    }
                    producer.produce(record);
                }

            } catch (SQLException e) {
                log.error(e.getMessage(), e);
                throw SysException.newException(PluginError.TASK_READ_ERROR,e);
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
        public void destroy() {

        }


    }
}