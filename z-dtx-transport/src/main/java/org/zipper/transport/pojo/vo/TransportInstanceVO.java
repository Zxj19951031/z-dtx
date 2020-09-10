package org.zipper.transport.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.zipper.transport.enums.TransportInstanceStatus;

import java.time.LocalDateTime;

/**
 * @author zhuxj
 * @since 2020/09/08
 */
@Data
public class TransportInstanceVO {
    @ApiModelProperty(value = "实例编号")
    private Long id;
    @ApiModelProperty(value = "读取记录数")
    private String readCnt;
    @ApiModelProperty(value = "写入记录数")
    private String writeCnt;
    @ApiModelProperty(value = "异常记录数")
    private String errorCnt;
    @ApiModelProperty(value = "实例状态描述")
    private Integer status;
    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    public String getStatusDesc() {
        return TransportInstanceStatus.get(this.status).getDesc();
    }
}
