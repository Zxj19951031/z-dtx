package org.zipper.transport.pojo.entity;

import lombok.Builder;
import lombok.Data;
import org.zipper.common.pojo.BaseEntity;

/**
 * @author zhuxj
 * @since 2020/4/1
 */
@Data
@Builder
public class OracleDB extends BaseEntity implements DataBase{
    private String dbName;
    private String host;
    private Integer port;
    private String user;
    private String password;
    private String connType;
}
