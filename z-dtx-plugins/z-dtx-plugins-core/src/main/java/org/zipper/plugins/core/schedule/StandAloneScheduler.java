package org.zipper.plugins.core.schedule;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zipper.plugins.common.collectors.TaskPluginCollector;
import org.zipper.plugins.common.errors.CommonError;
import org.zipper.plugins.core.container.TaskContainer;
import org.zipper.plugins.core.enums.PluginStatus;
import org.zipper.helper.exception.HelperException;
import org.zipper.helper.util.json.JsonObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 单机容器调度器
 * 内部维护了若干个task容器，进行启动停止操作和异常汇总以及任务消息报告
 *
 * @author zhuxj
 */
public class StandAloneScheduler extends AbstractScheduler {
    private static final Logger logger = LoggerFactory.getLogger(StandAloneScheduler.class);

    private List<TaskContainer> taskContainers;

    @Override
    public void schedule(List<JsonObject> taskConfigs) {
        Validate.notNull(taskConfigs, "scheduler配置不能为空");

        logger.info("汇报任务间隔{}ms", jobSleepIntervalInMillSec);
        try {
            taskContainers = new ArrayList<>(taskConfigs.size());

            for (JsonObject taskConfig : taskConfigs) {
                TaskContainer taskContainer = new TaskContainer(taskConfig);
                taskContainers.add(taskContainer);
                taskContainer.start();
            }
            collector.setStartLocalDateTime(LocalDateTime.now());

            List<TaskPluginCollector> list = new ArrayList<>();
            for (TaskContainer container : taskContainers) {
                list.add((TaskPluginCollector) container.getPluginCollector());
            }
            collector.setTaskPluginCollectors(list);


            while (true) {

                report();
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
                if (hasError || finished == taskContainers.size()) {
                    break;
                }

                Thread.sleep(jobSleepIntervalInMillSec);
            }
            collector.setEndLocalDateTime(LocalDateTime.now());
            collector.finalReport();
        } catch (InterruptedException e) {
            logger.error("捕获到InterruptedException异常!", e);
            throw HelperException.newException(CommonError.RUNTIME_ERROR, e);
        }
    }

    @Override
    public void report() {
        this.collector.cycleReport();
    }
}
