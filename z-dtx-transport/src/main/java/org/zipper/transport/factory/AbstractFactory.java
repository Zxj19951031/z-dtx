package org.zipper.transport.factory;

import org.zipper.transport.pojo.entity.DataBase;
import org.zipper.transport.pojo.dto.DBDTO;

public interface AbstractFactory {
    public DataBase createDB(DBDTO dto);
}
