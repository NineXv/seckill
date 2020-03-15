package cn.ninexv.seckill.controller;

import cn.ninexv.seckill.config.JSONChange;
import cn.ninexv.seckill.pojo.Seckill;
import cn.ninexv.seckill.service.MysqlService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seckill")
public class ExecutionController {

    @Autowired
    MysqlService mysqlService;

    @GetMapping("/get/{id}")
    public Seckill get(@PathVariable("id") int seckillId){
        return mysqlService.getById(seckillId);
    }

    @GetMapping("/list")
    public String list(){
        int page = 0; //分页这一块为了偷懒就写死了
        List<Seckill> seckillList = mysqlService.getSeckillList(page);
        String s = null;
        try {
            s = JSONChange.objToJson(seckillList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return s;
    }

}
