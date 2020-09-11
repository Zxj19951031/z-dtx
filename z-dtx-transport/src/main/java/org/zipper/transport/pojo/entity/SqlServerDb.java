package org.zipper.transport.pojo.entity;

import io.swagger.annotations.ApiModel;
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
@ApiModel(value = "SqlServer数据源")
public class SqlServerDb extends BaseEntity implements DataBase {

    @ApiModelProperty("数据源名称")
    private String dbName;
    @ApiModelProperty("主机地址")
    private String host;
    @ApiModelProperty("端口号")
    private Integer port;
    @ApiModelProperty("用户名")
    private String user;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("数据库")
    private String database;
}
