package cn.com.citydo.plugin.rocketmqreader;

import cn.com.citydo.common.json.Configuration;
import cn.com.citydo.dtx.common.spi.Reader;
import cn.com.citydo.dtx.common.spi.columns.StringColumn;
import cn.com.citydo.dtx.common.spi.records.DataRecord;
import cn.com.citydo.dtx.common.spi.records.Record;
import cn.com.citydo.dtx.common.spi.tunnels.RecordProducer;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.order.ConsumeOrderContext;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.ons.api.order.OrderAction;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
public class RocketMqReader extends Reader {
    public static class Job extends Reader.Job {


        @Override
        public void init() {

        }

        @Override
        public List<Configuration> split(int channel) {
            List<Configuration> configurations = new ArrayList<>();
            for (int i = 0; i < channel; i++) {
                configurations.add(this.getAllConfig().clone());
            }
            return configurations;
        }

        @Override
        public void destroy() {

        }
    }

    public static class Task extends Reader.Task {

        @Override
        public void init() {

        }

        @Override
        public void startRead(RecordProducer producer) {
            List<String> columns = this.getAllConfig().getList(Keys.COLUMNS, String.class);

            Properties properties = new Properties();
            properties.put(PropertyKeyConst.GROUP_ID, this.getAllConfig().getString(Keys.GROUP_ID));
            properties.put(PropertyKeyConst.AccessKey, this.getAllConfig().getString(Keys.ACCESS_KEY));
            properties.put(PropertyKeyConst.SecretKey, this.getAllConfig().getString(Keys.SECRET_KEY));
            properties.put(PropertyKeyConst.NAMESRV_ADDR, this.getAllConfig().getString(Keys.NAME_ADDR));
            properties.put(PropertyKeyConst.SuspendTimeMillis,
                    this.getAllConfig().getString(Keys.SUSPEND_TIME_MILLIS, "100"));
            properties.put(PropertyKeyConst.MaxReconsumeTimes,
                    this.getAllConfig().getString(Keys.MAX_RECONSUME_TIMES, "20"));

            OrderConsumer consumer = ONSFactory.createOrderedConsumer(properties);
            try {
                consumer.subscribe(
                        // Message 所属的 Topic
                        this.getAllConfig().getString(Keys.TOPIC),
                        // 订阅指定 Topic 下的 Tags：
                        // 1. * 表示订阅所有消息
                        // 2. TagA || TagB || TagC 表示订阅 TagA 或 TagB 或 TagC 的消息
                        this.getAllConfig().getString(Keys.TAGS, "*"),
                        new MessageOrderListener() {
                            /**
                             * 1. 消息消费处理失败或者处理出现异常，返回 OrderAction.Suspend<br>
                             * 2. 消息处理成功，返回 OrderAction.Success
                             */
                            @Override
                            public OrderAction consume(Message message, ConsumeOrderContext context) {
                                Record record = new DataRecord();
                                Configuration data = Configuration.from(new String(message.getBody()));

                                for (int i = 0; i < record.getColumnNumber(); i++) {
                                    record.addColumn(new StringColumn(data.getString(columns.get(i))));
                                }
                                producer.produce(record);

                                return OrderAction.Success;
                            }
                        });

                consumer.start();
                log.info("consumer start");

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                consumer.shutdown();
                log.info("consumer shutdown");
            }
        }


        @Override
        public void destroy() {

        }
    }
}
