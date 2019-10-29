package com.owp.rabbitmq.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1")
@Validated
public class DemoController {

    @Autowired
    private DemoChannel demoChannel;

    @GetMapping("private/sendMsg/{msg}")
    public String selectQbReal(@PathVariable(name = "msg") String msg) {
        Map<String, String> mapInfo = new HashMap<>();
        mapInfo.put("age","1");
        mapInfo.put("name","哈哈哈");
        mapInfo.put("msg",msg);
        demoChannel.sendMessage().send(MessageBuilder.withPayload(mapInfo).build());
        return "发送成功";
    }
}
