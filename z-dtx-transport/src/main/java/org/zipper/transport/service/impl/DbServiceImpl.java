package org.zipper.transport.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zipper.helper.exception.ErrorCode;
import org.zipper.helper.exception.HelperException;
import org.zipper.helper.web.entity.BaseEntity;
import org.zipper.transport.enums.DbInfo;
import org.zipper.transport.enums.DbType;
import org.zipper.transport.factory.DbFactory;
import org.zipper.transport.mapper.DbMapper;
import org.zipper.transport.pojo.dto.*;
import org.zipper.transport.pojo.entity.DataBase;
import org.zipper.transport.pojo.entity.MySqlDb;
import org.zipper.transport.pojo.entity.OracleDb;
import org.zipper.transport.pojo.entity.SqlServerDb;
import org.zipper.transport.pojo.vo.DbVO;
import org.zipper.transport.service.DbService;
import org.zipper.transport.utils.CatalogUtil;
import org.zipper.transport.utils.ColumnUtil;
import org.zipper.transport.utils.ConnectionUtil;
import org.zipper.transport.utils.TableUtil;

import javax.annotation.Resource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * @author zhuxj
 * @since 2020/4/2
 */
@Service
@Transactional
public class DbServiceImpl implements DbService {

    @Resource
    private DbMapper dbMapper;
    @Autowired
    private DbFactory dbFactory;

    @Override
    public Long addOne(DbDTO db) {

        DataBase dataBase = dbFactory.createDb(db);
        BaseEntity record = (BaseEntity) dataBase;
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(record.getCreateTime());
        record.setStatus(0);

        switch (db.getDbType()) {
            case MySql:
                dbMapper.insertOneMysql((MySqlDb) record);
                break;
            case Oracle:
                dbMapper.insertOneOracle((OracleDb) record);
                break;
            case SqlServer:
                dbMapper.insertOneSqlServer((SqlServerDb) record);
                break;
            default:
                throw HelperException.newException(ErrorCode.UNKNOWN_TYPE,
                        String.format("不支持的数据源类型:[%s]", db.getDbType()));
        }
        return record.getId();
    }

    @Override
    public List<DbVO> queryByParams(DbQueryParams params) {
        return dbMapper.selectUnionAll(params);
    }

    @Override
    public PageInfo<DbVO> queryByParams(DbQueryParams params, Integer pageNum, Integer pageSize) {
        if (pageNum >= 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<DbVO> list = queryByParams(params);
        return PageInfo.of(list);
    }

    @Override
    public boolean updateOne(DbDTO db) {
        DataBase dataBase = dbFactory.createDb(db);
        BaseEntity record = (BaseEntity) dataBase;
        record.setId(db.getId());
        record.setUpdateTime(LocalDateTime.now());

        int cnt;
        switch (db.getDbType()) {
            case MySql:
                cnt = dbMapper.updateOneMySql((MySqlDb) record);
                break;
            case Oracle:
                cnt = dbMapper.updateOneOracle((OracleDb) record);
                break;
            case SqlServer:
                cnt = dbMapper.updateOneSqlServer((SqlServerDb) record);
                break;
            default:
                throw HelperException.newException(ErrorCode.UNKNOWN_TYPE,
                        String.format("不支持的数据源类型:[%s]", db.getDbType()));
        }
        return cnt > 0;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean deleteBatch(DbDeleteParams params) {

        AtomicInteger cnt = new AtomicInteger();
        Map<DbType, List<Integer>> tmp = params.getRows().stream().collect(Collectors.toMap(
                DbDeleteRow::getType, row -> new ArrayList<Integer>() {{
                    add(row.getId());
                }},
                (o, n) -> {
                    o.addAll(n);
                    return o;
                }));

        tmp.forEach((type, ids) -> {
            switch (type) {
                case MySql:
                    cnt.addAndGet(dbMapper.deleteBatchMySql(ids));
                    break;
                case Oracle:
                    cnt.addAndGet(dbMapper.deleteBatchOracle(ids));
                    break;
                case SqlServer:
                    cnt.addAndGet(dbMapper.deleteBatchSqlServer(ids));
                    break;
                default:
                    throw HelperException.newException(ErrorCode.UNKNOWN_TYPE,
                            String.format("不支持的数据源类型:[%s],删除失败请重试", type));
            }
        });


        return cnt.get() > 0;
    }

    @Override
    public DataBase queryOne(Integer dbType, Integer id) {

        DataBase result = null;
        switch (DbType.get(dbType)) {
            case MySql:
                result = dbMapper.selectOneMySql(id);
                break;
            case Oracle:
                result = dbMapper.selectOneOracle(id);
                break;
            case SqlServer:
                result = dbMapper.selectOneSqlServer(id);
                break;
            default:
                throw HelperException.newException(ErrorCode.UNKNOWN_TYPE,
                        String.format("不支持的数据源类型:[%s]", DbType.get(dbType)));
        }
        return result;
    }

    @Override
    public boolean checkConnection(DbDTO db) {
        switch (db.getDbType()) {
            case MySql:
                ConnectionUtil.checkMysql(db.getHost(), db.getPort(), db.getUser(), db.getPassword());
                break;
            case Oracle:
                ConnectionUtil.checkOracle(db.getHost(), db.getPort(), db.getUser(), db.getPassword(), db.getConnType(), db.getConnValue(), db.getDriver());
                break;
            case SqlServer:
                ConnectionUtil.checkSqlServer(db.getHost(), db.getPort(), db.getUser(), db.getPassword(), db.getDatabase());
                break;
            default:
                throw HelperException.newException(ErrorCode.UNKNOWN_TYPE,
                        String.format("不支持的数据源类型:[%s]", db.getDbType()));
        }
        return true;
    }

    @Override
    public List<String> getInfo(DataBase dataBase, DbType dbType, DbInfo dbInfo, DbInfoParams params) {
        Connection conn = null;
        switch (dbType) {
            case MySql:
                conn = ConnectionUtil.getMysqlConnection(
                        ((MySqlDb) dataBase).getHost(),
                        ((MySqlDb) dataBase).getPort(),
                        ((MySqlDb) dataBase).getUser(),
                        ((MySqlDb) dataBase).getPassword());
                switch (dbInfo) {
                    case CATALOG:
                        return CatalogUtil.getMySqlCatalogs(conn);
                    case TABLE:
                        return TableUtil.getMySqlTables(conn, params.getCatalog());
                    case COLUMN:
                        return ColumnUtil.getMySqlColumns(conn, params.getCatalog(), params.getTable());
                    default:
                }
                break;
            case Oracle:
                conn = ConnectionUtil.getOracleConnection(
                        ((OracleDb) dataBase).getHost(),
                        ((OracleDb) dataBase).getPort(),
                        ((OracleDb) dataBase).getUser(),
                        ((OracleDb) dataBase).getPassword(),
                        ((OracleDb) dataBase).getConnType(),
                        ((OracleDb) dataBase).getConnValue(),
                        ((OracleDb) dataBase).getDriver());
                switch (dbInfo) {
                    case CATALOG:
                        return CatalogUtil.getOracleCatalogs(conn);
                    case TABLE:
                        return TableUtil.getOracleTables(conn, params.getCatalog());
                    case COLUMN:
                        return ColumnUtil.getOracleColumns(conn, params.getCatalog(), params.getTable());
                    default:
                }
                break;
            default:
        }
        return new ArrayList<>();
    }
}
