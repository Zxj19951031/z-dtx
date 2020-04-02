package cn.com.citydo.db.factory;

import cn.com.citydo.db.pojo.dto.DBDTO;
import cn.com.citydo.db.pojo.entity.DataBase;

public interface AbstractFactory {
    public DataBase createDB(DBDTO dto);
}
