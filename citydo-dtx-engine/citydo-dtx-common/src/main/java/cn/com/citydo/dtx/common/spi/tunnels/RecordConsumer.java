package cn.com.citydo.dtx.common.spi.tunnels;


import cn.com.citydo.dtx.common.spi.records.Record;

public interface RecordConsumer {
    public Record consume();
}
