package org.zipper.transport.utils;

import lombok.extern.slf4j.Slf4j;
import org.zipper.helper.exception.ErrorCode;
import org.zipper.helper.exception.HelperException;
import org.zipper.transport.enums.DbType;
import org.zipper.transport.enums.OracleConnType;
import org.zipper.transport.error.JdbcError;
import org.zipper.transport.pojo.entity.DataBase;
import org.zipper.transport.pojo.entity.MySqlDb;
import org.zipper.transport.pojo.entity.OracleDb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 连接操作
 * <p>
 * mysql：jdbc 连接
 *
 * @author zhuxj
 * @since 2020/5/12
 */
@Slf4j
public class ConnectionUtil {

    /**
     * 登录超时时间（秒）
     */
    private static final int LOGIN_TIMEOUT = 10;

    /**
     * 校验MySql连接连通性
     *
     * @param host     主机地址
     * @param port     端口号
     * @param username 用户名
     * @param pwd      密码
     */
    public static void checkMysql(String host, int port, String username, String pwd) {
        Connection conn = getMysqlConnection(host, port, username, pwd);
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.error("A error caused when close connection", e);
                throw HelperException.newException(ErrorCode.CLOSE_DB_ERROR, "关闭数据库连接失败，请及时联系管理员予以排查");
            }
        }
    }

    /**
     * 测试Oracle连接
     *
     * @param host      主机地址
     * @param port      端口号
     * @param username  用户名
     * @param pwd       密码
     * @param connType  连接类型
     * @param connValue 连接类型值
     * @param driver    驱动类型
     */
    public static void checkOracle(String host, Integer port, String username, String pwd, String connType, String connValue, String driver) {
        Connection conn = getOracleConnection(host, port, username, pwd, connType, connValue, driver);
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.error("A error caused when close connection", e);
                throw HelperException.newException(ErrorCode.CLOSE_DB_ERROR, "关闭数据库连接失败，请及时联系管理员予以排查");
            }
        }
    }


    /**
     * 校验SqlServer连接连通性
     *
     * @param host     主机地址
     * @param port     端口号
     * @param username 用户名
     * @param pwd      密码
     * @param database 数据库
     */
    public static void checkSqlServer(String host, Integer port, String username, String pwd, String database) {
        Connection conn = getSqlServerConnection(host, port, username, pwd, database);
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.error("A error caused when close connection", e);
                throw HelperException.newException(ErrorCode.CLOSE_DB_ERROR, "关闭数据库连接失败，请及时联系管理员予以排查");
            }
        }
    }


    /**
     * 获取数据库连接并不会主动关闭
     * 需要调用方进行关闭
     *
     * @param host     主机地址
     * @param port     端口号
     * @param username 用户名
     * @param pwd      密码
     */
    public static Connection getMysqlConnection(String host, int port, String username, String pwd) {
        try {
            Class.forName(DbType.MySql.className);
            String url = String.format("jdbc:mysql://%s:%s", host, port);
            log.info(String.format("Checking connection for url[%s],username[%s],password[%s]", url, username, pwd));

            DriverManager.setLoginTimeout(LOGIN_TIMEOUT);
            return DriverManager.getConnection(url, username, pwd);
        } catch (ClassNotFoundException e) {
            log.error("Class not found", e);
            throw HelperException.newException(ErrorCode.CLASS_NOT_FOUND,
                    String.format("无法找到目标类%s,请联系管理员检查程序合理性", DbType.MySql.className));
        } catch (SQLException e) {
            log.error("SQL exception", e);
            throw HelperException.newException(ErrorCode.CONNECTION_FAILED,
                    String.format("连接目标数据源时遇到错误%s，请检查连接信息", e.getMessage()));
        }
    }

    /**
     * 获取数据库连接并不会主动关闭
     * 需要调用方进行关闭
     *
     * @param host     主机地址
     * @param port     端口号
     * @param username 用户名
     * @param pwd      密码
     * @param database 数据库
     */
    public static Connection getSqlServerConnection(String host, int port, String username, String pwd, String database) {
        try {
            Class.forName(DbType.SqlServer.className);
            String url = String.format("jdbc:sqlserver://%s:%s;database=%s", host, port, database);
            log.info(String.format("Checking connection for url[%s],username[%s],password[%s]", url, username, pwd));

            DriverManager.setLoginTimeout(LOGIN_TIMEOUT);
            return DriverManager.getConnection(url, username, pwd);
        } catch (ClassNotFoundException e) {
            log.error("Class not found", e);
            throw HelperException.newException(ErrorCode.CLASS_NOT_FOUND,
                    String.format("无法找到目标类%s,请联系管理员检查程序合理性", DbType.MySql.className));
        } catch (SQLException e) {
            log.error("SQL exception", e);
            throw HelperException.newException(ErrorCode.CONNECTION_FAILED,
                    String.format("连接目标数据源时遇到错误%s，请检查连接信息", e.getMessage()));
        }
    }

    /**
     * 获取数据库连接并不会主动关闭
     * 需要调用方进行关闭
     *
     * @param host      主机地址
     * @param port      端口号
     * @param username  用户名
     * @param pwd       密码
     * @param connType  连接类型
     * @param connValue 连接类型值
     * @param driver    驱动类型
     */
    public static Connection getOracleConnection(String host, Integer port, String username, String pwd, String connType, String connValue, String driver) {
        try {
            Class.forName(DbType.Oracle.className);

            String url = "";
            switch (OracleConnType.get(connType)) {
                case SID:
                    url = String.format("jdbc:oracle:%s:@%s:%s:%s", driver.toLowerCase(), host, port, connValue);
                    break;
                case SERVICE_NAME:
                    url = String.format("jdbc:oracle:%s:@//%s:%s/%s", driver.toLowerCase(), host, port, connValue);
                    break;
                case TNS:
                    url = String.format("jdbc:oracle:%s:@%s", driver.toLowerCase(), connValue);
                    break;
                default:
            }

            log.info(String.format("Checking connection for url[%s],username[%s],password[%s]", url, username, pwd));

            DriverManager.setLoginTimeout(LOGIN_TIMEOUT);
            return DriverManager.getConnection(url, username, pwd);
        } catch (ClassNotFoundException e) {
            log.error("Class not found", e);
            throw HelperException.newException(ErrorCode.CLASS_NOT_FOUND,
                    String.format("无法找到目标类%s,请联系管理员检查程序合理性", DbType.MySql.className));
        } catch (SQLException e) {
            log.error("SQL exception", e);
            throw HelperException.newException(ErrorCode.CONNECTION_FAILED,
                    String.format("连接目标数据源时遇到错误%s，请检查连接信息", e.getMessage()));
        }
    }

    /**
     * 获取数据库JdbcUrl
     *
     * @param dataBase implement of {@link DataBase}
     * @return jdbcUrl
     */
    public static String getJdbcUrl(DataBase dataBase) {
        if (dataBase instanceof MySqlDb) {
            return String.format("jdbc:mysql://%s:%s", ((MySqlDb) dataBase).getHost(), ((MySqlDb) dataBase).getPort());
        } else if (dataBase instanceof OracleDb) {
            String url = "";
            switch (OracleConnType.get(((OracleDb) dataBase).getConnType())) {
                case SID:
                    url = String.format("jdbc:oracle:%s:@%s:%s:%s",
                            ((OracleDb) dataBase).getDriver().toLowerCase(),
                            ((OracleDb) dataBase).getHost(),
                            ((OracleDb) dataBase).getPort(),
                            ((OracleDb) dataBase).getConnValue());
                    break;
                case SERVICE_NAME:
                    url = String.format("jdbc:oracle:%s:@//%s:%s/%s",
                            ((OracleDb) dataBase).getDriver().toLowerCase(),
                            ((OracleDb) dataBase).getHost(),
                            ((OracleDb) dataBase).getPort(),
                            ((OracleDb) dataBase).getConnValue());
                    break;
                case TNS:
                    url = String.format("jdbc:oracle:%s:@%s",
                            ((OracleDb) dataBase).getDriver().toLowerCase(),
                            ((OracleDb) dataBase).getConnValue());
                    break;
                default:
            }
            return url;
        } else {
            log.error("不支持的数据源类型{}，无法正确获取JDBC url", dataBase.getClass().getName());
            throw HelperException.newException(JdbcError.UNSUPPORTED_DB_TYPE, "不支持的数据源类型，无法正确获取JDBC url");
        }
    }


}
