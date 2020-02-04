package cn.com.citydo.dtx.core.spi.records;

/**
 * 脏数据类型记录
 */
public class DirtyRecord extends DataRecord {
    public DirtyRecord(DataRecord record) {
        this.columns = record.columns;
        this.byteSize = record.byteSize;
        this.memorySize = record.memorySize;
    }
}
