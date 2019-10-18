package com.owp.rabbitmq.simple;

import com.owp.rabbitmq.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RecverSimpleOneMessage {

    /**
     * 队列名称，和生产者的队列名称必须保持一致
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
            // 声明队列【参数说明：参数一：队列名称，参数二：是否持久化；参数三：是否独占模式；参数四：消费者断开连接时是否删除队列；参数五：消息其他参数】
            channel.queueDeclare(QUEUE, false, false, false, null);

            GetResponse resp = channel.basicGet(QUEUE, false);
            if (resp == null) {
                System.out.println("没有消息");
                return;
            }
            String message = new String(resp.getBody(), "UTF-8");
            channel.basicAck(resp.getEnvelope().getDeliveryTag(), false); // 消息确认
            System.out.println("result:" + message);
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
