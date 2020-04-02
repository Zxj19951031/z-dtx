package org.zipper.db.pojo.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhuxj
 * @since 2020/4/1
 */
@Data
@Builder
public class MySqlDB extends BaseEntity implements DataBase {
    private String dbName;
    private String host;
    private Integer port;
    private String user;
    private String password;

}
