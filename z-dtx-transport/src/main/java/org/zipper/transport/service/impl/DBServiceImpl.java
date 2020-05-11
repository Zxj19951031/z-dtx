package org.zipper.transport.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zipper.common.enums.Status;
import org.zipper.common.exceptions.SysException;
import org.zipper.common.exceptions.errors.TypeError;
import org.zipper.common.pojo.BaseEntity;
import org.zipper.transport.factory.DBFactory;
import org.zipper.transport.mapper.DBMapper;
import org.zipper.transport.pojo.dto.DBDeleteParams;
import org.zipper.transport.pojo.dto.DBDeleteRow;
import org.zipper.transport.pojo.dto.DBQueryParams;
import org.zipper.transport.pojo.entity.DataBase;
import org.zipper.transport.pojo.entity.MySqlDB;
import org.zipper.transport.pojo.entity.OracleDB;
import org.zipper.transport.pojo.vo.DBVO;
import org.zipper.transport.service.DBService;
import org.zipper.transport.pojo.dto.DBDTO;
import org.zipper.transport.enums.DBType;

import java.util.ArrayList;
import java.util.Calendar;
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
public class DBServiceImpl implements DBService {

    @Autowired
    private DBMapper dbMapper;
    @Autowired
    private DBFactory dbFactory;

    @Override
    public int addOne(DBDTO db) {

        DataBase dataBase = dbFactory.createDB(db);
        BaseEntity record = (BaseEntity) dataBase;
        record.setCreateTime(Calendar.getInstance().getTime());
        record.setUpdateTime(record.getCreateTime());
        record.setStatus(Status.VALID);

        int id;
        switch (db.getDbType()) {
            case MySql:
                id = dbMapper.insertOneMysql((MySqlDB) record);
                break;
            case Oracle:
                id = dbMapper.insertOneOracle((OracleDB) record);
                break;
            default:
                throw SysException.newException(TypeError.UNKNOWN_TYPE,
                        String.format("不支持的数据源类型:[%s]", db.getDbType()));
        }
        return id;
    }

    @Override
    public List<DBVO> queryByParams(DBQueryParams params) {
        return dbMapper.selectUnionAll(params);
    }

    @Override
    public PageInfo<DBVO> queryByParams(DBQueryParams params, Integer pageNum, Integer pageSize) {
        if (pageNum >= 0)
            PageHelper.startPage(pageNum, pageSize);
        List<DBVO> list = queryByParams(params);
        return PageInfo.of(list);
    }

    @Override
    public boolean updateOne(DBDTO db) {
        DataBase dataBase = dbFactory.createDB(db);
        BaseEntity record = (BaseEntity) dataBase;
        record.setId(db.getId());
        record.setUpdateTime(Calendar.getInstance().getTime());

        int cnt;
        switch (db.getDbType()) {
            case MySql:
                cnt = dbMapper.updateOneMySql((MySqlDB) record);
                break;
            case Oracle:
                cnt = dbMapper.updateOneOracle((OracleDB) record);
                break;
            default:
                throw SysException.newException(TypeError.UNKNOWN_TYPE,
                        String.format("不支持的数据源类型:[%s]", db.getDbType()));
        }
        return cnt > 0;
    }

    @Override
    public boolean deleteBatch(DBDeleteParams params) {

        AtomicInteger cnt = new AtomicInteger();
        Map<DBType, List<Integer>> tmp = params.getRows().stream().collect(Collectors.toMap(
                DBDeleteRow::getType, row -> new ArrayList<Integer>() {{
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
                default:
                    throw SysException.newException(TypeError.UNKNOWN_TYPE,
                            String.format("不支持的数据源类型:[%s]", type));
            }
        });


        return cnt.get() > 0;
    }

    @Override
    public DataBase queryOne(Integer id, Integer dbType) {

        DataBase result = null;
        switch (DBType.get(dbType)) {
            case MySql:
                result = dbMapper.selectOneMySql(id);
                break;
            case Oracle:
                result = dbMapper.selectOneOracle(id);
                break;
            default:
                throw SysException.newException(TypeError.UNKNOWN_TYPE,
                        String.format("不支持的数据源类型:[%s]", DBType.get(dbType)));
        }
        return result;
    }
}
