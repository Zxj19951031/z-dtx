package org.zipper.job.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zipper.helper.exception.HelperException;
import org.zipper.helper.quartz.QuartzError;
import org.zipper.job.mapper.RuleMapper;
import org.zipper.job.pojo.dto.RuleDTO;
import org.zipper.job.pojo.dto.RuleQueryParams;
import org.zipper.job.pojo.entity.Rule;
import org.zipper.job.pojo.vo.RuleVO;
import org.zipper.job.service.IRuleService;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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
        rule.setCreateTime(LocalDateTime.now());
        rule.setUpdateTime(rule.getCreateTime());
        rule.setStatus(0);
        return this.ruleMapper.insertOne(rule);
    }

    @Override
    public PageInfo<RuleVO> listPage(RuleQueryParams params, int pageSize, int pageNum) {

        PageHelper.startPage(pageNum, pageSize);
        List<RuleVO> list = this.ruleMapper.query(params);
        return PageInfo.of(list);
    }

    @Override
    public Rule queryOne(Long id) {
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

        if (!CronExpression.isValidExpression(cron)) {
            throw HelperException.newException(QuartzError.CRON_ERROR);
        }

        Trigger trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        Date offset = Calendar.getInstance().getTime();
        for (int i = 0; i < 10; i++) {
            if (result == null) {
                result = new ArrayList<>();
            }
            offset = trigger.getFireTimeAfter(offset);
            result.add(SimpleDateFormat.getDateTimeInstance().format(offset));
        }

        return result;
    }
}
