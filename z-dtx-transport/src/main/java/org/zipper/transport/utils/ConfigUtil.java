package org.zipper.transport.utils;

import org.zipper.helper.util.json.JsonObject;
import org.zipper.transport.pojo.entity.MySqlDb;

/**
 * 传输任务配置操作
 *
 * @author zhuxj
 * @since 2020/08/28
 */
public class ConfigUtil {

    /**
     * 补全MySql读取配置
     * 补充jdbcUrl
     * 补充username
     * 补充password
     *
     * @param config 全局配置
     * @return 补充后的配置
     */
    public static String supplementMySqlReader(String config, MySqlDb mySqlDb) {
        JsonObject configJson = JsonObject.from(config);
        configJson.set("content.reader.parameter.username",mySqlDb.getUser());
        configJson.set("content.reader.parameter.password",mySqlDb.getPassword());
        configJson.set("content.reader.parameter.jdbcUrl",ConnectionUtil.getJdbcUrl(mySqlDb));
        return configJson.toJSON();
    }

    /**
     * 补全MySql读取配置
     * 补充jdbcUrl
     * 补充username
     * 补充password
     *
     * @param config 全局配置
     * @return 补充后的配置
     */
    public static String supplementMySqlWriter(String config, MySqlDb mySqlDb) {
        JsonObject configJson = JsonObject.from(config);
        configJson.set("content.writer.parameter.username",mySqlDb.getUser());
        configJson.set("content.writer.parameter.password",mySqlDb.getPassword());
        configJson.set("content.writer.parameter.jdbcUrl",ConnectionUtil.getJdbcUrl(mySqlDb));
        return configJson.toJSON();
    }
}
