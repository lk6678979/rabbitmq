package com.owp.rabbitmq.simple;

import com.owp.rabbitmq.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 简单的发送者
 * 通过默认交换机发送消息
 */
public class SenderSimple {
    /**
     * 队列名称
     */
    private static final String QUEUE = "test_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection con = null;
        Channel channel = null;
        try {
            // 获取连接
            con = ConnectionUtils.getConnection();

            // 从连接中创建通道
            channel = con.createChannel();

            //声明队列【参数说明：参数一：队列名称，参数二：是否持久化；参数三：是否独占模式，如果为true，则其他通道不能访问当前队列；参数四：消费者断开连接时是否删除队列；参数五：消息其他参数】
            channel.queueDeclare(QUEUE, false, false, false, null).getQueue();

            // 消息内容
            String msg = "哈哈哈哈哈哈哈2!";

            // 推送内容【参数说明：参数一：交换机名；参数二：队列映射的路由key，因为没有设置交换机（参数一），此处就是队列名称，参数三：消息的其他属性-路由的headers信息；参数四：消息主体】
            channel.basicPublish("", QUEUE, null, msg.getBytes());
            System.out.println("发送成功");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接
            ConnectionUtils.close(channel, con);
        }
    }
}
