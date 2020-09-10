package org.zipper.transport.mapper;

import org.apache.ibatis.annotations.Param;
import org.zipper.transport.pojo.entity.TransportInstance;
import org.zipper.transport.pojo.vo.TransportInstanceVO;

import java.util.List;

/**
 * @author zhuxj
 * @since 2020/09/08
 */
public interface TransportInstanceMapper {

    /**
     * 新增实例
     *
     * @param instance 实例
     * @return 新增记录数
     */
    int insert(@Param("i") TransportInstance instance);

    /**
     * 实例更新
     *
     * @param instance 实例
     */
    void updateOne(@Param("i") TransportInstance instance);

    /**
     * 查询某个任务下所有实例
     *
     * @param transportId 任务编号
     * @return list of 实例
     */
    List<TransportInstanceVO> selectByTransportId(Integer transportId);

    /**
     * 查询某个实例
     *
     * @param instanceId 实例编号
     * @return 实例
     */
    TransportInstance selectByInstanceId(Integer instanceId);
}
