package org.zipper.db.service;

import org.zipper.db.pojo.dto.DBDTO;
import org.zipper.db.pojo.dto.DBQueryParams;
import org.zipper.db.pojo.vo.DBVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface DBService {
    int addOne(DBDTO db);

    List<DBVO> queryByParams(DBQueryParams params);

    PageInfo<DBVO> queryByParams(DBQueryParams params, Integer pageNum, Integer pageSize);

    int updateOne(DBDTO db);
}
