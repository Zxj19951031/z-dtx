package cn.com.citydo.dtx.core.schedule;

import cn.com.citydo.consts.exceptions.SysException;
import cn.com.citydo.consts.json.Configuration;
import cn.com.citydo.dtx.common.spi.errors.CommonError;
import cn.com.citydo.dtx.core.container.TaskContainer;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class StandAloneScheduler extends AbstractScheduler {
    private static Logger logger = LoggerFactory.getLogger(StandAloneScheduler.class);

    private List<TaskContainer> taskContainers;

    @Override
    public void schedule(List<Configuration> taskConfigs) {
        Validate.notNull(taskConfigs, "scheduler配置不能为空");

        int jobReportIntervalInMillSec = 30 * 1000;

        try {
            taskContainers = new ArrayList<>(taskConfigs.size());

            for (Configuration taskConfig : taskConfigs) {
                TaskContainer taskContainer = new TaskContainer(taskConfig);
                taskContainers.add(taskContainer);
                taskContainer.start();
            }

            while (true) {


                // TODO: 2020/2/14 汇报间隔

                Thread.sleep(jobReportIntervalInMillSec);
            }
        } catch (InterruptedException e) {
            logger.error("捕获到InterruptedException异常!", e);
            throw SysException.newException(CommonError.RUNTIME_ERROR, e);
        }
    }

}
