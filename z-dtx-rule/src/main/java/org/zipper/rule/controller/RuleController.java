package org.zipper.rule.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zipper.common.response.RespResult;
import org.zipper.rule.pojo.dto.RuleDTO;
import org.zipper.rule.pojo.dto.RuleQueryParams;
import org.zipper.rule.pojo.entity.Rule;
import org.zipper.rule.pojo.vo.RuleVO;
import org.zipper.rule.service.IRuleService;

import java.util.List;

@RestController
@RequestMapping("rule")
public class RuleController {

    @Autowired
    private IRuleService ruleService;

    @PostMapping("add")
    public RespResult<Integer> addOne(@RequestBody RuleDTO dto) {
        int id = this.ruleService.addOne(dto);
        return RespResult.success(id);
    }

    @GetMapping("listPage")
    public RespResult<PageInfo<RuleVO>> listPage(@RequestParam(required = false) String name,
                                               @RequestParam(defaultValue = "0") Integer pageNum,
                                               @RequestParam(defaultValue = "20") Integer pageSize) {
        RuleQueryParams params = RuleQueryParams.builder().name(name).build();
        PageInfo<RuleVO> list = this.ruleService.listPage(params, pageSize, pageNum);
        return RespResult.success(list);
    }

    @GetMapping("find")
    public RespResult<Rule> findOne(@RequestParam Integer id) {
        Rule rule = this.ruleService.queryOne(id);
        return RespResult.success(rule);
    }

    @PostMapping("modify")
    public RespResult<Boolean> updateOne(@RequestBody RuleDTO dto) {
        boolean result = this.ruleService.updateOne(dto);
        return RespResult.success(result);
    }

    @PostMapping("deleteBatch")
    public RespResult<Boolean> deleteBatch(@RequestBody List<Integer> ids) {
        boolean result = this.ruleService.deleteBatch(ids);
        return RespResult.success(result);
    }

    @GetMapping("nextFires")
    public RespResult<List<String>> nextTenFireTimes(@RequestParam String cron) {
        List<String> result = this.ruleService.getNextTenFireTimes(cron);
        return RespResult.success(result);
    }
}
