package com.zjh.util.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author:jinhui.zhao
 * @description:
 * @date: created in 下午2:11 2018/10/25
 */
public class CacheManagerConfig {

    @Bean
    public CacheManager createCacheManager(@Autowired RedisTemplate redisTemplate) {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        List<Cache> caches = new ArrayList<>();
        caches.add(new MerchantRedisCache("5minute",5L,TimeUnit.MINUTES, MerchantRedisCache.CachePrefixEnum.MC5M.getPrefix(),redisTemplate));
        caches.add(new MerchantRedisCache("12hour",12L,TimeUnit.HOURS,MerchantRedisCache.CachePrefixEnum.MC12H.getPrefix(),redisTemplate));
        cacheManager.setCaches(caches);
        return cacheManager;
    }

}
