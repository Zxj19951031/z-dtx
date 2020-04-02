package cn.com.citydo.db.factory;

import cn.com.citydo.common.exceptions.SysException;
import cn.com.citydo.common.exceptions.errors.TypeError;
import cn.com.citydo.db.pojo.dto.DBDTO;
import cn.com.citydo.db.pojo.entity.DataBase;
import cn.com.citydo.db.pojo.entity.MySqlDB;
import cn.com.citydo.db.pojo.entity.OracleDB;
import org.springframework.stereotype.Component;

@Component
public class DBFactory implements AbstractFactory {
    @Override
    public DataBase createDB(DBDTO dto) {
        switch (dto.getDbType()) {
            case MySql:
                return MySqlDB.builder().dbName(dto.getDbName()).host(dto.getHost()).user(dto.getUser())
                        .password(dto.getPassword()).port(dto.getPort()).build();
            case Oracle:
                return OracleDB.builder().dbName(dto.getDbName()).host(dto.getHost()).user(dto.getUser())
                        .password(dto.getPassword()).port(dto.getPort()).connType(dto.getConnType()).build();
            default:
                throw SysException.newException(TypeError.UNKNOWN_TYPE,
                        String.format("不支持的数据源类型:[%s]", dto.getDbType()));
        }
    }
}
