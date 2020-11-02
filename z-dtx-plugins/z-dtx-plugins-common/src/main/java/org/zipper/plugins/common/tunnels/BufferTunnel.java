package org.zipper.plugins.common.tunnels;

import cn.hutool.core.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zipper.plugins.common.collectors.TaskPluginCollector;
import org.zipper.plugins.common.commons.CoreConstant;
import org.zipper.plugins.common.errors.CommonError;
import org.zipper.plugins.common.records.DataRecord;
import org.zipper.plugins.common.records.Record;
import org.zipper.plugins.common.records.SkipRecord;
import org.zipper.plugins.common.records.TerminateRecord;
import org.zipper.plugins.common.tunnels.RecordConsumer;
import org.zipper.plugins.common.tunnels.RecordProducer;
import org.zipper.helper.exception.HelperException;
import org.zipper.helper.util.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 交换通道
 * 维护了一个阻塞队列进行实现消费-生产模型
 *
 * @author zhuxj
 */
public class BufferTunnel implements RecordConsumer, RecordProducer {

    private static final Logger log = LoggerFactory.getLogger(BufferTunnel.class);

    /**
     * 阻塞队列
     */
    private final BufferQueue queue;
    /**
     * 最大缓冲记录数
     * 当读取数量达到这个阈值后统一推至队列，默认1024
     * BufferQueue队列大小也等同这个
     */
    private final int bufferSize;
    /**
     * 单条记录最大字节数，默认8M
     */
    private final int byteCapacity;
    /**
     * 读取缓冲数组
     */
    private final List<Record> bufferIn;
    /**
     * 写入缓冲数组
     */
    private final List<Record> bufferOut;
    /**
     * 关闭通道标记
     */
    private volatile boolean shutdown = false;
    /**
     * 任务信息收集器
     */
    private TaskPluginCollector pluginCollector;

    public BufferTunnel(JsonObject tunnelConfig) {
        assert null != tunnelConfig;
        this.bufferSize = tunnelConfig.getInt(CoreConstant.JOB_TUNNEL_BUFFER_SIZE, 1024);
        this.byteCapacity = tunnelConfig.getInt(CoreConstant.JOB_TUNNEL_BYTE_CAPACITY, 8 * 1024 * 1024);
        this.bufferIn = new ArrayList<>(this.bufferSize);
        this.bufferOut = new ArrayList<>();
        this.queue = new BufferQueue(this.bufferSize);
    }

    /**
     * 消费记录，当写入缓冲数组大小为0时，则尝试去队列中获取
     *
     * @return 单条记录 如果是{@link TerminateRecord}则写入插件会停止写入
     */
    @Override
    public Record consume() {
        if (shutdown) {
            throw HelperException.newException(CommonError.SHUT_DOWN_TASK);
        }

        boolean isEmpty = (this.bufferOut.isEmpty());
        if (isEmpty) {
            List<Record> temp = new ArrayList<>();
            this.queue.doPullAll(temp);
            this.bufferOut.addAll(temp);
        }

        Record record = null;
        if (!this.bufferOut.isEmpty()) {
            record = this.bufferOut.get(0);
            this.bufferOut.remove(0);
        }

        if (record instanceof TerminateRecord) {
            record = null;
        }

        //写入数+1，之后可能因为插件异常导致最终没有写到目标位置，需要在那里进行减少写入数
        if (record instanceof DataRecord) {
            this.pluginCollector.incrWriteCnt(1);
        }
        return record;
    }

    /**
     * 生产一条空记录，并立即刷入队列
     */
    @Override
    public void produce() {
        this.produce(SkipRecord.get(), true);
    }

    /**
     * 生产一条记录，但不立即刷入队列
     *
     * @param record 记录
     */
    @Override
    public void produce(Record record) {
        this.produce(record, false);
    }

    /**
     * 生产一条记录并写入到读取缓冲数组
     * 当读取缓冲数组大小达到预设阈值时会进行刷入队列操作
     *
     * @param record      记录
     * @param immediately 及时消费
     */
    @Override
    public void produce(Record record, boolean immediately) {
        if (shutdown) {
            throw HelperException.newException(CommonError.SHUT_DOWN_TASK);
        }

        Assert.notNull(record, "record不能为空.");

        boolean isFull = (this.bufferIn.size() >= this.bufferSize);
        if (isFull || immediately) {
            flush();
        }

        this.bufferIn.add(record);
        //读取数+1
        if (record instanceof DataRecord) {
            this.pluginCollector.incrReadCnt(1);
        }
    }

    /**
     * 刷入队列，并清空读取缓冲
     */
    private void flush() {
        if (shutdown) {
            throw HelperException.newException(CommonError.SHUT_DOWN_TASK);
        }
        this.queue.doPushAll(this.bufferIn);
        this.bufferIn.clear();
    }

    /**
     * 提交读取缓冲中的剩余记录，并追加刷入一个终止记录
     */
    public void terminate() {
        if (shutdown) {
            throw HelperException.newException(CommonError.SHUT_DOWN_TASK);
        }
        flush();
        this.queue.terminate();
    }

    /**
     * 关闭通道
     */
    public void shutdown() {
        shutdown = true;
        try {
            bufferIn.clear();
            bufferOut.clear();
            queue.clear();
        } catch (Throwable t) {
            log.error("关闭通道失败", t);
        }
    }

    public TaskPluginCollector getPluginCollector() {
        return pluginCollector;
    }

    public void setPluginCollector(TaskPluginCollector pluginCollector) {
        this.pluginCollector = pluginCollector;
    }
}
