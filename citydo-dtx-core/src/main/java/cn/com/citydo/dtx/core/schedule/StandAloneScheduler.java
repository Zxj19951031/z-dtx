package cn.com.citydo.dtx.core.schedule;

import cn.com.citydo.consts.exceptions.SysException;
import cn.com.citydo.consts.json.Configuration;
import cn.com.citydo.dtx.common.spi.errors.CommonError;
import cn.com.citydo.dtx.core.container.TaskContainer;
import cn.com.citydo.dtx.core.enums.PluginStatus;
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
        int jobSleepIntervalInMillSec = 3 * 1000;

        try {
            taskContainers = new ArrayList<>(taskConfigs.size());

            for (Configuration taskConfig : taskConfigs) {
                TaskContainer taskContainer = new TaskContainer(taskConfig);
                taskContainers.add(taskContainer);
                taskContainer.start();
            }

            while (true) {

                // TODO: 2020/2/14 汇报
                int finished = 0;
                boolean hasError = false;
                for (TaskContainer taskContainer : taskContainers) {
                    if (taskContainer.getStatus() == PluginStatus.ERROR) {
                        logger.error("读写任务组存在异常");
                        hasError = true;
                        break;
                    }
                    if (taskContainer.getStatus() == PluginStatus.FINISH) {
                        finished++;
                        logger.info("累计完成读写任务共{}组", finished);
                    }
                }
                if (hasError || finished == taskContainers.size())
                    break;

                Thread.sleep(jobSleepIntervalInMillSec);
            }
        } catch (InterruptedException e) {
            logger.error("捕获到InterruptedException异常!", e);
            throw SysException.newException(CommonError.RUNTIME_ERROR, e);
        }
    }


}
