package cn.ninexv.seckill.cache;

import cn.ninexv.seckill.enums.SeckillStateEnum;
import cn.ninexv.seckill.pojo.SuccessKilled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class RabbitMqDao {
    @Autowired
    RabbitTemplate rabbitTemplate;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 将数据发送到消息队列
     */
    public int send(SuccessKilled successKilled){
        try {
            rabbitTemplate.convertAndSend(successKilled);
            return SeckillStateEnum.SUCCESS.getState();
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return SeckillStateEnum.INNER_ERROR.getState();
    }

    /**
     * 从消息队列中接收消息
     * @return
     */
    public SuccessKilled receive(){
        try {
            SuccessKilled successKilled = (SuccessKilled) rabbitTemplate.receiveAndConvert("seckill");
            return successKilled;
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return null;
    }
}
