package org.zipper.helper.data.transport.common.records;

import com.alibaba.fastjson.JSON;
import org.zipper.helper.data.transport.common.columns.Column;
import org.zipper.helper.data.transport.common.commons.ClassSize;
import org.zipper.helper.data.transport.common.errors.ColumnError;
import org.zipper.helper.exception.HelperException;

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

    @Override
    public void addColumn(Column column) {
        this.columns.add(column);
        this.incrByteSize(column);
    }

    @Override
    public void setColumn(int i, Column column) {
        if (i < 0) {
            throw HelperException.newException(ColumnError.ARGUMENT_ERROR, "不能给index小于0的column设置值");
        }

        if (i >= columns.size()) {
            expandCapacity(i + 1);
        }

        this.decrByteSize(getColumn(i));
        this.columns.set(i, column);
        this.incrByteSize(getColumn(i));
    }

    @Override
    public Column getColumn(int i) {
        if (i < 0 || i >= columns.size()) {
            return null;
        }
        return columns.get(i);
    }

    @Override
    public int getColumnNumber() {
        return this.columns.size();
    }

    @Override
    public int getByteSize() {
        return byteSize;
    }

    @Override
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
