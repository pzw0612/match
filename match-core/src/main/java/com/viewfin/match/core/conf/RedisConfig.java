package com.viewfin.match.core.conf;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisSerializer fastJson2JsonRedisSerializer() {
        return new FastJson2JsonRedisSerializer<Object>(Object.class);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory, RedisSerializer fastJson2JsonRedisSerializer) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(factory);


        redisTemplate.setValueSerializer(fastJson2JsonRedisSerializer);
        //redisTemplate.setDefaultSerializer(fastJson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(fastJson2JsonRedisSerializer);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 实例化 RedisTemplate 对象
     *
     * @return
     */
//    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
//    @Bean(name = "redisTemplate")
//    public RedisTemplate<String, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        RedisTemplate redisTemplate = new RedisTemplate();
//        redisTemplate.setConnectionFactory(redisConnectionFactory);
//
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//
//        redisTemplate.afterPropertiesSet();
//        return redisTemplate;
//    }

    /**
     * 实例化 HashOperations 对象,可以使用 Hash 类型操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public HashOperations<String, String, Object> hashOperations(@Qualifier("redisTemplate") RedisTemplate<String, ?> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    /**
     * 实例化 ValueOperations 对象,可以使用 String 操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public <T> ValueOperations<String, T> valueOperations(@Qualifier("redisTemplate") RedisTemplate<String, ?> redisTemplate) {
        return (ValueOperations<String, T>) redisTemplate.opsForValue();
    }

    /**
     * 实例化 ListOperations 对象,可以使用 List 操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public <T> ListOperations<String, T> listOperations(@Qualifier("redisTemplate") RedisTemplate<String, ?> redisTemplate) {
        return (ListOperations<String, T>) redisTemplate.opsForList();
    }

    /**
     * 实例化 SetOperations 对象,可以使用 Set 操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public <T> SetOperations<String, T> setOperations(@Qualifier("redisTemplate") RedisTemplate<String, ?> redisTemplate) {
        return (SetOperations<String, T>) redisTemplate.opsForSet();
    }

    /**
     * 实例化 ZSetOperations 对象,可以使用 ZSet 操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public <T> ZSetOperations<String, T> zSetOperations(@Qualifier("redisTemplate") RedisTemplate<String, ?> redisTemplate) {
        return (ZSetOperations<String, T>) redisTemplate.opsForZSet();
    }
}
