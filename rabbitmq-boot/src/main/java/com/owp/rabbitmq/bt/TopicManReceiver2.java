package com.owp.rabbitmq.bt;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class TopicManReceiver2 {
//    @RabbitListener(queues = "delayd.didi")
//    public void process(String testMessage,@Headers Map<String, String> head) {
//        System.out.println("消费者收到消息  : " + testMessage);
//    }


    @RabbitListener(queues = "delayd.didi")
    public void process(String testMessage, @Header(name = "amqp_deliveryTag") long deliveryTag,
                        @Header("amqp_redelivered") boolean redelivered,
                        @Headers Map<String, String> head, Channel channel) throws IOException {
        try {
            System.out.println("消费者收到消息  : " + testMessage);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            //这一步千万不要忘记，不会会导致消息未确认，消息到达连接的qos之后便不能再接收新消息
            //一般重试肯定的有次数，这里简单的根据是否已经重发过来来决定重发。第二个参数表示是否重新分发
            channel.basicReject(deliveryTag, !redelivered);
            //这个方法我知道的是比上面多一个批量确认的参数
            // channel.basicNack(deliveryTag, false,!redelivered);
        }
    }
}
