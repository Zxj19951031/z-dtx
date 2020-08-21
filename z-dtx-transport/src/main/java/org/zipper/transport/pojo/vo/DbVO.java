package org.zipper.transport.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.zipper.transport.enums.DbType;

import java.time.LocalDateTime;

/**
 * @author zhuxj
 */
@Data
@ApiModel(value = "数据源列表视图")
public class DbVO {
    @ApiModelProperty(value = "数据源编号")
    private Integer id;
    @ApiModelProperty(value = "数据源名称")
    private String dbName;
    @ApiModelProperty(value = "数据源类型")
    private Integer dbType;
    @ApiModelProperty(value = "主机地址")
    private String host;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "数据源类型描述")
    public String getDbTypeStr() {
        return DbType.get(dbType).name();
    }


}
