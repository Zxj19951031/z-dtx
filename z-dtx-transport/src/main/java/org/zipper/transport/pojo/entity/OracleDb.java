package org.zipper.transport.pojo.entity;

import io.swagger.annotations.ApiModelProperty;
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
public class OracleDb extends BaseEntity implements DataBase {
    @ApiModelProperty(value = "数据源名称")
    private String dbName;
    @ApiModelProperty(value = "主机地址")
    private String host;
    @ApiModelProperty(value = "端口号")
    private Integer port;
    @ApiModelProperty(value = "用户名")
    private String user;
    @ApiModelProperty(value = "密码")
    private String password;
    @ApiModelProperty(value = "连接类型[SID,TNS,SERVICE_NAME]")
    private String connType;
    @ApiModelProperty(value = "连接类型值")
    private String connValue;
    @ApiModelProperty(value = "驱动类型[Thin,OCI,OCI8]")
    private String driver;
}
