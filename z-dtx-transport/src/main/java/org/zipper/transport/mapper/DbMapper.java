package org.zipper.transport.mapper;

import org.apache.ibatis.annotations.Param;
import org.zipper.transport.pojo.dto.DbQueryParams;
import org.zipper.transport.pojo.entity.MySqlDb;
import org.zipper.transport.pojo.entity.OracleDb;
import org.zipper.transport.pojo.entity.SqlServerDb;
import org.zipper.transport.pojo.vo.DbVO;

import java.util.List;

/**
 * @author zhuxj
 */
public interface DbMapper {

    int insertOneMysql(@Param("r") MySqlDb record);

    int insertOneOracle(@Param("r") OracleDb record);

    int insertOneSqlServer(@Param("r") SqlServerDb record);

    List<DbVO> selectUnionAll(@Param("p") DbQueryParams params);

    int updateOneMySql(@Param("r") MySqlDb record);

    int updateOneOracle(@Param("r") OracleDb record);

    int updateOneSqlServer(@Param("r") SqlServerDb record);

    int deleteBatchMySql(@Param("list") List<Integer> ids);

    int deleteBatchOracle(@Param("list") List<Integer> ids);

    int deleteBatchSqlServer(@Param("list") List<Integer> ids);

    MySqlDb selectOneMySql(@Param("id") Integer id);

    OracleDb selectOneOracle(@Param("id") Integer id);

    SqlServerDb selectOneSqlServer(@Param("id") Integer id);
}
