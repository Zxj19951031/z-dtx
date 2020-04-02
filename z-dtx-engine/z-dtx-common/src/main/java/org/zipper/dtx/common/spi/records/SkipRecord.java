package org.zipper.dtx.common.spi.records;


import org.zipper.dtx.common.spi.columns.Column;

/**
 * 无用记录，实时读取插件为了不让通道阻塞而通知写入插件的无用记录
 */
public class SkipRecord implements Record {
    private final static SkipRecord SINGLE = new SkipRecord();

    private SkipRecord() {
    }

    public static SkipRecord get() {
        return SINGLE;
    }

    @Override
    public void addColumn(Column column) {

    }

    @Override
    public void setColumn(int i, Column column) {

    }

    @Override
    public Column getColumn(int i) {
        return null;
    }

    @Override
    public int getColumnNumber() {
        return 0;
    }

    @Override
    public int getByteSize() {
        return 0;
    }

    @Override
    public int getMemorySize() {
        return 0;
    }
}