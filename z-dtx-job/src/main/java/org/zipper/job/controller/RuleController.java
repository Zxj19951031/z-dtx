package org.zipper.job.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zipper.helper.web.response.ResponseEntity;
import org.zipper.job.pojo.dto.RuleDTO;
import org.zipper.job.pojo.dto.RuleQueryParams;
import org.zipper.job.pojo.entity.Rule;
import org.zipper.job.pojo.vo.RuleVO;
import org.zipper.job.service.IRuleService;

import java.util.List;

/**
 * @author zhuxj
 */
@RestController
@RequestMapping("rule")
public class RuleController {

    @Autowired
    private IRuleService ruleService;

    @PostMapping("add")
    public ResponseEntity<Integer> addOne(@RequestBody RuleDTO dto) {
        int id = this.ruleService.addOne(dto);
        return ResponseEntity.success(id);
    }

    @GetMapping("listPage")
    public ResponseEntity<PageInfo<RuleVO>> listPage(@RequestParam(required = false) String name,
                                                     @RequestParam(defaultValue = "0") Integer pageNum,
                                                     @RequestParam(defaultValue = "20") Integer pageSize) {
        RuleQueryParams params = RuleQueryParams.builder().name(name).build();
        PageInfo<RuleVO> list = this.ruleService.listPage(params, pageSize, pageNum);
        return ResponseEntity.success(list);
    }

    @GetMapping("find")
    public ResponseEntity<Rule> findOne(@RequestParam Long id) {
        Rule rule = this.ruleService.queryOne(id);
        return ResponseEntity.success(rule);
    }

    @PostMapping("modify")
    public ResponseEntity<Boolean> updateOne(@RequestBody RuleDTO dto) {
        boolean result = this.ruleService.updateOne(dto);
        return ResponseEntity.success(result);
    }

    @PostMapping("deleteBatch")
    public ResponseEntity<Boolean> deleteBatch(@RequestBody List<Integer> ids) {
        boolean result = this.ruleService.deleteBatch(ids);
        return ResponseEntity.success(result);
    }

    @GetMapping("nextFires")
    public ResponseEntity<List<String>> nextTenFireTimes(@RequestParam String cron) {
        List<String> result = this.ruleService.getNextTenFireTimes(cron);
        return ResponseEntity.success(result);
    }
}
