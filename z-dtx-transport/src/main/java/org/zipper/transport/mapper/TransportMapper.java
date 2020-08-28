package org.zipper.transport.mapper;

import org.apache.ibatis.annotations.Param;
import org.zipper.transport.pojo.dto.TransportQueryParams;
import org.zipper.transport.pojo.entity.Transport;
import org.zipper.transport.pojo.vo.TransportVO;

import java.util.List;

/**
 * @author zhuxj
 * @since 2020/08/27
 */
public interface TransportMapper {

    int insert(@Param("t") Transport transport);

    List<TransportVO> select(@Param("p") TransportQueryParams params);

    Transport selectById(@Param("id") Integer id);

    int update(@Param("t") Transport transport);
}
