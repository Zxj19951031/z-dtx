package org.zipper.job.service;

/**
 * 任务操作相关
 *
 * @author zhuxj
 * @since 2020/08/28
 */
public interface IJobService {

    /**
     * 注册任务
     *
     * @param ruleId      规则编号
     * @param transportId 传输任务编号
     * @return true or false
     */
    public boolean registerJob(Long ruleId, Long transportId);

    /**
     * 注销任务
     *
     * @param transportId 传输任务编号
     * @return true of false
     */
    public boolean cancelJob(Long transportId);
}
