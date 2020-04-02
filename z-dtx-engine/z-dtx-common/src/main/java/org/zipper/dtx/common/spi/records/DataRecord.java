package org.zipper.dtx.common.spi.records;

import org.zipper.common.exceptions.SysException;
import org.zipper.dtx.common.spi.columns.Column;
import org.zipper.dtx.common.spi.commons.ClassSize;
import org.zipper.dtx.common.spi.errors.ColumnError;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据记录
 */
public class DataRecord implements Record {

    protected List<Column> columns;
    protected int byteSize;
    protected int memorySize = ClassSize.DEFAULT_RECORD_HEAD;

    public DataRecord() {
        this.columns = new ArrayList<>();
    }

    public void addColumn(Column column) {
        this.columns.add(column);
        this.incrByteSize(column);
    }

    public void setColumn(int i, Column column) {
        if (i < 0) {
            throw SysException.newException(ColumnError.ARGUMENT_ERROR, "不能给index小于0的column设置值");
        }

        if (i >= columns.size()) {
            expandCapacity(i + 1);
        }

        this.decrByteSize(getColumn(i));
        this.columns.set(i, column);
        this.incrByteSize(getColumn(i));
    }

    public Column getColumn(int i) {
        if (i < 0 || i >= columns.size()) {
            return null;
        }
        return columns.get(i);
    }

    public int getColumnNumber() {
        return this.columns.size();
    }

    public int getByteSize() {
        return byteSize;
    }

    public int getMemorySize() {
        return memorySize;
    }

    private void expandCapacity(int totalSize) {
        if (totalSize <= 0) {
            return;
        }

        int needToExpand = totalSize - columns.size();
        while (needToExpand-- > 0) {
            this.columns.add(null);
        }
    }

    private void decrByteSize(final Column column) {
        if (null == column) {
            return;
        }

        byteSize -= column.getByteSize();

        //内存的占用是column对象的头 再加实际大小
        memorySize = memorySize - ClassSize.COLUMN_HEAD - column.getByteSize();
    }

    private void incrByteSize(final Column column) {
        if (null == column) {
            return;
        }

        byteSize += column.getByteSize();

        //内存的占用是column对象的头 再加实际大小
        memorySize = memorySize + ClassSize.COLUMN_HEAD + column.getByteSize();
    }

    public List<Column> getColumns() {
        return columns;
    }

    @Override
    public String toString() {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("size", this.getColumnNumber());
        json.put("data", columns);
        json.put("byteSize", byteSize);
        json.put("memorySize", memorySize);
        return JSON.toJSONString(json);
    }
}