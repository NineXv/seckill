package cn.ninexv.seckill.service.impl;


import cn.ninexv.seckill.Utils.MD5Utils;
import cn.ninexv.seckill.dao.SeckillDao;
import cn.ninexv.seckill.dao.SuccessKilledDao;
import cn.ninexv.seckill.dto.SeckillExecution;
import cn.ninexv.seckill.enums.SeckillStateEnum;
import cn.ninexv.seckill.exception.RepeatKillException;
import cn.ninexv.seckill.exception.SeckillCloseException;
import cn.ninexv.seckill.exception.SeckillException;
import cn.ninexv.seckill.pojo.Seckill;
import cn.ninexv.seckill.pojo.SuccessKilled;
import cn.ninexv.seckill.service.MysqlService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MysqlServiceImpl implements MysqlService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Override
    public List<Seckill> getSeckillList(int page) {
        return seckillDao.queryAll(page, page+5);
    }

    @Override
    public Seckill getById(int seckillId) {
        return seckillDao.queryById(seckillId);
    }


    /**
     * 被监听的方法的返回值必须是void
     * @param suk
     */
    @Override
    @RabbitListener(queues = "seckill") //监听seckill消息队列
   public void executeSeckillProcedure (SuccessKilled suk){
        int seckillId = suk.getSeckillId();
        long userPhone = suk.getUserPhone();
        Date killTime = new Date();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("result", -2);
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        // 执行存储过程，result被赋值
        try {
            seckillDao.killByProcedure(map);
            // 获取result
            int result = (int) map.get("result");
            if (result == 1) {
                SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
//                return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, sk);
//           } else { 最初没有使用消息队列，所以此方法应该可以返回是否成功
//                return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
//            return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
        }
    }


    @Override
    @Transactional
    /**
     * 方法已废弃，项目里新的执行秒杀使用的是mysql存储过程，请将consumer模块下的sql包中的seckill.sql执行后操作
     */
    public SeckillExecution executeSeckill ( int seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {
        if (md5 == null || !md5.equals(MD5Utils.getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");//秒杀数据被重写了
        }
        // 执行秒杀逻辑：减库存 + 记录购买行为
        // (此处将顺序调换，达到优化的目的)
        //优化详解
        //原来执行的流程
        //update(发送在mysql网络时间+gc时间） + insert(发送在mysql网络时间+gc时间)
        //
        //因为update同一行会导致行级锁，而insert是可以并行执行的。
        //1.如果先update, update在前面会加锁
        //锁 + update(发送在mysql网络时间+gc时间） + insert(发送在mysql网络时间+gc时间) + 提交锁
        //其实的线程就要等，这个锁提交才能执行。
        //
        //2.如果先insert,
        //insert(发送在mysql网络时间+gc时间） +  锁+ update(发送在mysql网络时间+gc时间) + 提交锁
        //其实的线程可以并发insert. 这样子会减少锁的时长
        Date now = new Date();
        try {
            // 记录购买行为
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone,new Date());
            // 唯一：seckillId,userPhone
            if (insertCount <= 0) {
                // 重复秒杀
                throw new RepeatKillException("seckill repeated");
            } else {
                // 减库存，热点商品竞争
                int updateCount = seckillDao.reduceNumber(seckillId, now);
                if (updateCount <= 0) {
                    // 没有更新到记录 rollback
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    // 秒杀成功 commit
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
        }  catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 所有编译期异常转换为运行期异常
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }

}

