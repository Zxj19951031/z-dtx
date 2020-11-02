package org.zipper.plugins.common.tunnels;


import org.zipper.plugins.common.records.Record;

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
