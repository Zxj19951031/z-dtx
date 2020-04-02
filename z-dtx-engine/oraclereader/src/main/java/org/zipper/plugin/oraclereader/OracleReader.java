package org.zipper.plugin.oraclereader;

import org.zipper.common.json.Configuration;
import org.zipper.dtx.common.spi.Reader;
import org.zipper.dtx.common.spi.tunnels.RecordProducer;

import java.util.List;

public class OracleReader extends Reader {

    public static class Job extends  Reader.Job{

        @Override
        public List<Configuration> split(int channel) {
            return null;
        }

        @Override
        public void init() {

        }

        @Override
        public void destroy() {

        }
    }
    public static class Task extends Reader.Task{

        @Override
        public void startRead(RecordProducer producer) {

        }

        @Override
        public void init() {

        }

        @Override
        public void destroy() {

        }
    }
}
