//package com.owp.rabbitmq.bt;
//
//import org.springframework.amqp.rabbit.annotation.RabbitHandler;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//
//@Component
//@RabbitListener(queues = "delayd.didi")
//public class TopicManReceiver {
//    @RabbitHandler
//    public void process(String testMessage) {
//        System.out.println("消费者收到消息  : " + testMessage);
//    }
//
//}
