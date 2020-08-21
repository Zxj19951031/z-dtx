package org.zipper.transport.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zipper.helper.web.entity.BaseEntity;

/**
 * @author zhuxj
 * @since 2020/4/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OracleDb extends BaseEntity implements DataBase{
    private String dbName;
    private String host;
    private Integer port;
    private String user;
    private String password;
    private String connType;
}
