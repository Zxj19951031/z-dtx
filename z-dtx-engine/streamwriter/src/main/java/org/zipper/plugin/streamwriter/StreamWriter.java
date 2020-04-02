package org.zipper.plugin.streamwriter;

import org.zipper.common.json.Configuration;
import org.zipper.dtx.common.spi.Writer;
import org.zipper.dtx.common.spi.records.Record;
import org.zipper.dtx.common.spi.tunnels.RecordConsumer;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class StreamWriter extends Writer {

    public static class Job extends Writer.Job {

        @Override
        public List<Configuration> split(int channel) {
            return new ArrayList<Configuration>() {{
                add(getAllConfig());
            }};
        }

        @Override
        public void init() {

        }

        @Override
        public void destroy() {

        }
    }

    public static class Task extends Writer.Task {

        @Override
        public void startWrite(RecordConsumer consumer) {
            Record record = null;
            while ((record = consumer.consume()) != null) {
                log.info(JSONUtil.parse(record).toJSONString(2));
            }
        }

        @Override
        public void init() {

        }

        @Override
        public void destroy() {

        }
    }
}
