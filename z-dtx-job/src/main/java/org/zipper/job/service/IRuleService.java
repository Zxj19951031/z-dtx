package org.zipper.job.service;

import com.github.pagehelper.PageInfo;
import org.zipper.job.pojo.dto.RuleDTO;
import org.zipper.job.pojo.dto.RuleQueryParams;
import org.zipper.job.pojo.entity.Rule;
import org.zipper.job.pojo.vo.RuleVO;

import java.util.List;

public interface IRuleService {
    int addOne(RuleDTO dto);

    PageInfo<RuleVO> listPage(RuleQueryParams params, int pageSize, int pageNum);

    Rule queryOne(Integer id);

    boolean updateOne(RuleDTO dto);

    boolean deleteBatch(List<Integer> ids);

    /**
     * 获取近十次调度触发时间
     *
     * @param cron 表达式
     * @return list
     */
    List<String> getNextTenFireTimes(String cron);
}
