package cn.ninexv.seckill.controller;

import cn.ninexv.seckill.config.JSONChange;
import cn.ninexv.seckill.dto.Exposer;
import cn.ninexv.seckill.dto.SeckillExecution;
import cn.ninexv.seckill.dto.SeckillResult;
import cn.ninexv.seckill.enums.SeckillStateEnum;
import cn.ninexv.seckill.exception.RepeatKillException;
import cn.ninexv.seckill.exception.SeckillCloseException;
import cn.ninexv.seckill.pojo.Seckill;
import cn.ninexv.seckill.service.RedisSeckill;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/seckill") // url:/模块/资源/{id}/细分 /seckill/list
public class SeckillController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisSeckill redisSeckill;

    @Autowired
    RestTemplate restTemplate;

    private static final String REST_URL_PREFIX="http://SERVER";

    /**
     * 这个方法其实挺尴尬的。。
     * 真正的秒杀应该是将静态资源放在CDN上
     * 不过我在更新redis缓存的时候也会用到该方法
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        // 获取列表页
        String lists = restTemplate.getForObject(REST_URL_PREFIX + "/seckill/list", String.class);
        ObjectMapper mapper = new ObjectMapper();
        List<Seckill> seckills = new ArrayList<>();
        try {
            seckills = mapper.readValue(lists,new TypeReference<List<Seckill>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        model.addAttribute("list", seckills);
        // list.jsp + model = ModelAndView
        return "list";
    }

    /**
     * 显示详情页面
     */
    @GetMapping(value = "/{seckillId}/detail")
    public String detail(@PathVariable("seckillId") int seckillId, Model model) {
        System.out.println("接收到"+seckillId);//todo
        if (seckillId == 0) {
            return "redirect:/seckill/list";
        }
        Seckill seckill = redisSeckill.getById(seckillId);
        if (seckill == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    // ajax接收json数据，传递暴露的接口
    @RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.POST, produces = {
            "application/json; charset=utf-8" })
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") int seckillId) {
        SeckillResult<Exposer> result;
        try {
            //拿id所对应的url接口
            Exposer exposer = redisSeckill.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);
            System.out.println(exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }
        return result;
    }


    /**
     * 秒杀的执行
     */
    @RequestMapping(value = "/{seckillId}/{md5}/execution", method = RequestMethod.POST, produces = {
            "application/json; charset=utf-8" })
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") int seckillId,
                                                   @PathVariable("md5") String md5,
                                                   @CookieValue(value = "killPhone", required = false) Long phone) {
        if (phone == null) {
            return new SeckillResult<>(false, "未注册");
        }
        try {
            // 存储过程调用
            SeckillExecution execution = redisSeckill.executionByRedis(seckillId, phone, md5);
//            SeckillExecution execution = seckillService.executeSeckill(seckillId, phone, md5);//优化后，该方法已不用
            return new SeckillResult<SeckillExecution>(true, execution);
        } catch (RepeatKillException e) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true, execution);
        } catch (SeckillCloseException e) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.END);
            return new SeckillResult<SeckillExecution>(true, execution);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true, execution);
        }
    }


    /**
     * 拿到时间
     * @return
     */
    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time() {
        Date now = new Date();
        SeckillResult<Long> longSeckillResult = new SeckillResult<>(true, now.getTime());
        return longSeckillResult;
    }

}
