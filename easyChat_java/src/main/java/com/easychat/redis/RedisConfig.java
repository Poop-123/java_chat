package com.easychat.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig<V> {
    @Bean("redisTemplate")
    public RedisTemplate<String,V>redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String,V> template=new RedisTemplate<String, V>();
        //连接redis
        template.setConnectionFactory(factory);
        //设置key序列化方式
        template.setKeySerializer(RedisSerializer.string());
        //设置value序列化方式
        template.setValueSerializer(RedisSerializer.json());
        //设置HashKey序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        //设置HashValue序列化方式
        template.setHashValueSerializer(RedisSerializer.json());
        //初始化bean
        template.afterPropertiesSet();
        return template;
    }
}
