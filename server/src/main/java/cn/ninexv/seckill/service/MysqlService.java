package cn.ninexv.seckill.service;

import cn.ninexv.seckill.dto.Exposer;
import cn.ninexv.seckill.dto.SeckillExecution;
import cn.ninexv.seckill.exception.RepeatKillException;
import cn.ninexv.seckill.exception.SeckillCloseException;
import cn.ninexv.seckill.exception.SeckillException;
import cn.ninexv.seckill.pojo.Seckill;
import cn.ninexv.seckill.pojo.SuccessKilled;

import java.util.List;

/**
 * 业务接口：站在"使用者"角度设计接口
 * 三个方面：
 * 1.方法定义粒度，
 * 2.参数，
 * 3.返回类型（return 类型/异常）
 */
public interface MysqlService {

	/**
	 * 查询所有秒杀记录
	 */
	List<Seckill> getSeckillList(int page);

	/**
	 * 查询单个秒杀记录
	 * @param seckillId
	 */
	Seckill getById(int seckillId);



	/**
	 * 执行秒杀操作
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 */
	SeckillExecution executeSeckill(int seckillId, long userPhone, String md5)
			throws SeckillException, RepeatKillException, SeckillCloseException;



	/**
	 * 执行秒杀操作by存储过程
	 */
	void executeSeckillProcedure(SuccessKilled successKilled);

}
