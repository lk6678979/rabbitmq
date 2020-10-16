### 1.导入pom
```xml
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- springboot的测试依赖jar-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
```
### 2.添加配置
```yaml
spring:
  rabbitmq:
    addresses: test01:5672,test02:5672,test03:5672
    username: admin
    password: 123456
    #虚拟机
    virtual-host: /demo
```
### 3.简单的生产者和消费者
提前在MQ中创建好交换机和队列（不建议在代码里生成，一版生产不会有这种情况）  
提前创建：
* 交换机：demo.exchange
* 队列：demo.queue
* 队列绑定交换机：demo.routingKey
#### 3.1 生产者
```java
    @Autowired
    private RabbitTemplate rabbitTemplate;  //使用RabbitTemplate,这提供了接收/发送等等方法

    @GetMapping("/sendDirectMessage")
    public String sendDirectMessage() {
        String msg = "数据" + System.currentTimeMillis();
        rabbitTemplate.convertAndSend("demo.exchange", "demo.routingKey", msg);
        return "ok";
    }
```
很简单：使用SptingBoot提供RabbitTemplate类直接操作  

#### 3.2 消费者-1
```java
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "demo.queue")
public class TopicManReceiver {
    @RabbitHandler
    public void process(String testMessage) {
        System.out.println("消费者收到消息  : " + testMessage);
    }
}
```
* 在类上添加注解@RabbitListener(queues = "demo.queue")
* 使用@RabbitHandler标记监听方法，如果希望一个队列被多个客户端消费，就在这个类里添加多个@RabbitHandlerb类

#### 3.3 消费者-2
也可以直接在方法上添加@RabbitListener注解实现监听
```yaml
@Component
public class TopicManReceiver2 {
    @RabbitListener(queues = "delayd.didi")
    public void process(String testMessage) {
        System.out.println("消费者收到消息  : " + testMessage);
    }
}
```
### 4.发送者确认模式
考虑这样一个场景：你发送了一个消息给RabbitMq，RabbitMq接收了但是存入磁盘之前服务器就挂了，消息也就丢了。为了保证消息的投递有两种解决方案，最保险的就是事务（和DB的事务没有太大的可比性）， 但是因为事务会极大的降低性能，会导致生产者和RabbitMq之间产生同步(等待确认)，这也违背了我们使用RabbitMq的初衷。所以一般很少采用，这就引入第二种方案：发送者确认模式。  
发送者确认模式是指发送方发送的消息都带有一个id，RabbitMq会将消息持久化到磁盘之后通知生产者消息已经成功投递，如果因为RabbitMq内部的错误会发送nack。注意这里的发送者和RabbitMq之间是异步的，所以相较于事务机制性能大大提高。  
### 4.2 配置文件中启动配置项
```yaml
spring:
  rabbitmq:
    #消息确认配置项
    #确认消息已发送到交换机(Exchange)
    publisher-confirms: true
    #确认消息已发送到队列(Queue)
    publisher-returns: true
```
### 4.2 配置RabbitTemplate
```yaml
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMqConfig {
    @Bean
    public RabbitTemplate createRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
        rabbitTemplate.setMandatory(true);
        /**
         * 返回数据写入是否成功的结果和原因
         * 注意：如果队列设置了最大数据限制，超过限制没有任何错误提示
         */
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("ConfirmCallback:     " + "相关数据：" + correlationData);
                System.out.println("ConfirmCallback:     " + "确认情况：" + ack);
                System.out.println("ConfirmCallback:     " + "原因：" + cause);
            }
        });

        /**
         * 在找不过交换机和routingkey才会触发
         */
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("ReturnCallback:     " + "消息：" + message);
                System.out.println("ReturnCallback:     " + "回应码：" + replyCode);
                System.out.println("ReturnCallback:     " + "回应信息：" + replyText);
                System.out.println("ReturnCallback:     " + "交换机：" + exchange);
                System.out.println("ReturnCallback:     " + "路由键：" + routingKey);
            }
        });

        return rabbitTemplate;
    }
}
```
### 4.2 发送数据时添加CorrelationData
CorrelationData里存储数据的唯一标示
```yaml
    @GetMapping("/sendDirectMessage2")
    public String sendDirectMessage2() {
        CorrelationData correlationData = new CorrelationData();
        long time = System.currentTimeMillis();
        correlationData.setId(String.valueOf(time));
        //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
        String msg = "数据" + time;
        rabbitTemplate.convertAndSend("datasyn.send", "delayt.did2i", msg, correlationData);
        return "ok";
    }
```
### 5.同步发送
将convertSend方法换成convertSendAndReceive

### 6.设置失败重发机制
```yaml
spring:
  rabbitmq:
    listener:
      simple:
        retry:
          enabled: true   # 允许消息消费失败的重试
          max-attempts: 3   # 消息最多消费次数3次
          initial-interval: 2000    # 消息多次消费的间隔2秒
```
### 6.手动提交和消费线程等配置
```yaml
  rabbitmq:
    listener:
      simple:
        acknowledge-mode: manual #消费手动确认
        concurrency: 5  #每个listener在初始化的时候设置的并发消费者的个数
        prefetch: 10  #每次从一次性从broker里面取的待消费的消息的个数
```