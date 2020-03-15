package cn.ninexv.seckill.service.impl;

import cn.ninexv.seckill.Utils.MD5Utils;
import cn.ninexv.seckill.cache.RabbitMqDao;
import cn.ninexv.seckill.cache.RedisDao;
import cn.ninexv.seckill.dto.Exposer;
import cn.ninexv.seckill.dto.SeckillExecution;
import cn.ninexv.seckill.enums.SeckillStateEnum;
import cn.ninexv.seckill.pojo.Seckill;
import cn.ninexv.seckill.pojo.SuccessKilled;

import cn.ninexv.seckill.service.RedisSeckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
public class RedisSeckillImpl implements RedisSeckill {

    @Autowired
    RedisDao redisDao;
    @Autowired
    RabbitMqDao rabbitMqDao;
    @Autowired
    private RestTemplate restTemplate;


    private static final String REST_URL_PREFIX="http://SERVER";

    @Override
    public Exposer exportSeckillUrl(int seckillId) {
        // 优化点：缓存优化：超时的基础上维护一致性
        // 1.访问redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            // 2.访问数据库
            seckill = restTemplate.getForObject(REST_URL_PREFIX + "/seckill/get/" + seckillId, Seckill.class);
            if (seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                // 3.访问redis
                redisDao.putSeckill(seckill);
            }
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        // 系统当前时间
        Date nowTime = new Date();
        //进行比对，判断时间是否到了，如果时间到了或者超了，就返回false
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        // 转化特定字符串的过程，不可逆
        String md5 = MD5Utils.getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }


    @Override
    public SeckillExecution executionByRedis(int seckillId, long userPhone, String md5){
        //先判断md5是否正确
        if (md5 == null || !md5.equals(MD5Utils.getMD5(seckillId))) {
            return new SeckillExecution(seckillId, SeckillStateEnum.DATA_REWRITE);
        }
        //从redis的商品list中弹出一个
        Seckill seckill = redisDao.popSeckill(seckillId);
        //如果等于null，说明已经没有了，返回秒杀结束
        if (seckill == null){
            return new SeckillExecution(seckillId,SeckillStateEnum.END);
        }
        //封装一个成功秒杀的对象，通过发送至消息队列
        SuccessKilled suk = new SuccessKilled();
        suk.setSeckillId(seckillId);
        suk.setUserPhone(userPhone);
        rabbitMqDao.send(suk);
        return new SeckillExecution(seckillId,SeckillStateEnum.SUCCESS);
    }



    @Override
    public Seckill getById(int seckillId) {
        Seckill s = redisDao.getSeckill(seckillId);
        if (s == null){
            return restTemplate.getForObject(REST_URL_PREFIX + "/seckill/get/" + seckillId, Seckill.class);
        }
        return s;
    }
}
