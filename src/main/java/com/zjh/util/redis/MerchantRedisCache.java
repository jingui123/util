package com.zjh.util.redis;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 自定义redis
 * @author:jinhui.zhao
 * @description:
 * @date: created in 下午10:52 2018/6/20
 */
public class MerchantRedisCache implements Cache {


    private static RedisTemplate redisTemplate;

    private String name;

    private Long existTime;

    private TimeUnit timeUnit;

    private  String prefix;

    public MerchantRedisCache(String name, Long existTime, TimeUnit timeUnit, String prefix, RedisTemplate redisTemplate) {
        this.name = name;
        this.existTime = existTime;
        this.timeUnit = timeUnit;
        this.prefix = prefix;
        this.redisTemplate = redisTemplate;
    }
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this.redisTemplate;
    }

    @Override
    public ValueWrapper get(Object o) {
        if(!redisTemplate.hasKey(prefix+o.toString())){
            return null;
        }
        return new SimpleValueWrapper(redisTemplate.opsForValue().get(prefix+o.toString()));

    }

    @Override
    public <T> T get(Object o, Class<T> aClass) {
        return (T)redisTemplate.opsForValue().get(prefix+o.toString());
    }

    @Override
    public <T> T get(Object o, Callable<T> callable) {

        Object  resultObject= redisTemplate.opsForValue().get(prefix+o.toString());

        if(resultObject==null){
            try {
                return callable.call();
            }catch (Exception e){
                //throw  new RuntimeException("获取缓存信息失败！o="+ JSON.toJSONString(o));
            }
        }
        return (T)o;
    }

    @Override
    public void put(Object o, Object o1) {
        redisTemplate.opsForValue().set(prefix+o.toString(),o1,existTime,timeUnit);

    }

    @Override
    public ValueWrapper putIfAbsent(Object o, Object o1) {
        Boolean isAbsent = redisTemplate.opsForValue().setIfAbsent(prefix+o.toString(),o1);
        if(isAbsent){
            return new SimpleValueWrapper(o1);
        }
        return new SimpleValueWrapper(redisTemplate.opsForValue().get(prefix+o.toString()));

    }

    @Override
    public void evict(Object o) {
        redisTemplate.delete(prefix+o.toString());
    }

    @Override
    public void clear() {
        redisTemplate.delete(prefix+"*");
    }

    public static enum CachePrefixEnum{
        MC5M("MC5M_"),//5分钟
        MC12H("MC12H_");//12小时

        private String prefix = "";

        CachePrefixEnum(String prefix){
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }
    }
}
