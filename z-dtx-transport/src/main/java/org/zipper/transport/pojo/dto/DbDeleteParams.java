package org.zipper.transport.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zhuxj
 * @since 2020/4/1
 */
@Data
@ApiModel(value = "数据源删除入参")
public class DbDeleteParams {

    @ApiModelProperty(value = "待删除行")
    private List<DbDeleteRow> rows;

}

