package cn.com.citydo.plugin.rocketmqwriter;

import cn.com.citydo.common.json.Configuration;
import cn.com.citydo.dtx.common.spi.Writer;
import cn.com.citydo.dtx.common.spi.records.Record;
import cn.com.citydo.dtx.common.spi.records.SkipRecord;
import cn.com.citydo.dtx.common.spi.tunnels.RecordConsumer;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.order.OrderProducer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class RocketMqWriter extends Writer {

    public static class Job extends Writer.Job {

        @Override
        public List<Configuration> split(int channel) {
            List<Configuration> configurations = new ArrayList<>();
            for (int i = 0; i < channel; i++) {
                configurations.add(this.getAllConfig().clone());
            }
            return configurations;
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
        public void init() {

        }

        @Override
        public void startWrite(RecordConsumer consumer) {
            Properties properties = new Properties();
            properties.put(PropertyKeyConst.ProducerId, this.getAllConfig().getString(Keys.GROUP_ID));
            properties.put(PropertyKeyConst.AccessKey, this.getAllConfig().getString(Keys.ACCESS_KEY));
            properties.put(PropertyKeyConst.SecretKey, this.getAllConfig().getString(Keys.SECRET_KEY));
            properties.put(PropertyKeyConst.NAMESRV_ADDR, this.getAllConfig().getString(Keys.NAME_ADDR));
            OrderProducer producer = ONSFactory.createOrderProducer(properties);
            // 在发送消息前，必须调用 start 方法来启动 Producer，只需调用一次即可
            producer.start();

            Record record = null;
            while ((record = consumer.consume()) != null) {
                if (record instanceof SkipRecord) {
                    continue;
                }
                String orderId = String.valueOf(System.currentTimeMillis());
                Message msg = MessageUtil.buildMessage(
                        this.getAllConfig().getList(Keys.COLUMNS, String.class),
                        record,
                        this.getAllConfig().getString(Keys.TOPIC),
                        this.getAllConfig().getString(Keys.TAGS, "tagA"));
                // 设置代表消息的业务关键属性，请尽可能全局唯一。
                // 以方便您在无法正常收到消息情况下，可通过控制台查询消息并补发。
                // 注意：不设置也不会影响消息正常收发
                msg.setKey(orderId);
                // 分区顺序消息中区分不同分区的关键字段，Sharding Key 与普通消息的 key 是完全不同的概念。
                // 全局顺序消息，该字段可以设置为任意非空字符串。
                String shardingKey = String.valueOf(orderId);
                try {
                    SendResult sendResult = producer.send(msg, shardingKey);
                    // 发送消息，只要不抛异常就是成功
                    if (sendResult != null) {
                        System.out.println(new Date() + " Send mq message success. Topic is:" + msg.getTopic() + " msgId is: " + sendResult.getMessageId());
                    }
                } catch (Exception e) {
                    // 消息发送失败，需要进行重试处理，可重新发送这条消息或持久化这条数据进行补偿处理
                    System.out.println(new Date() + " Send mq message failed. Topic is:" + msg.getTopic());
                    e.printStackTrace();
                }
            }
            // 在应用退出前，销毁 Producer 对象
            // 注意：如果不销毁也没有问题
            producer.shutdown();
        }

        @Override
        public void destroy() {

        }
    }
}
