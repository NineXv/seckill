package cn.ninexv.seckill.dto;


import cn.ninexv.seckill.enums.SeckillStateEnum;
import cn.ninexv.seckill.pojo.SuccessKilled;
import lombok.Data;

import java.io.Serializable;

/**
 * 封装秒杀执行后结果
 */
@Data
public class SeckillExecution implements Serializable {

	private int seckillId;

	// 秒杀执行结果状态
	private int state;

	// 状态标识
	private String stateInfo;

	// 秒杀成功对象
	private SuccessKilled successKilled;

	public SeckillExecution(int seckillId, SeckillStateEnum stateEnum, SuccessKilled successKilled) {
		this.seckillId = seckillId;
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.successKilled = successKilled;
	}

	public SeckillExecution(int seckillId, SeckillStateEnum stateEnum) {
		this.seckillId = seckillId;
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
	}



}
