package org.zipper.transport.utils;

import org.apache.commons.lang3.StringUtils;
import org.zipper.helper.util.json.JsonObject;
import org.zipper.transport.pojo.entity.MySqlDb;
import org.zipper.transport.pojo.entity.OracleDb;

/**
 * 传输任务配置操作
 *
 * @author zhuxj
 * @since 2020/08/28
 */
public class ConfigUtil {


    public static JsonObject removeBlank(JsonObject configJson) {
        //剔除掉所有空白字符串
        configJson.getKeys().forEach(key -> {
            if (StringUtils.isBlank(configJson.getString(key))) {
                configJson.remove(key);
            }
        });
        return configJson;
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
    public static String supplementMySqlReader(String config, MySqlDb mySqlDb) {
        JsonObject configJson = JsonObject.from(config);
        configJson.set("content.reader.parameter.username", mySqlDb.getUser());
        configJson.set("content.reader.parameter.password", mySqlDb.getPassword());
        configJson.set("content.reader.parameter.jdbcUrl", ConnectionUtil.getJdbcUrl(mySqlDb));
        return removeBlank(configJson).toJSON();
    }

    /**
     * 补全MySql写入配置
     * 补充jdbcUrl
     * 补充username
     * 补充password
     *
     * @param config 全局配置
     * @return 补充后的配置
     */
    public static String supplementMySqlWriter(String config, MySqlDb mySqlDb) {
        JsonObject configJson = JsonObject.from(config);
        configJson.set("content.writer.parameter.username", mySqlDb.getUser());
        configJson.set("content.writer.parameter.password", mySqlDb.getPassword());
        configJson.set("content.writer.parameter.jdbcUrl", ConnectionUtil.getJdbcUrl(mySqlDb));
        return removeBlank(configJson).toJSON();
    }


    /**
     * 补全Oracle写入配置
     * 补充jdbcUrl
     * 补充username
     * 补充password
     *
     * @param config 全局配置
     * @return 补充后的配置
     */
    public static String supplementOracleReader(String config, OracleDb oracleDb) {
        JsonObject configJson = JsonObject.from(config);
        configJson.set("content.reader.parameter.username", oracleDb.getUser());
        configJson.set("content.reader.parameter.password", oracleDb.getPassword());
        configJson.set("content.reader.parameter.jdbcUrl", ConnectionUtil.getJdbcUrl(oracleDb));
        return removeBlank(configJson).toJSON();
    }

    /**
     * 补全Oracle写入配置
     * 补充jdbcUrl
     * 补充username
     * 补充password
     *
     * @param config 全局配置
     * @return 补充后的配置
     */
    public static String supplementOracleWriter(String config, OracleDb oracleDb) {
        JsonObject configJson = JsonObject.from(config);
        configJson.set("content.writer.parameter.username", oracleDb.getUser());
        configJson.set("content.writer.parameter.password", oracleDb.getPassword());
        configJson.set("content.writer.parameter.jdbcUrl", ConnectionUtil.getJdbcUrl(oracleDb));
        return removeBlank(configJson).toJSON();
    }

}
