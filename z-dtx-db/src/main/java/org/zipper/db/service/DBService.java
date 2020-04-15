package org.zipper.db.service;

import org.zipper.db.pojo.dto.DBDeleteParams;
import org.zipper.db.pojo.dto.DBQueryParams;
import org.zipper.db.pojo.vo.DBVO;
import com.github.pagehelper.PageInfo;
import org.zipper.dto.DBDTO;

import java.util.List;

public interface DBService {
    int addOne(DBDTO db);

    List<DBVO> queryByParams(DBQueryParams params);

    PageInfo<DBVO> queryByParams(DBQueryParams params, Integer pageNum, Integer pageSize);

    boolean updateOne(DBDTO db);

    boolean deleteBatch(DBDeleteParams params);
}
