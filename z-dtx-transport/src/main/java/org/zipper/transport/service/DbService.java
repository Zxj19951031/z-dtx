package org.zipper.transport.service;

import com.github.pagehelper.PageInfo;
import org.zipper.transport.enums.DbInfo;
import org.zipper.transport.enums.DbType;
import org.zipper.transport.pojo.dto.DbDTO;
import org.zipper.transport.pojo.dto.DbDeleteParams;
import org.zipper.transport.pojo.dto.DbInfoParams;
import org.zipper.transport.pojo.dto.DbQueryParams;
import org.zipper.transport.pojo.entity.DataBase;
import org.zipper.transport.pojo.vo.DbVO;

import java.util.List;

/**
 * @author zhuxj
 */
public interface DbService {

    /**
     * 新增数据源
     *
     * @param db 数据源入参
     * @return 成功插入记录数
     */
    Long addOne(DbDTO db);

    /**
     * 查询数据源列表
     *
     * @param params 查询参数
     * @return list of {@link DbVO}
     */
    List<DbVO> queryByParams(DbQueryParams params);

    /**
     * 查询数据源列表
     *
     * @param params 查询参数
     * @return PageInfo of {@link DbVO}
     */
    PageInfo<DbVO> queryByParams(DbQueryParams params, Integer pageNum, Integer pageSize);

    /**
     * 更新数据源
     *
     * @param db 数据源详情
     * @return true or false
     * @see DbDTO
     */
    boolean updateOne(DbDTO db);

    /**
     * 批量删除数据源
     *
     * @param params 删除行信息
     * @return true or false
     * @see DbDeleteParams
     */
    boolean deleteBatch(DbDeleteParams params);

    /**
     * 查看详情
     *
     * @param id     数据源编号
     * @param dbType 数据源类型
     * @return Implement of {@link DataBase}
     * @see org.zipper.transport.pojo.entity.MySqlDb
     * @see org.zipper.transport.pojo.entity.OracleDb
     */
    DataBase queryOne(Integer id, Integer dbType);

    /**
     * 确认数据源联通性
     *
     * @param db 数据源详情
     * @return true or false
     * @see DbDTO
     */
    boolean checkConnection(DbDTO db);

    /**
     * 查询数据源明细
     *
     * @param dataBase 数据源详情 implement of {@link DataBase}
     * @param dbType   数据源类型 {@link DbType}
     * @param dbInfo   数据源明细标签 {@link DbInfo}
     * @param params   数据源明细查询入参
     * @return 明细名称列表，可以是数据模型名称，可以是数据库名称，可以是数据库表名称，可以是字段名称
     * @see org.zipper.transport.pojo.entity.MySqlDb
     * @see org.zipper.transport.pojo.entity.OracleDb
     */
    List<String> getInfo(DataBase dataBase, DbType dbType, DbInfo dbInfo, DbInfoParams params);
}
