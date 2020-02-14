package cn.com.citydo.dtx.core;

import cn.com.citydo.consts.json.Configuration;
import cn.com.citydo.dtx.common.spi.columns.ColumnCast;
import cn.com.citydo.dtx.common.spi.commons.CoreConstant;
import cn.com.citydo.dtx.common.spi.commons.VMInfo;
import cn.com.citydo.dtx.core.container.AbstractContainer;
import cn.com.citydo.dtx.core.container.JobContainer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class Engine {

    private static Logger logger = LoggerFactory.getLogger(Engine.class);
    private Configuration allConfig;

    public Engine(Configuration allConfig) {
        this.allConfig = allConfig;
    }

    /**
     * 交换入口
     */
    public void entry() {
        String jobId = allConfig.getString(CoreConstant.JOB_ID, "undefined");
        allConfig.set(CoreConstant.JOB_ID, jobId);

        //打印vmInfo
        VMInfo vmInfo = VMInfo.getVmInfo();
        if (vmInfo != null) {
            logger.info(vmInfo.toString());
        }

        logger.info("\n" + Engine.filterJobConfiguration(allConfig) + "\n");

        logger.debug(allConfig.toJSON());

        start();
    }

    // 注意屏蔽敏感信息
    public static String filterJobConfiguration(final Configuration configuration) {
        Configuration jobConfWithSetting = configuration.getConfiguration("job").clone();

        Configuration jobContent = jobConfWithSetting.getConfiguration("content");

        filterSensitiveConfiguration(jobContent);

        jobConfWithSetting.set("content", jobContent);

        return jobConfWithSetting.beautify();
    }

    public static void filterSensitiveConfiguration(Configuration configuration) {
        Set<String> keys = configuration.getKeys();
        for (final String key : keys) {
            boolean isSensitive = StringUtils.endsWithIgnoreCase(key, "password")
                    || StringUtils.endsWithIgnoreCase(key, "accessKey");
            if (isSensitive && configuration.get(key) instanceof String) {
                configuration.set(key, configuration.getString(key).replaceAll(".", "*"));
            }
        }
    }

    public void start() {

        ColumnCast.bind(allConfig);

        AbstractContainer container = new JobContainer(allConfig);

        container.start();

    }
}
