package cn.ninexv.seckill.dao;


import cn.ninexv.seckill.pojo.Seckill;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.mapping.StatementType;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 秒杀库存DAO接口
 * 
 * @author ninexv
 */
@Repository
public interface SeckillDao {

	/**
	 * 减库存
	 *
	 * @return 如果影响行数等于>1，表示更新的记录行数
	 */
	@Update("UPDATE seckill SET number = number - 1 WHERE seckill_id = #{seckillId} AND start_time <= #{killTime}" +
			"AND end_time >= #{killTime} AND number > 0")
	int reduceNumber(@Param("seckillId") int seckillId, @Param("killTime") Date killTime);

	/**
	 *
	 * 根据id查询秒杀对象
	 */
	@Select("SELECT seckill_id, NAME, number, start_time, end_time, create_time FROM seckill WHERE seckill_id = #{seckillId}")
	Seckill queryById(int seckillId);


	/**
	 * 根据偏移量查询秒杀商品列表
	 * ORDER BY create_time DESC : 根据创建时间反向
	 */
	@Select("SELECT seckill_id, NAME, number, start_time, end_time, create_time FROM seckill ORDER BY " +
			"create_time DESC LIMIT #{offset}, #{limit}")
	List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);


	/**
	 * 使用存储过程执行秒杀
	 */
	@Select("CALL execute_seckill(#{seckillId, jdbcType = INTEGER, mode = IN }, #{phone, jdbcType = BIGINT, mode = IN }, " +
			"#{killTime, jdbcType = TIMESTAMP, mode = IN }, #{result, jdbcType = INTEGER, mode = OUT });")
	@Options(statementType = StatementType.CALLABLE)
	void killByProcedure(Map<String, Object> paramMap);

}