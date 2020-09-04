package org.zipper.job.schedule;

import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

/**
 * 调度任务消费逻辑
 *
 * @author zhuxj
 * @since 2020/08/28
 */
public class JobConsumer implements InterruptableJob {
    @Override
    public void interrupt() throws UnableToInterruptJobException {

    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        System.out.println("!@#!@#!@#");
    }
}
