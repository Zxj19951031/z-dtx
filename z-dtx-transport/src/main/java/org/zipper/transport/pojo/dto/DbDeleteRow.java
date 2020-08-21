package org.zipper.transport.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.zipper.transport.enums.DbType;

@Data
@ApiModel(value = "数据源待删除行")
public class DbDeleteRow {

    @ApiModelProperty(value = "数据源编号")
    private Integer id;
    @ApiModelProperty(value = "数据源类型")
    private DbType type;

    /**
     * 重载set
     * @param val value in {@link DbType}
     * @see DbType
     */
    public void setType(int val) {
        this.type = DbType.get(val);
    }

}
