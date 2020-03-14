package cn.ninexv.seckill.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 秒杀库存实体
 *
 */
@Data
public class Seckill implements Serializable {

	private int seckillId;

	private String name;

	private int number;

	private Date startTime;

	private Date endTime;

	private Date createTime;

}
