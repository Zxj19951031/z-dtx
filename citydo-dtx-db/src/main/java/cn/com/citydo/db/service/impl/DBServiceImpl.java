package cn.com.citydo.db.service.impl;

import cn.com.citydo.common.enums.Status;
import cn.com.citydo.common.exceptions.SysException;
import cn.com.citydo.common.exceptions.errors.TypeError;
import cn.com.citydo.db.factory.DBFactory;
import cn.com.citydo.db.mapper.DBMapper;
import cn.com.citydo.db.pojo.dto.DBDTO;
import cn.com.citydo.db.pojo.dto.DBQueryParams;
import cn.com.citydo.db.pojo.entity.BaseEntity;
import cn.com.citydo.db.pojo.entity.DataBase;
import cn.com.citydo.db.pojo.entity.MySqlDB;
import cn.com.citydo.db.pojo.entity.OracleDB;
import cn.com.citydo.db.pojo.vo.DBVO;
import cn.com.citydo.db.service.DBService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;


/**
 * @author zhuxj
 * @since 2020/4/2
 */
@Service
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
    public int updateOne(DBDTO db) {
        DataBase dataBase = dbFactory.createDB(db);
        BaseEntity record = (BaseEntity) dataBase;
        record.setId(db.getId());
        record.setUpdateTime(Calendar.getInstance().getTime());

        int id;
        switch (db.getDbType()) {
            case MySql:
                id = dbMapper.updateOneMysql((MySqlDB) record);
                break;
            case Oracle:
                id = dbMapper.updateOneOracle((OracleDB) record);
                break;
            default:
                throw SysException.newException(TypeError.UNKNOWN_TYPE,
                        String.format("不支持的数据源类型:[%s]", db.getDbType()));
        }
        return id;
    }
}
