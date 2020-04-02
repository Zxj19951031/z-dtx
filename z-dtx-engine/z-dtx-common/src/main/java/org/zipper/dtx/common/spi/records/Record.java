package org.zipper.dtx.common.spi.records;


import org.zipper.dtx.common.spi.columns.Column;

public interface Record {
    public void addColumn(Column column);

    public void setColumn(int i, final Column column);

    public Column getColumn(int i);

    @Override
    public String toString();

    public int getColumnNumber();

    public int getByteSize();

    public int getMemorySize();
}
