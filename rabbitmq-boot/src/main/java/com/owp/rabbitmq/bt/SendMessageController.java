package com.owp.rabbitmq.bt;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SendMessageController {
    @Autowired
    private RabbitTemplate rabbitTemplate;  //使用RabbitTemplate,这提供了接收/发送等等方法

    @GetMapping("/sendDirectMessage")
    public String sendDirectMessage() {
        String msg = "数据" + System.currentTimeMillis();
        rabbitTemplate.convertAndSend("datasyn.send", "delayt.didi", msg);
        return "ok";
    }


    @GetMapping("/sendDirectMessage2")
    public String sendDirectMessage2() {
        CorrelationData correlationData = new CorrelationData();
        long time = System.currentTimeMillis();
        correlationData.setId(String.valueOf(time));
        //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
        String msg = "数据" + time;
        rabbitTemplate.convertAndSend("datasyn.send", "delayt.didi", msg, correlationData);
        return "ok";
    }

    @GetMapping("/sendDirectMessage3")
    public String sendDirectMessage3() {
        CorrelationData correlationData = new CorrelationData();
        long time = System.currentTimeMillis();
        correlationData.setId(String.valueOf(time));
        //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
        String msg = "数据" + time;
        Object  message = rabbitTemplate.convertSendAndReceive("datasyn.send", "delayt.didi", msg, correlationData);
        return "ok";
    }
}