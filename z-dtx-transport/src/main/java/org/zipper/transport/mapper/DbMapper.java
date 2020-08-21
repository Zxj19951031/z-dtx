package org.zipper.transport.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.zipper.transport.pojo.dto.DbQueryParams;
import org.zipper.transport.pojo.entity.MySqlDb;
import org.zipper.transport.pojo.entity.OracleDb;
import org.zipper.transport.pojo.vo.DbVO;

import java.util.List;

/**
 * @author zhuxj
 */
public interface DbMapper {

    int insertOneMysql(@Param("r") MySqlDb record);

    int insertOneOracle(@Param("r") OracleDb record);

    List<DbVO> selectUnionAll(@Param("p") DbQueryParams params);

    int updateOneMySql(@Param("r") MySqlDb record);

    int updateOneOracle(@Param("r") OracleDb record);

    int deleteBatchMySql(@Param("list") List<Integer> ids);

    int deleteBatchOracle(@Param("list") List<Integer> ids);

    MySqlDb selectOneMySql(@Param("id") Integer id);

    OracleDb selectOneOracle(@Param("id") Integer id);
}
