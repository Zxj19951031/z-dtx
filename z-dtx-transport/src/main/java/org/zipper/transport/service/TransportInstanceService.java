package org.zipper.transport.service;

import com.github.pagehelper.PageInfo;
import org.zipper.transport.pojo.entity.TransportInstance;
import org.zipper.transport.pojo.vo.TransportInstanceVO;

import java.util.List;

/**
 * @author zhuxj
 * @since 2020/09/08
 */
public interface TransportInstanceService {
    /**
     * 新增传输实例
     *
     * @param instance 实例
     * @return id of 实例
     */
    long addOne(TransportInstance instance);

    /**
     * 更新传输实例
     *
     * @param instance 实例
     */
    void updateOne(TransportInstance instance);

    /**
     * 查询某个任务的所有实例
     *
     * @param transportId 任务编号
     * @param pageNum     分页参数
     * @param pageSize    分页参数
     * @return list of 实例
     */
    PageInfo<TransportInstanceVO> queryByTransportId(Integer transportId, Integer pageNum, Integer pageSize);

    /**
     * 查询某个实例
     *
     * @param instanceId 实例编号
     * @return 实例
     */
    TransportInstance queryByInstanceId(Integer instanceId);

    /**
     * 分页检索实例日志
     *
     * @param instanceId 实例编号
     * @param pageNum    分页参数
     * @param pageSize   分页参数
     * @return list of 日志
     */
    List<String> queryLog(Integer instanceId, Integer pageNum, Integer pageSize);
}
