package com.owp.rabbitmq.exchange.direct;

import com.owp.rabbitmq.ConnectionUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.Date;
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
            channel.exchangeDeclare(EXCHANGE, "direct" , true , false , null) ;

            // 队列参数
//            Map<String, Object> params = new HashMap();
//            params.put("x-message-ttl", (60 * 1000));// 消息过期时间

            //声明队列【参数说明：参数一：队列名称，参数二：是否持久化；参数三：是否独占模式，如果为true，则其他通道不能访问当前队列；参数四：消费者断开连接时是否删除队列；参数五：消息其他参数】
            channel.queueDeclare(QUEUE, true, false, false, null).getQueue();

            //将交换器与队列通过路由键绑定,第一个参数：队列名，第二个参数：交换机名称，第三个参数：队列在交换机中的路由映射
            channel.queueBind(QUEUE, EXCHANGE, QUEUE);

//            AMQP.BasicProperties PERSISTENT_TEXT_PLAIN = new AMQP.BasicProperties("text/plain", (String)null, (Map)null, 2, 0, (String)null, (String)null, (String)null, (String)null, (Date)null, (String)null, (String)null, (String)null, (String)null);

            // 消息内容
            String msg = "哈哈哈哈哈哈哈2!";
            // 推送内容【参数说明：参数一：交换机名；参数二：队列映射的路由key，因为没有设置交换机（参数一），此处就是队列名称，参数三：消息的其他属性-路由的headers信息；参数四：消息主体】
            channel.basicPublish(EXCHANGE, QUEUE, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
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
