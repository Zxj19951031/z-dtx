package org.zipper.transport.utils;

import lombok.extern.slf4j.Slf4j;
import org.zipper.helper.exception.HelperException;
import org.zipper.helper.exception.ErrorCode;
import org.zipper.transport.enums.DbType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author zhuxj
 * @since 2020/5/12
 */
@Slf4j
public class ConnectionUtil {

    private static final int LOGIN_TIMEOUT = 10;

    public static void checkMysql(String host, int port, String username, String pwd) {
        Connection conn = getMysqlConnection(host, port, username, pwd);
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.error("A error caused when close connection", e);
                throw HelperException.newException(ErrorCode.CLOSE_ERROR, "关闭数据库连接失败，请及时联系管理员予以排查");
            }
        }
    }

    /**
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
}
