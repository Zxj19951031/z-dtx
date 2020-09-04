package org.zipper.transport.service;

import com.github.pagehelper.PageInfo;
import org.zipper.transport.pojo.dto.TransportQueryParams;
import org.zipper.transport.pojo.entity.Transport;
import org.zipper.transport.pojo.vo.TransportVO;

import java.util.List;

/**
 * @author zhuxj
 * @since 2020/08/27
 */
public interface TransportService {
    /**
     * 新增传输任务
     *
     * @param transport
     * @return id
     */
    int addOne(Transport transport);

    /**
     * 条件查询列表
     *
     * @param params 查询条件 {@link TransportQueryParams}
     * @return list of {@link TransportVO}
     */
    List<TransportVO> queryByParams(TransportQueryParams params);

    /**
     * 带分页条件的查询
     *
     * @param params   查询条件 {@link TransportQueryParams}
     * @param pageNum  分页参数-页码
     * @param pageSize 分页参数-单页大小
     * @return PageInfo of {@link TransportVO}
     */
    PageInfo<TransportVO> queryByParams(TransportQueryParams params, Integer pageNum, Integer pageSize);

    /**
     * 查询某个传输任务
     *
     * @param id 任务编号
     * @return {@link Transport}
     */
    Transport queryOne(Integer id);

    /**
     * 更新某个传输任务
     *
     * @param transport 任务详情
     * @return bool
     */
    boolean updateOne(Transport transport);

    /**
     * 异步执行某个传输任务
     *
     * @param id 任务编号
     */
    void run(Integer id);

    /**
     * 将任务注册到调度中心
     * @param id 任务编号
     */
    void registerJob(Integer id);

    /**
     * 将任务从调度中心注销
     * @param id 任务编号
     */
    void cancelJob(Integer id);
}
