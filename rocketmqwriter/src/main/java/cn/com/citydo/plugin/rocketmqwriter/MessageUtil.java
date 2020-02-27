package cn.com.citydo.plugin.rocketmqwriter;

import cn.com.citydo.dtx.common.spi.records.Record;
import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageUtil {
    public static Message buildMessage(List<String> columnNames, Record record, String topic, String tag) {
        Map<String, String> data = new HashMap<>();
        for (int i = 0; i < record.getColumnNumber(); i++) {
            data.put(columnNames.get(i), record.getColumn(i).asString());
        }
        Message message = new Message(
                // Message 所属的 Topic
                topic,
                // Message Tag，可理解为 Gmail 中的标签，对消息进行再归类，方便 Consumer 指定过滤条件在消息队列 RocketMQ 版的服务器过滤
                tag,
                // Message Body 可以是任何二进制形式的数据，消息队列 RocketMQ 版不做任何干预，需要 Producer 与 Consumer 协商好一致的序列化和反序列化方式
                JSON.toJSONString(data).getBytes()
        );
        return message;
    }
}
