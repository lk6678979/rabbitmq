package com.owp.rabbitmq.boot;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface DemoChannel {

    String DEMO_INPUT = "demo_input";

    String DEMO_OUTPUT = "demo_output";

    /**
     * 接收国标信息
     */
    @Input(DEMO_INPUT)
    SubscribableChannel recieveMessage();

    @Output(DEMO_OUTPUT)
    MessageChannel sendMessage();

}
