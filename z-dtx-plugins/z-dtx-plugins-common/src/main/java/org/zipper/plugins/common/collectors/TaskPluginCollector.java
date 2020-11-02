package org.zipper.plugins.common.collectors;

import java.math.BigInteger;

/**
 * Task 的信息收集器
 *
 * @author zhuxj
 */
public class TaskPluginCollector implements PluginCollector {
    /**
     * 读取记录数
     */
    private BigInteger readCnt;
    /**
     * 写入记录数
     */
    private BigInteger writeCnt;
    /**
     * 写入失败记录数
     */
    private BigInteger errorCnt;

    public TaskPluginCollector() {
        this.readCnt = new BigInteger("0");
        this.writeCnt = new BigInteger("0");
        this.errorCnt = new BigInteger("0");
    }

    /**
     * 增加读取记录
     */
    public void incrReadCnt(int cnt) {
        readCnt = readCnt.add(BigInteger.valueOf(cnt));
    }

    /**
     * 减少读取记录
     */
    public void decrReadCnt(int cnt) {
        readCnt = readCnt.add(BigInteger.valueOf(-cnt));
    }

    /**
     * 新增写入记录
     */
    public void incrWriteCnt(int cnt) {
        writeCnt = writeCnt.add(BigInteger.valueOf(cnt));
    }

    /**
     * 减少写入记录
     */
    public void decrWriteCnt(int cnt) {
        writeCnt = writeCnt.add(BigInteger.valueOf(-cnt));
    }

    /**
     * 增加失败记录数
     */
    public void incrErrorCnt(int cnt) {
        errorCnt = errorCnt.add(BigInteger.valueOf(cnt));
    }

    public BigInteger getReadCnt() {
        return readCnt;
    }

    public BigInteger getWriteCnt() {
        return writeCnt;
    }

    public BigInteger getErrorCnt() {
        return errorCnt;
    }
}
