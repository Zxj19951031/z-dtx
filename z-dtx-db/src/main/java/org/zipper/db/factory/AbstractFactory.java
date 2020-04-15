package org.zipper.db.factory;

import org.zipper.db.pojo.entity.DataBase;
import org.zipper.dto.DBDTO;

public interface AbstractFactory {
    public DataBase createDB(DBDTO dto);
}
