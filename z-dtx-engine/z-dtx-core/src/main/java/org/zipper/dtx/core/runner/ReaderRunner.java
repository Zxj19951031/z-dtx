package org.zipper.dtx.core.runner;

import org.zipper.dtx.common.spi.Reader;
import org.zipper.dtx.common.spi.commons.CoreConstant;
import org.zipper.dtx.common.spi.tunnels.BufferTunnel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReaderRunner extends AbstractRunner implements Runnable {
    private BufferTunnel tunnel;
    private Reader.Task taskReader;
    private boolean autoTerminate;

    public ReaderRunner(BufferTunnel tunnel, Reader.Task taskReader) {
        super();
        this.tunnel = tunnel;
        this.taskReader = taskReader;
        this.autoTerminate = this.taskReader.getAllConfig().getBool(CoreConstant.AUTO_TERMINATE, true);
    }

    @Override
    public void run() {
        try {
            this.running();

            taskReader.init();

            taskReader.startRead(tunnel);

            taskReader.destroy();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.error();
        } finally {
            if (autoTerminate) {
                tunnel.terminate();
                log.info("reader autoTerminate");
            }
            this.finish();
        }
    }
}
