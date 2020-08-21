package org.zipper.transport.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.zipper.transport.enums.DbType;

/**
 * @author zhuxj
 * @since 2020/4/1
 */
@Data
@ApiModel(value = "数据源入参详情")
public class DbDTO {
    @ApiModelProperty(value = "数据源编号")
    private Long id;
    @ApiModelProperty(value = "数据源名称")
    private String dbName;
    @ApiModelProperty(value = "数据源类型", allowableValues = "1,2")
    private DbType dbType;
    @ApiModelProperty(value = "主机地址")
    private String host;
    @ApiModelProperty(value = "端口号")
    private Integer port;
    @ApiModelProperty(value = "用户名称")
    private String user;
    @ApiModelProperty(value = "密码")
    private String password;
    @ApiModelProperty(value = "连接类型", allowableValues = "SID,SERVER_NAME,TNS")
    private String connType;

    /**
     * 重载set
     *
     * @param key {@link DbType}
     */
    public void setDbType(String key) {
        this.dbType = DbType.valueOf(key);
    }

    /**
     * 重载set
     * @param val see type properties of {@link DbType}
     */
    public void setDbType(int val) {
        this.dbType = DbType.get(val);
    }
}
