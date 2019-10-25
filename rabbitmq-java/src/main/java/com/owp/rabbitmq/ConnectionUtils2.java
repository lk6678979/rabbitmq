package com.owp.rabbitmq;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 集群模式，多个host
 */
public class ConnectionUtils2 {
    /**
     * 获取连接
     *
     * @return
     * @throws IOException
     * @throws TimeoutException
     */
    public static Connection getConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        // 设置服务地址
//        factory.setHost("47.106.135.182");
        Address[] addrs = new Address[]{new Address("kafka01",5672),new Address("kafka02",5672),new Address("kafka03",5672)};
        // 端口
//        factory.setPort(5672);
        // vhost
        /**
         * 像mysql有数据库的概念并且可以指定用户对库和表等操作的权限。那RabbitMQ呢？RabbitMQ也有类似的权限管理。
         * 在RabbitMQ中可以虚拟消息服务器VirtualHost，每个VirtualHost相当月一个相对独立的RabbitMQ服务器，
         * 每个VirtualHost之间是相互隔离的。exchange、queue、message不能互通，早操作界面【admin】下的【Add a new virtual host】可以添加
         * 需要先在mq中设置了虚拟消息服务器才能使用
         */
        factory.setVirtualHost("/vmsdms");
        // 用户名
        factory.setUsername("sziov");
        // 密码
        factory.setPassword("sziov");

        return factory.newConnection(addrs);
    }

    /**
     * 关闭连接
     *
     * @param channel
     * @param con
     */
    public static void close(Channel channel, Connection con) {
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
