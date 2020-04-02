package org.zipper.plugin.drdsreader;

import org.zipper.common.json.Configuration;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Slf4j
public class SqlUtil {

    public static String buildQuerySql(Configuration config) {
        String format = "select %s from %s where %s";
        String querySql = config.getString(Keys.QUERY_SQL);

        if (StringUtils.isNotBlank(querySql)) {
            log.info("监测到配置了querySql参数");
            return querySql;
        } else {
            return String.format(format, getColumns(config), getTableName(config), getWhere(config));
        }
    }

    private static String getTableName(Configuration config) {
        return config.getString(Keys.TABLE);
    }

    private static String getColumns(Configuration config) {
        List<String> columns = config.getList(Keys.COLUMNS, String.class);
        return CollectionUtil.join(columns, ",");
    }

    private static String getWhere(Configuration config) {
        return config.getString(Keys.WHERE, "1=1");
    }
}
