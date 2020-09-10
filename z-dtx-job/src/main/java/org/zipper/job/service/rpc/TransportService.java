package org.zipper.job.service.rpc;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.zipper.helper.web.response.ResponseEntity;

/**
 * @author zhuxj
 * @since 2020/08/31
 */
@FeignClient(value = "z-dtx-transport-service")
public interface TransportService {

    @GetMapping(value = "transport/run")
    ResponseEntity runJob(@RequestParam Integer id);
}
