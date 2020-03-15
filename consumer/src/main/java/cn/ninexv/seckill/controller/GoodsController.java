package cn.ninexv.seckill.controller;

import cn.ninexv.seckill.cache.RedisDao;
import cn.ninexv.seckill.pojo.Seckill;
import cn.ninexv.seckill.service.RedisSeckill;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用来更新redis仓库的
 */
@Controller
@RequestMapping("/update")
public class GoodsController {
    @Autowired
    RedisSeckill redisSeckill;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    RedisDao redisDao;

    private static final String REST_URL_PREFIX="http://SERVER";

    /**
     * 更新redis库存
     */
    @GetMapping("/update")
    public String updateList(Model model){
        // 获取列表页
        String lists = restTemplate.getForObject(REST_URL_PREFIX + "/seckill/list", String.class);
        List<Seckill> seckills = new ArrayList<>();
        try {
            //接收并将json转化为list
            seckills = objectMapper.readValue(lists,new TypeReference<List<Seckill>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i=0;i<seckills.size();i++){
            //清空原本的redis缓存
            redisDao.removeAll(seckills.get(i).getSeckillId());
            for (int j=0;j<seckills.get(i).getNumber();j++){
                //将新的库存数目加进去
                redisDao.pushSeckill(seckills.get(i));
            }
        }
        model.addAttribute("list", seckills);
        return "update";
    }
}
