package cn.com.citydo.dtx.common.spi;

import cn.com.citydo.consts.json.Configuration;
import cn.com.citydo.dtx.common.spi.plugins.AbstractJobPlugin;
import cn.com.citydo.dtx.common.spi.plugins.AbstractTaskPlugin;
import cn.com.citydo.dtx.common.spi.tunnels.RecordConsumer;

import java.util.List;

public abstract class Writer {

    public abstract class Job extends AbstractJobPlugin {

        public Job(Configuration allConfig) {
            super(allConfig);
        }

        public abstract List<Configuration> split(int channel);

    }

    public abstract class Task extends AbstractTaskPlugin {

        public Task(Configuration allConfig) {
            super(allConfig);
        }

        public abstract void startWrite(RecordConsumer consumer);

    }
}
