package cn.com.citydo.dtx.core.spi.tunnels;

import cn.com.citydo.dtx.core.spi.records.Record;

public interface RecordConsumer {
    public Record consume();
}
