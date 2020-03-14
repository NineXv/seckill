package cn.ninexv.seckill.dao;


import cn.ninexv.seckill.pojo.SuccessKilled;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface SuccessKilledDao {

	/**
	 * 插入购买明细，可过滤重复
	 *
	 * ignore (忽略): 当出现主键冲突的时候，不报错，但是返回0
	 *
	 * @return 插入的行数
	 */
	@Insert("INSERT ignore INTO success_killed (seckill_id, user_phone, state,create_time) VALUES (#{seckillId}, #{userPhone}, 0,#{createTime})")
	int insertSuccessKilled(@Param("seckillId") int seckillId, @Param("userPhone") long userPhone,@Param("createTime") Date createTime);

	/**
	 * 根据id查询SuccessKilled并携带秒杀产品对象实体
	 * 告诉MyBatis把结果映射到SuccessKilled同时映射seckill属性
	 * INNER JOIN: 将一个表中的行与其他表中的行进行匹配，并允许从两个表中查询包含列的行记录。
	 */
	@Select("SELECT sk.seckill_id, sk.user_phone, \n" +
			"sk.create_time, sk.state, \n" +
			"s.seckill_id \"seckill.seckill_id\",\n" +
			"s.`name` \"seckill.name\",\n" +
			"s.number \"seckill.number\",\n" +
			"s.start_time \"seckill.start_time\",\n" +
			"s.end_time \"seckill.end_time\",\n" +
			"s.create_time \"seckill.create_time\"\n" +
			"FROM success_killed sk\n" +
			"INNER JOIN seckill s ON sk.seckill_id = s.seckill_id\n" +
			"WHERE sk.seckill_id = #{seckillId} AND sk.user_phone = #{userPhone}")
	SuccessKilled queryByIdWithSeckill(@Param("seckillId") int seckillId, @Param("userPhone") long userPhone);

}
