package com.owp.rabbitmq.boot;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @描述: X9e数据流处理
 * @公司:
 * @作者: 刘恺
 * @版本: 1.0.0
 * @日期: 2019-03-08 17:44:25
 */
@Service
public class DemoInputStream {
    @StreamListener(DemoChannel.DEMO_INPUT)
    public void receive(Message<String> EntityMessage,
                        @Header(AmqpHeaders.CHANNEL) Channel channel,
                        @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag) {
        try {
            if (EntityMessage.getPayload() == null) {
                throw new Exception("数据有误");
            }
            System.out.println("数据：" + EntityMessage.getPayload());
            channel.basicAck(deliveryTag, false);//手动确认
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
