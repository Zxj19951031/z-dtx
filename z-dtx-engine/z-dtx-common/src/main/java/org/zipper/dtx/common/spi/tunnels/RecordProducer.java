package org.zipper.dtx.common.spi.tunnels;


import org.zipper.dtx.common.spi.records.Record;

/**
 * 记录生产者
 */
public interface RecordProducer {

    /**
     * 生产空记录
     */
    public void produce();

    /**
     * 生产记录
     *
     * @param record 记录
     */
    public void produce(Record record);

    /**
     * 生产记录并及时告知消费者消费
     *
     * @param record      记录
     * @param immediately 及时消费
     */
    public void produce(Record record, boolean immediately);

}
