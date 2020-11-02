package org.zipper.plugins.common.tunnels;


import org.zipper.plugins.common.records.Record;

/**
 * 记录生产者
 */
public interface RecordProducer {

    /**
     * 生产空记录
     */
    void produce();

    /**
     * 生产记录
     *
     * @param record 记录
     */
    void produce(Record record);

    /**
     * 生产记录并及时告知消费者消费
     *
     * @param record      记录
     * @param immediately 及时消费
     */
    void produce(Record record, boolean immediately);

}
