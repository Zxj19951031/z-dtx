package org.zipper.helper.data.transport.common.tunnels;


import org.zipper.helper.data.transport.common.records.Record;

/**
 * @author zhuxj
 */
public interface RecordConsumer {
    /**
     * 消费记录
     *
     * @return {@link Record}
     */
    Record consume();
}
