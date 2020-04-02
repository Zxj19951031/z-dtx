package org.zipper.dtx.common.spi;

import org.zipper.common.json.Configuration;
import org.zipper.dtx.common.spi.plugins.AbstractJobPlugin;
import org.zipper.dtx.common.spi.plugins.AbstractTaskPlugin;
import org.zipper.dtx.common.spi.tunnels.RecordProducer;

import java.util.List;

public abstract class Reader {

    public static abstract class Job extends AbstractJobPlugin {

        public abstract List<Configuration> split(int channel);

    }

    public static abstract class Task extends AbstractTaskPlugin {


        public abstract void startRead(RecordProducer producer);

    }
}
