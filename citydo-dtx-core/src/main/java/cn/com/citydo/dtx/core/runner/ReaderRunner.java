package cn.com.citydo.dtx.core.runner;

import cn.com.citydo.dtx.common.spi.Reader;
import cn.com.citydo.dtx.common.spi.tunnels.BufferTunnel;

public class ReaderRunner implements Runnable {
    private BufferTunnel tunnel;
    private Reader.Task taskReader;

    public ReaderRunner(BufferTunnel tunnel, Reader.Task taskReader) {
        this.tunnel = tunnel;
        this.taskReader = taskReader;
    }

    @Override
    public void run() {
        try {

        } catch (Throwable e) {

        } finally {

        }
    }


}
