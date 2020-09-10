package org.zipper.job.service.impl;

import org.quartz.CronScheduleBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zipper.helper.quartz.QuartzUtils;
import org.zipper.job.pojo.entity.Rule;
import org.zipper.job.schedule.JobConsumer;
import org.zipper.job.service.IJobService;
import org.zipper.job.service.IRuleService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhuxj
 * @since 2020/08/28
 */
@Service
public class JobServiceImpl implements IJobService {


    private static final String JOB_NAME = "Transport-Job-%s";
    private static final String JOB_GROUP_NAME = "Transport-Job-Group";
    private static final String TRIGGER_NAME = "Transport-Trigger-%s";
    private static final String TRIGGER_GROUP_NAME = "Transport-Trigger-GROUP";


    @Autowired
    private IRuleService ruleService;

    @Override
    public boolean registerJob(Long ruleId, Long transportId) {
        Rule rule = ruleService.queryOne(ruleId);

        Map<String, Object> data = new HashMap<>();
        data.put("transportId", transportId);
        QuartzUtils.addJob(
                String.format(JOB_NAME, transportId), JOB_GROUP_NAME,
                String.format(TRIGGER_NAME, transportId), TRIGGER_GROUP_NAME,
                JobConsumer.class, CronScheduleBuilder.cronSchedule(rule.getExpression()), data);

        return true;
    }

    @Override
    public boolean cancelJob(Long transportId) {
        QuartzUtils.stopJob(String.format(JOB_NAME, transportId), JOB_GROUP_NAME);
        return true;
    }
}
