package cn.ninexv.seckill.service;

import cn.ninexv.seckill.dto.Exposer;
import cn.ninexv.seckill.dto.SeckillExecution;
import cn.ninexv.seckill.pojo.Seckill;

/**
 * 初衷是为了尽可能让redis来返回请求，除非查找不到，才会调用MysqlService
 */
public interface RedisSeckill {
    /**
     * 成功减少redis库存的话就发送消息队列
     */
    SeckillExecution executionByRedis(int seckillId, long userPhone, String md5);

    /**
     * 从redis中根据id拿seckill，拿不到再去mysql中拿
     */
    public Seckill getById(int seckillId);

    /**
     * 秒杀开启时输出秒杀接口地址，否则输出系统时间和秒杀时间
     * @param seckillId
     */
    public Exposer exportSeckillUrl(int seckillId);

}
