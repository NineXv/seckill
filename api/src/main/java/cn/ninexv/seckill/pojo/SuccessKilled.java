package cn.ninexv.seckill.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 成功秒杀实体
 */
@Data
public class SuccessKilled implements Serializable {

	private int seckillId;

	private long userPhone;

	private short state;

	private Date creteTime;

	// 多对一的复合属性
	private Seckill seckill;


}
