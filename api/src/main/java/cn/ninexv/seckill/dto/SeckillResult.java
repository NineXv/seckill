package cn.ninexv.seckill.dto;

import lombok.Data;

import java.io.Serializable;

//封装json结果
@Data
public class SeckillResult<T> implements Serializable {

	private boolean success;//判断情况是否成功

	private T data;//泛型数据

	private String error;//错误信息

	public SeckillResult(boolean success, String error) {
		this.success = success;
		this.error = error;
	}

	public SeckillResult(boolean success, T data) {
		this.success = success;
		this.data = data;
	}

}
