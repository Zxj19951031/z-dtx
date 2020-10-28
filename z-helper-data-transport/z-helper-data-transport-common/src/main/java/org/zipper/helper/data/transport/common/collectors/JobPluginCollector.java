package org.zipper.helper.data.transport.common.collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * job 消息收集器
 *
 * @author zhuxj
 */
public class JobPluginCollector implements PluginCollector {

    private static final Logger logger = LoggerFactory.getLogger(JobPluginCollector.class);

    /**
     * 任务开始时间
     */
    private LocalDateTime startLocalDateTime;

    /**
     * 任务结束时间
     */
    private LocalDateTime endLocalDateTime;

    /**
     * 子task信息收集器
     */
    private List<TaskPluginCollector> taskPluginCollectors;


    private BigInteger readCnt = new BigInteger("0");
    private BigInteger writeCnt = new BigInteger("0");
    private BigInteger errorCnt = new BigInteger("0");

    /**
     * 阶段性汇报
     */
    public void cycleReport() {
        readCnt = BigInteger.valueOf(0);
        writeCnt = BigInteger.valueOf(0);
        errorCnt = BigInteger.valueOf(0);

        for (TaskPluginCollector pluginCollector : taskPluginCollectors) {
            readCnt = readCnt.add(pluginCollector.getReadCnt());
            writeCnt = writeCnt.add(pluginCollector.getWriteCnt());
            errorCnt = errorCnt.add(pluginCollector.getErrorCnt());
        }
        StringBuilder s = new StringBuilder();
        s.append("累计读取记录数:")
                .append(readCnt.toString())
                .append(" | ")
                .append("累计写入记录数:")
                .append(writeCnt.toString())
                .append(" | ")
                .append("累计失败记录数:")
                .append(errorCnt.toString());

        logger.info(s.toString());
    }

    /**
     * 终止汇报
     */
    public void finalReport() {
        long costTime = Duration.between(startLocalDateTime, endLocalDateTime).get(ChronoUnit.SECONDS);
        long speedIn = Math.round(readCnt.floatValue() / costTime);
        long speedOut = Math.round(writeCnt.floatValue() / costTime);

        StringBuilder s = new StringBuilder();
        s
                .append(String.format("%-15s:", "任务开始时间"))
                .append(String.format("%26s", startLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                .append("\n")
                .append(String.format("%-15s:", "任务结束时间"))
                .append(String.format("%26s", endLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                .append("\n")
                .append(String.format("%-15s:", "累计读取记录"))
                .append(String.format("%22s rec", readCnt.toString()))
                .append("\n")
                .append(String.format("%-15s:", "累计写入记录"))
                .append(String.format("%22s rec", writeCnt.toString()))
                .append("\n")
                .append(String.format("%-15s:", "累计失败记录"))
                .append(String.format("%22s rec", errorCnt.toString()))
                .append("\n")
                .append(String.format("%-15s:", "平均读取速度"))
                .append(String.format("%20s rec/s", speedIn))
                .append("\n")
                .append(String.format("%-15s:", "平均写入速度"))
                .append(String.format("%20s rec/s", speedOut));

        logger.info("\n" + s.toString());
    }

    public void setStartLocalDateTime(LocalDateTime startLocalDateTime) {
        this.startLocalDateTime = startLocalDateTime;
    }

    public void setEndLocalDateTime(LocalDateTime endLocalDateTime) {
        this.endLocalDateTime = endLocalDateTime;
    }

    public void setTaskPluginCollectors(List<TaskPluginCollector> taskPluginCollectors) {
        this.taskPluginCollectors = taskPluginCollectors;
    }

    public LocalDateTime getStartLocalDateTime() {
        return startLocalDateTime;
    }

    public LocalDateTime getEndLocalDateTime() {
        return endLocalDateTime;
    }

    public BigInteger getReadCnt() {
        return readCnt;
    }

    public void setReadCnt(BigInteger readCnt) {
        this.readCnt = readCnt;
    }

    public BigInteger getWriteCnt() {
        return writeCnt;
    }

    public void setWriteCnt(BigInteger writeCnt) {
        this.writeCnt = writeCnt;
    }

    public BigInteger getErrorCnt() {
        return errorCnt;
    }

    public void setErrorCnt(BigInteger errorCnt) {
        this.errorCnt = errorCnt;
    }
}
