package cn.ninexv.seckill.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 暴露秒杀接口DTO
 */
@Data
public class Exposer implements Serializable {

	// 是否开启秒杀
	private boolean exposed;

	// 一种加密措施
	private String md5;

	// id
	private int seckillId;

	// 系统当前时间（毫秒）
	private long now;

	// 开启时间
	private long start;

	// 结束时间
	private long end;

	public Exposer(boolean exposed, String md5, int seckillId) {
		this.exposed = exposed;
		this.md5 = md5;
		this.seckillId = seckillId;
	}

	public Exposer(boolean exposed, int seckillId, long now, long start, long end) {
		this.exposed = exposed;
		this.seckillId = seckillId;
		this.now = now;
		this.start = start;
		this.end = end;
	}

	public Exposer(boolean exposed, int seckillId) {
		this.exposed = exposed;
		this.seckillId = seckillId;
	}

}
