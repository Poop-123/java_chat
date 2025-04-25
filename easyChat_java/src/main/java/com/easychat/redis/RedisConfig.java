package com.easychat.redis;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig<V> {

    private static final Logger logger= LoggerFactory.getLogger(RedisConfig.class);
    @Value("${spring.redis.host:}")
    private String redisHost;
    @Value("${spring.redis.port:}")
    private Integer redisPort;
    //定义RedisTemplate
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
    //定义redissonClient
    @Bean(name="redissonClient",destroyMethod = "shutdown")
    public RedissonClient redissonClient(){
        try{
            Config config=new Config();
            config.useSingleServer().setAddress("redis://"+redisHost+":"+redisPort);
            return Redisson.create(config);
        }catch (Exception e){
            logger.info("redis配置错误，请检查redis配置");
        }
        return null;
    }

}
