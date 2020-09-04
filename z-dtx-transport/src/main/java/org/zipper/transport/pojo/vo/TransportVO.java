package org.zipper.transport.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.zipper.transport.enums.TransportScheduleStatus;

import java.time.LocalDateTime;

/**
 * @author zhuxj
 * @since 2020/08/27
 */
@Data
@ApiModel(value = "传输任务视图")
public class TransportVO {
    @ApiModelProperty(value = "记录编号")
    private Long id;
    @ApiModelProperty(value = "任务名称")
    private String name;
    @ApiModelProperty(value = "调度状态描述")
    private String registeredDesc;
    @ApiModelProperty(value = "调度状态值")
    private Integer registered;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    public String getRegisteredDesc() {
        return TransportScheduleStatus.get(this.registered).getDesc();
    }
}
