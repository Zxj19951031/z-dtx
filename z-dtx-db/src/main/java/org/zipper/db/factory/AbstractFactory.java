package org.zipper.db.factory;

import org.zipper.db.pojo.dto.DBDTO;
import org.zipper.db.pojo.entity.DataBase;

public interface AbstractFactory {
    public DataBase createDB(DBDTO dto);
}
