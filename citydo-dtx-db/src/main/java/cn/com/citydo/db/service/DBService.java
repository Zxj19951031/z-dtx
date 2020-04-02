package cn.com.citydo.db.service;

import cn.com.citydo.db.pojo.dto.DBDTO;
import cn.com.citydo.db.pojo.dto.DBQueryParams;
import cn.com.citydo.db.pojo.vo.DBVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface DBService {
    int addOne(DBDTO db);

    List<DBVO> queryByParams(DBQueryParams params);

    PageInfo<DBVO> queryByParams(DBQueryParams params, Integer pageNum, Integer pageSize);

    int updateOne(DBDTO db);
}
