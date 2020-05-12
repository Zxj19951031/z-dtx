package org.zipper.transport.service;

import org.zipper.transport.enums.DBInfo;
import org.zipper.transport.enums.DBType;
import org.zipper.transport.pojo.dto.DBDeleteParams;
import org.zipper.transport.pojo.dto.DBInfoParams;
import org.zipper.transport.pojo.dto.DBQueryParams;
import org.zipper.transport.pojo.entity.DataBase;
import org.zipper.transport.pojo.vo.DBVO;
import com.github.pagehelper.PageInfo;
import org.zipper.transport.pojo.dto.DBDTO;

import java.util.List;

public interface DBService {
    int addOne(DBDTO db);

    List<DBVO> queryByParams(DBQueryParams params);

    PageInfo<DBVO> queryByParams(DBQueryParams params, Integer pageNum, Integer pageSize);

    boolean updateOne(DBDTO db);

    boolean deleteBatch(DBDeleteParams params);

    DataBase queryOne(Integer id, Integer dbType);

    boolean checkConnection(DBDTO db);

    List<String> getInfo(DataBase dataBase, DBType dbType, DBInfo dbInfo, DBInfoParams params);
}
