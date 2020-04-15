package org.zipper.job.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zipper.common.response.RespResult;
import org.zipper.dto.DBDTO;

/**
 * @author zhuxj
 * @since 2020/4/8
 */
@RestController
@RequestMapping(value = "job")
public class JobController {

    @PostMapping(value = "connective")
    public RespResult<Boolean> confirmConnective(@RequestBody DBDTO db) {
        System.out.println(JSONObject.toJSONString(db));
        return RespResult.success(Boolean.TRUE);
    }
}
