package com.ggr.serviceredis.controller;

import com.ggr.serviceredis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@Slf4j
public class IndexController {

    @Autowired
    RedisService redisService;

    @RequestMapping("/getString")
    public String getCacheValue(String key){
        Optional<String> resultSlot = redisService.doInJedis(jedis -> jedis.get(key));
        String result = resultSlot.orElse("-ERROR: 该key不存在");
        if(resultSlot.isPresent()){
            log.info("该key={},value={}",result);
        }
        log.info("该key不存在");
        return result;
    }

    @RequestMapping("/setString")
    public void setCacheValue(String key,String value){
        Optional<String> resultSlot = redisService.doInJedis(jedis -> jedis.set(key, value));
        if(resultSlot.isPresent()){
            log.info("有新数据加入了缓存key={},value={}",key,value);
        }
        log.info("添加失败");
    }
}
