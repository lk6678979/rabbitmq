package com.owp.rabbitmq.exchange.direct;

import com.owp.rabbitmq.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class CreateExtendAndQueue {

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

            //声明交换机【参数说明：
            // 参数一【exchange】：交换机名称
            // 参数二【type】：交换机类型，常见的有direct,fanout,topic等,这里是direct：直接交换机；
            // 参数三【durable】：设置是否持久化。durable设置为true时表示持久化，反之非持久化.持久化可以将交换器存入磁盘，在服务器重启的时候不会丢失相关信息
            // 参数四【autoDelete】：设置是否自动删除。autoDelete设置为true时，则表示自动删除。自动删除的前提是至少有一个队列或者交换器与这个交换器绑定，之后，所有与这个交换器绑定的队列或者交换器都与此解绑。不能错误的理解—当与此交换器连接的客户端都断开连接时，RabbitMq会自动删除本交换器
            // 参数五【internal】：其它一些结构化的参数，比如：alternate-exchange
            channel.exchangeDeclare(EXCHANGE, "direct", true, false, null);

            // 队列参数
//            Map<String, Object> params = new HashMap();
//            params.put("x-message-ttl", (60 * 1000));// 消息过期时间

            //声明队列【参数说明：参数一：队列名称，参数二：是否持久化；参数三：是否独占模式，如果为true，则其他通道不能访问当前队列；参数四：消费者断开连接时是否删除队列；参数五：消息其他参数】
            channel.queueDeclare(QUEUE, true, false, false, null).getQueue();

            //将交换器与队列通过路由键绑定,第一个参数：队列名，第二个参数：交换机名称，第三个参数：队列在交换机中的路由映射
            channel.queueBind(QUEUE, EXCHANGE, QUEUE);

            System.out.println("操作成功");
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
