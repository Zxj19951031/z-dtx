package org.zipper.transport.pojo.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.zipper.helper.web.entity.BaseEntity;
import org.zipper.transport.enums.TransportScheduleStatus;

/**
 * @author zhuxj
 * @since 2020/08/27
 */
@Data
@ApiModel(value = "传输任务")
public class Transport extends BaseEntity {
    @ApiModelProperty(value = "任务名称")
    private String name;
    @ApiModelProperty(value = "源头数据源")
    private String source;
    @ApiModelProperty(value = "目标数据源")
    private String target;
    @ApiModelProperty(value = "规则编号")
    private Long ruleId;
    @ApiModelProperty(value = "任务配置")
    private String config;
    @ApiModelProperty(value = "前端元数据，后台留存")
    private String metadata;
    @ApiModelProperty(value = "调度状态0-未调度，1-调度中")
    private Integer registered;
}
