package org.zipper.transport.factory;

import org.springframework.stereotype.Component;
import org.zipper.helper.exception.ErrorCode;
import org.zipper.helper.exception.HelperException;
import org.zipper.transport.pojo.dto.DbDTO;
import org.zipper.transport.pojo.entity.DataBase;
import org.zipper.transport.pojo.entity.MySqlDb;
import org.zipper.transport.pojo.entity.OracleDb;

/**
 * @author zhuxj
 */
@Component
public class DbFactory {

    public DataBase createDb(DbDTO dto) {
        switch (dto.getDbType()) {
            case MySql:
                return MySqlDb.builder().dbName(dto.getDbName()).host(dto.getHost()).user(dto.getUser())
                        .password(dto.getPassword()).port(dto.getPort()).build();
            case Oracle:
                return OracleDb.builder().dbName(dto.getDbName()).host(dto.getHost()).user(dto.getUser())
                        .password(dto.getPassword()).port(dto.getPort()).connType(dto.getConnType()).build();
            default:
                throw HelperException.newException(ErrorCode.UNKNOWN_TYPE,
                        String.format("不支持的数据源类型:[%s]", dto.getDbType()));
        }
    }
}
