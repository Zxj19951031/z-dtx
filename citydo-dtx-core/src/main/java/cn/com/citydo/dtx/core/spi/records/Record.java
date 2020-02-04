package cn.com.citydo.dtx.core.spi.records;

import cn.com.citydo.dtx.core.spi.columns.Column;

public interface Record{
    public void addColumn(Column column);

    public void setColumn(int i, final Column column);

    public Column getColumn(int i);

    @Override
    public String toString();

    public int getColumnNumber();

    public int getByteSize();

    public int getMemorySize();
}
