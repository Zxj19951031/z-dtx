package org.zipper.rule.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zipper.common.enums.Status;
import org.zipper.common.exceptions.SysException;
import org.zipper.common.exceptions.errors.QuartzError;
import org.zipper.common.json.Configuration;
import org.zipper.rule.mapper.RuleMapper;
import org.zipper.rule.pojo.dto.RuleDTO;
import org.zipper.rule.pojo.dto.RuleQueryParams;
import org.zipper.rule.pojo.entity.Rule;
import org.zipper.rule.pojo.vo.RuleVO;
import org.zipper.rule.service.IRuleService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class RuleServiceImpl implements IRuleService {

    @Autowired
    private RuleMapper ruleMapper;

    @Override
    public int addOne(RuleDTO dto) {
        Rule rule = Rule.builder().name(dto.getName()).expression(dto.getExpression())
                .var1(dto.getVar1()).build();
        rule.setCreateTime(Calendar.getInstance().getTime());
        rule.setUpdateTime(rule.getCreateTime());
        rule.setStatus(Status.VALID);
        return this.ruleMapper.insertOne(rule);
    }

    @Override
    public PageInfo<RuleVO> listPage(RuleQueryParams params, int pageSize, int pageNum) {

        PageHelper.startPage(pageNum, pageSize);
        List<RuleVO> list = this.ruleMapper.query(params);
        return PageInfo.of(list);
    }

    @Override
    public Rule queryOne(Integer id) {
        return this.ruleMapper.selectOne(id);
    }

    @Override
    public boolean updateOne(RuleDTO dto) {
        int cnt = this.ruleMapper.updateOne(dto);
        return cnt > 0;
    }

    @Override
    public boolean deleteBatch(List<Integer> ids) {
        int cnt = this.ruleMapper.deleteBatch(ids);
        return cnt > 0;
    }

    @Override
    public List<String> getNextTenFireTimes(String cron) {
        List<String> result = null;

        if (!CronExpression.isValidExpression(cron))
            throw SysException.newException(QuartzError.CRON_ERROR);

        Trigger trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        Date offset = Calendar.getInstance().getTime();
        for (int i = 0; i < 10; i++) {
            if (result == null)
                result = new ArrayList<>();
            offset = trigger.getFireTimeAfter(offset);
            result.add(SimpleDateFormat.getDateTimeInstance().format(offset));
        }

        return result;
    }
}
