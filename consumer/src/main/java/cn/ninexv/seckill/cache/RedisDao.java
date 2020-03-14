package cn.ninexv.seckill.cache;

import cn.ninexv.seckill.pojo.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisDao {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

//	@Autowired
//	StringRedisTemplate stringRedisTemplate;
	@Autowired
	RedisTemplate redisTemplate;


	public Seckill getSeckill(int seckillId) {
		// redis操作逻辑
		try {
			Seckill s = (Seckill) redisTemplate.opsForValue().get(seckillId+"ex");
			return s;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	public String putSeckill(Seckill seckill) {
		try {
			redisTemplate.opsForValue().set(seckill.getSeckillId()+"ex",seckill);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	public Seckill popSeckill(int id){
		Seckill sk = (Seckill) redisTemplate.opsForList().leftPop(id);
		return sk;
	}
	
	public Long pushSeckill(Seckill sk){
		return redisTemplate.opsForList().leftPush(sk.getSeckillId(), sk);
	}

	public void removeAll(int id){
		redisTemplate.opsForList().trim(id,1,0);
	}
}
