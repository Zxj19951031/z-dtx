package cn.com.citydo.dtx.core.runner;

import cn.com.citydo.dtx.common.spi.Writer;
import cn.com.citydo.dtx.common.spi.tunnels.BufferTunnel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WriterRunner extends AbstractRunner implements Runnable {
    private BufferTunnel tunnel;
    private Writer.Task taskWriter;

    public WriterRunner(BufferTunnel tunnel, Writer.Task taskWriter) {
        super();
        this.tunnel = tunnel;
        this.taskWriter = taskWriter;
    }

    @Override
    public void run() {
        try {
            this.running();

            taskWriter.init();

            taskWriter.startWrite(tunnel);

            taskWriter.destroy();

        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            this.error();
        } finally {
            this.finish();
        }
    }
}
