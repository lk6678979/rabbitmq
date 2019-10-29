package com.owp.rabbitmq.exchange.direct;

import com.owp.rabbitmq.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RecverSimple {

    /**
     * 队列名称
     */
    private static final String QUEUE = "order";

    /**
     * 交换机名称
     */
    private static final String EXCHANGE = "work_order";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection con = null;
        Channel channel = null;
        try {
            // 获取连接
            con = ConnectionUtils.getConnection();
            // 从连接中创建通道
            channel = con.createChannel();
            channel.basicQos(64); //设置客户端最多接收未被 ack 的消息的个数

            //以下的defaultconsumer实现了consumer这个接口,这个接口被用来缓冲服务器推送过来的信息
            //一开始的set up和刚刚的send.java里的相似:1.打开一个连接,2.声明一个队列（这个队列名要和刚刚的队列名相同）
            //注意:我们在这里声明队列,因为我们可能在生产者之前开始消费
            //我们告诉服务器从队列向我们传送消息,既然它会异步传送,我们以对象的形式提供一个回调,来缓冲这些消息,直到我们准备使用它们,这正是defaultconsumer做的事情
            final Channel finalChannel = channel;
            Consumer consumer = new DefaultConsumer(finalChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println("接收消息：'" + message + "'");
                    finalChannel.basicAck(envelope.getDeliveryTag(), false); // 手动确认消息【参数说明：参数一：该消息的index；参数二：是否批量应答，true批量确认小于当前id的消息】
                }
            };
            // 监听队列,参数1：队列名称，参数2：是否自动提交，参数3：消费者类
            String result = channel.basicConsume(QUEUE, true, "consumerTag", consumer);
            System.out.println("result:" + result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接(关闭了会停止监听)
//            ConnectionUtils.close(channel, con);
        }
    }
}
