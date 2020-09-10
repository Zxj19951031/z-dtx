package org.zipper.job.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zipper.helper.web.response.ResponseEntity;
import org.zipper.job.service.IJobService;

import javax.annotation.Resource;

/**
 * 调度任务中心
 *
 * @author zhuxj
 * @since 2020/08/28
 */
@RestController
@RequestMapping(value = "job")
public class JobController {

    @Autowired
    private IJobService jobService;

    @GetMapping(value = "register")
    public ResponseEntity<String> registerJob(@RequestParam Integer ruleId,
                                              @RequestParam Integer transportId) {
        boolean result = jobService.registerJob(ruleId.longValue(), transportId.longValue());
        return ResponseEntity.success("注册任务成功");
    }

    @GetMapping(value = "cancel")
    public ResponseEntity<String> cancelJob(@RequestParam Integer transportId) {
        boolean result = jobService.cancelJob(transportId.longValue());
        return ResponseEntity.success("注销任务成功");
    }
}
