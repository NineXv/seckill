package cn.ninexv.seckill.config;


import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRabbitMqConfig {

    //设置消息发送所使用的序列化的转化器，此处配置Jackson2JsonMessageConverter，就可以将对象转换成json数据了
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
