package cn.com.citydo.dtx.core.runner;

import cn.com.citydo.dtx.common.spi.Reader;
import cn.com.citydo.dtx.common.spi.tunnels.BufferTunnel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReaderRunner extends AbstractRunner implements Runnable {
    private BufferTunnel tunnel;
    private Reader.Task taskReader;

    public ReaderRunner(BufferTunnel tunnel, Reader.Task taskReader) {
        super();
        this.tunnel = tunnel;
        this.taskReader = taskReader;
    }

    @Override
    public void run() {
        try {
            this.running();

            taskReader.init();

            taskReader.startRead(tunnel);

            tunnel.terminate();

            taskReader.destroy();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.error();
        } finally {
            this.finish();
        }
    }


}
