package cn.com.citydo.dtx.core.spi;

import cn.com.citydo.dtx.core.plugins.AbstractJobPlugin;
import cn.com.citydo.dtx.core.plugins.AbstractTaskPlugin;
import cn.com.citydo.dtx.core.spi.tunnels.RecordProducer;
import cn.com.citydo.consts.json.Configuration;

import java.util.List;

public abstract class Reader {

    public abstract class Job extends AbstractJobPlugin {

        public abstract List<Configuration> split(int channel);

    }

    public abstract class Task extends AbstractTaskPlugin {

        public abstract void startRead(RecordProducer producer);

    }
}
