package org.zipper.plugins.common.records;

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
