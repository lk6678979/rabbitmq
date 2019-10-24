package com.owp.rabbitmq.exchange.direct;

import com.owp.rabbitmq.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 使用直连交换机，使用mandatory特性，如果发送到交换机的信息路由不到任何队列，则消息会返回给生产者
 * 直连型交换机（direct exchange）是根据消息携带的路由键（routing key）将消息投递给对应队列的，步骤如下：
 * <p>
 * 1.将一个队列绑定到某个交换机上，同时赋予该绑定一个路由键（routing key）
 * 2.当一个携带着路由值为R的消息被发送给直连交换机时，交换机会把它路由给绑定值同样为R的队列。
 */
public class SenderWithDirectExchangeMandatory {
    /**
     * 队列名称
     */
    private static final String QUEUE = "order";

    /**
     * 交换机名称
     */
    private static final String EXCHANGE = "work_order";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Connection con = null;
        Channel channel = null;
        try {
            // 获取连接
            con = ConnectionUtils.getConnection();

            // 从连接中创建通道
            channel = con.createChannel();
            channel.basicQos(1);
            channel.basicPublish(EXCHANGE, QUEUE, true, MessageProperties.PERSISTENT_TEXT_PLAIN, "===mandatory===".getBytes());
            channel.addReturnListener(new ReturnListener() {
                public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties basicProperties, byte[] body) throws IOException {
                    String message = new String(body);
                    System.out.println("Basic.return返回的结果是：" + message);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接
            Thread.sleep(2000);
            ConnectionUtils.close(channel, con);
        }
    }
}
