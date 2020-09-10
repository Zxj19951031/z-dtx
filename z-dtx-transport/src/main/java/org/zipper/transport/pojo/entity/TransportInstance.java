package org.zipper.transport.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.zipper.helper.web.entity.BaseEntity;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * @author zhuxj
 * @since 2020/09/08
 */
@Data
@ApiModel(value = "传输任务实例")
public class TransportInstance extends BaseEntity {
    @ApiModelProperty(value = "任务编号")
    private Long tid;
    @ApiModelProperty(value = "读取记录数")
    private BigInteger readCnt;
    @ApiModelProperty(value = "写入记录数")
    private BigInteger writeCnt;
    @ApiModelProperty(value = "失败记录数")
    private BigInteger errorCnt;
    @ApiModelProperty(value = "传输任务配置")
    private String config;
    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;
    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;
}
