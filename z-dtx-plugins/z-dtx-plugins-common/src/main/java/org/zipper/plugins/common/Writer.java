package org.zipper.plugins.common;

import org.zipper.plugins.common.plugins.AbstractJobPlugin;
import org.zipper.plugins.common.plugins.AbstractTaskPlugin;
import org.zipper.plugins.common.tunnels.RecordConsumer;
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
