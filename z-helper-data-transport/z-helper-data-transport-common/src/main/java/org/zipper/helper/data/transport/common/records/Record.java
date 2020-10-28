package org.zipper.helper.data.transport.common.records;


import org.zipper.helper.data.transport.common.columns.Column;

/**
 * 记录
 *
 * @author zhuxj
 */
public interface Record {
    /**
     * 添加列
     *
     * @param column 列
     */
    void addColumn(Column column);

    /**
     * 设置列
     *
     * @param i      下标索引
     * @param column 列
     */
    void setColumn(int i, final Column column);

    /**
     * 获取列
     *
     * @param i 下标索引
     * @return 列
     */
    Column getColumn(int i);

    @Override
    String toString();

    /**
     * 获取列数量
     *
     * @return 数量
     */
    int getColumnNumber();

    /**
     * 获取字节数
     *
     * @return 字节数
     */
    int getByteSize();

    /**
     * 获取内存数
     *
     * @return 内存数
     */
    int getMemorySize();
}
