package org.zipper.plugins.oraclereader;

/**
 * @author zhuxj
 * @since 2020/08/03
 */
public class Keys {
    /**
     * jdbc 连接地址
     */
    public static final String JDBC_URL = "jdbcUrl";
    /**
     * 用户名
     */
    public static final String USERNAME = "username";
    /**
     * 密码
     */
    public static final String PASSWORD = "password";
    /**
     * 读取表名称，设置了querySql会忽略该配置
     */
    public static final String TABLE = "table";
    /**
     * 读取列表，设置了querySql会忽略该配置
     */
    public static final String COLUMNS = "columns";
    /**
     * 数据库查询超时时间
     */
    public static final String QUERY_TIMEOUT = "queryTimeout";
    /**
     * 自定义查询sql
     */
    public static final String QUERY_SQL = "querySql";
    /**
     * 查询条件，设置了querySql会忽略该配置
     */
    public static final String WHERE = "where";
    /**
     * 数据库登录超时时间
     */
    public static final String LOGIN_TIMEOUT = "loginTimeout";
    /**
     * 主键字段，任务切片时会用依据该字段进行切分
     */
    public static final String PRIMARY_KEY = "primaryKey";
}
