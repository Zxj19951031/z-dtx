package cn.com.citydo.dtx.core.runner;

import cn.com.citydo.dtx.common.spi.Writer;
import cn.com.citydo.dtx.common.spi.tunnels.BufferTunnel;

public class WriterRunner implements Runnable {
    private BufferTunnel tunnel;
    private Writer.Task taskWriter;

    public WriterRunner(BufferTunnel tunnel, Writer.Task taskWriter) {
        this.tunnel = tunnel;
        this.taskWriter = taskWriter;
    }

    @Override
    public void run() {

        try {

        } catch (Throwable e) {

        } finally {

        }
    }
}
