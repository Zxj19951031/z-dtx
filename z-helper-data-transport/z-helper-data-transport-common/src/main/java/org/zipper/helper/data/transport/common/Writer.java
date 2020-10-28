package org.zipper.helper.data.transport.common;

import org.zipper.helper.data.transport.common.plugins.AbstractJobPlugin;
import org.zipper.helper.data.transport.common.plugins.AbstractTaskPlugin;
import org.zipper.helper.data.transport.common.tunnels.RecordConsumer;
import org.zipper.helper.util.json.JsonObject;

import java.util.List;

public abstract class Writer {

    public static abstract class Job extends AbstractJobPlugin {


        public abstract List<JsonObject> split(int channel);

    }

    public static abstract class Task extends AbstractTaskPlugin {


        public abstract void startWrite(RecordConsumer consumer);

    }
}
