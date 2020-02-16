package cn.com.citydo.dtx.common.spi;

import cn.com.citydo.consts.json.Configuration;
import cn.com.citydo.dtx.common.spi.plugins.AbstractJobPlugin;
import cn.com.citydo.dtx.common.spi.plugins.AbstractTaskPlugin;
import cn.com.citydo.dtx.common.spi.tunnels.RecordProducer;

import java.util.List;

public abstract class Reader {

    public abstract class Job extends AbstractJobPlugin {

        public abstract List<Configuration> split(int channel);

    }

    public abstract class Task extends AbstractTaskPlugin {


        public abstract void startRead(RecordProducer producer);

    }
}
