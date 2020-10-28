package org.zipper.helper.data.transport.core.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zipper.helper.data.transport.common.Reader;
import org.zipper.helper.data.transport.common.commons.CoreConstant;
import org.zipper.helper.data.transport.common.tunnels.BufferTunnel;

public class ReaderRunner extends AbstractRunner implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ReaderRunner.class);
    private final BufferTunnel tunnel;
    private final Reader.Task taskReader;
    private final boolean autoTerminate;

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
            log.info("task reader init ok");

            taskReader.startRead(tunnel);

            taskReader.destroy();
            log.info("task destroy init ok");

        } catch (Exception e) {
            log.error("执行读取插件Task异常", e);
            this.error();
        } finally {
            if (autoTerminate) {
                tunnel.terminate();
                log.info("task reader auto terminate");
            }
            this.finish();
        }
    }
}
