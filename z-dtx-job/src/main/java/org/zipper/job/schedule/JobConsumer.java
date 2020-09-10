package org.zipper.job.schedule;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.zipper.helper.web.response.ResponseEntity;
import org.zipper.job.service.rpc.TransportService;

/**
 * 调度任务消费逻辑
 *
 * @author zhuxj
 * @since 2020/08/28
 */
@Slf4j
public class JobConsumer implements InterruptableJob {

    private TransportService transportService;

    @Override
    public void interrupt() throws UnableToInterruptJobException {

    }

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        try {
            transportService = (TransportService) ctx.getScheduler().getContext().get("transportService");
            Long transportId = ((Long) ctx.getJobDetail().getJobDataMap().get("transportId"));
            ResponseEntity resp = transportService.runJob(transportId.intValue());
            log.info(resp.toString());
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
