package com.owp.rabbitmq.exchange.direct;

import com.owp.rabbitmq.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 使用直连交换机
 * 直连型交换机（direct exchange）是根据消息携带的路由键（routing key）将消息投递给对应队列的，步骤如下：
 * <p>
 * 1.将一个队列绑定到某个交换机上，同时赋予该绑定一个路由键（routing key）
 * 2.当一个携带着路由值为R的消息被发送给直连交换机时，交换机会把它路由给绑定值同样为R的队列。
 */
public class SenderWithDirectExchange {
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

            //声明交换机【参数说明：参数一：交换机名称，参数二：交换机类型，这里是direct：直接交换机；w参数三：是否独占模式，如果为true，则其他通道不能访问当前交换机；参数四：消费者断开连接时是否删除队列；参数五：消息其他参数】
            channel.exchangeDeclare("direct_exchange_demo", " direct" , true , false , null) ;


            // 队列参数
            Map<String, Object> params = new HashMap();
            params.put("x-message-ttl", 60 * 1000);// 消息过期时间

            //声明队列【参数说明：参数一：队列名称，参数二：是否持久化；参数三：是否独占模式，如果为true，则其他通道不能访问当前队列；参数四：消费者断开连接时是否删除队列；参数五：消息其他参数】
            channel.queueDeclare(QUEUE, false, false, false, params).getQueue();

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
