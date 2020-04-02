package org.zipper.dtx.common.spi.tunnels;


import org.zipper.dtx.common.spi.records.Record;

public interface RecordConsumer {
    public Record consume();
}
