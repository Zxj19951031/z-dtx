package org.zipper.db.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zipper.common.enums.Status;
import org.zipper.common.exceptions.SysException;
import org.zipper.common.exceptions.errors.TypeError;
import org.zipper.db.factory.DBFactory;
import org.zipper.db.mapper.DBMapper;
import org.zipper.db.pojo.dto.DBDeleteParams;
import org.zipper.db.pojo.dto.DBQueryParams;
import org.zipper.db.pojo.entity.BaseEntity;
import org.zipper.db.pojo.entity.DataBase;
import org.zipper.db.pojo.entity.MySqlDB;
import org.zipper.db.pojo.entity.OracleDB;
import org.zipper.db.pojo.vo.DBVO;
import org.zipper.db.service.DBService;
import org.zipper.dto.DBDTO;

import java.util.Calendar;
import java.util.List;


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

        int cnt;
        switch (params.getType()) {
            case MySql:
                cnt = dbMapper.deleteBatchMySql(params.getIds());
                break;
            case Oracle:
                cnt = dbMapper.deleteBatchOracle(params.getIds());
                break;
            default:
                throw SysException.newException(TypeError.UNKNOWN_TYPE,
                        String.format("不支持的数据源类型:[%s]", params.getType()));
        }
        return cnt>0;
    }
}
