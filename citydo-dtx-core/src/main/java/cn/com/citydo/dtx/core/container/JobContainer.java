package cn.com.citydo.dtx.core.container;

import cn.com.citydo.dtx.core.collectors.JobPluginCollector;
import cn.com.citydo.consts.json.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobContainer extends AbstractContainer {

    private static Logger logger = LoggerFactory.getLogger(JobContainer.class);

    private long startTimeStamp;

    public JobContainer(Configuration allConfig) {
        super(allConfig);
        this.pluginCollector = new JobPluginCollector();
    }

    @Override
    public void start() {
        logger.info("start job container");

        boolean hasException = false;

        try {
            this.startTimeStamp = System.currentTimeMillis();
            logger.debug("jobContainer starts to do init ...");

        } catch (Throwable e) {

        } finally {

        }
    }

    private void init() {

    }

}
