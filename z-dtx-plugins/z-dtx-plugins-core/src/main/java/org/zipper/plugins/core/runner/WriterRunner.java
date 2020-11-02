package org.zipper.plugins.core.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zipper.plugins.common.Writer;
import org.zipper.plugins.common.tunnels.BufferTunnel;
import org.zipper.plugins.core.runner.AbstractRunner;

public class WriterRunner extends AbstractRunner implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(WriterRunner.class);
    private final BufferTunnel tunnel;
    private final Writer.Task taskWriter;

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
