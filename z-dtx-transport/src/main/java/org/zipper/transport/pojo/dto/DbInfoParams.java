package org.zipper.transport.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhuxj
 * @since 2020/5/12
 */
@Data
@ApiModel(value = "数据源明细入参")
public class DbInfoParams {
    /**
     * 数据库模型，对应不同类型的数据源含义可能不一样
     */
    @ApiModelProperty(value = "数据库模型")
    private String schema;
    /**
     * 数据库库名，对应不同类型的数据源含义可能不一样
     */
    @ApiModelProperty(value = "数据库库名")
    private String catalog;
    /**
     * 数据库表，对应不同类型的数据源含义可能不一样
     */
    @ApiModelProperty(value = "数据库表")
    private String table;
}
