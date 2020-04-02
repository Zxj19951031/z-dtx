package org.zipper.dtx.core;

import org.zipper.common.json.Configuration;
import org.zipper.dtx.common.spi.columns.ColumnCast;
import org.zipper.dtx.common.spi.commons.CoreConstant;
import org.zipper.dtx.common.spi.commons.VMInfo;
import org.zipper.dtx.common.spi.records.DataRecord;
import org.zipper.dtx.common.spi.records.Record;
import org.zipper.dtx.core.container.AbstractContainer;
import org.zipper.dtx.core.container.JobContainer;
import org.zipper.dtx.core.utils.LoadUtil;
import cn.hutool.core.io.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
    public void entry() throws IOException {
        String jobId = allConfig.getString(CoreConstant.JOB_ID, "undefined");
        allConfig.set(CoreConstant.JOB_ID, jobId);


        Thread.currentThread().setName("job-" + jobId);
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

    public void start() throws IOException {

        ColumnCast.bind(allConfig);

        LoadUtil.bind();

        AbstractContainer container = new JobContainer(allConfig);

        container.start();

    }

    public static void main(String[] args) {

        try {
            String a = FileUtil.readString("/Users/zhuxj/IdeaProjects/z-dtx/allConfig.json", "utf-8");

            Engine engine = new Engine(Configuration.from(a));
            engine.entry();
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }

    }
}
