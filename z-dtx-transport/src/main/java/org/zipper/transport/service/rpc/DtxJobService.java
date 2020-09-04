package org.zipper.transport.service.rpc;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.zipper.helper.web.response.ResponseEntity;

@FeignClient(value = "z-dtx-job-service")
public interface DtxJobService {

    @GetMapping(value = "job/register")
    ResponseEntity registerJob(@RequestParam Long ruleId, @RequestParam Long transportId);

    @GetMapping(value = "job/cancel")
    ResponseEntity cancelJob(@RequestParam Long transportId);

}
