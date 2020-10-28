package org.zipper.helper.data.transport.common.records;


import org.zipper.helper.data.transport.common.columns.Column;

/**
 * 终止符记录，读取插件通知写入插件停止的操作
 */
public class TerminateRecord implements Record {
    private final static TerminateRecord SINGLE = new TerminateRecord();

    private TerminateRecord() {
    }

    public static TerminateRecord get() {
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