package org.rabbit.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

// 集成多CacheManager
//https://www.twblogs.net/a/5c81f63fbd9eee35fc138743
//https://segmentfault.com/a/1190000013269653
@Configuration
@EnableCaching
public class CacheConfig  {

    /**
     * cacheManager名称
     */
    public interface CacheManagerName {
        /**
         * redis
         */
        String REDIS_CACHE_MANAGER = "redisCacheManager";

        /**
         * ehCache
         */
        String EHCACHE_CACHE_MAANGER = "ehCacheCacheManager";
    }
//    /**
//     *  定义 StringRedisTemplate ，指定序列号和反序列化的处理类
//     * @param factory
//     * @return
//     */
//    @Bean
//    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
//        StringRedisTemplate template = new StringRedisTemplate(factory);
//        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
//                Object.class);
//        ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        jackson2JsonRedisSerializer.setObjectMapper(om);
//        //序列化 值时使用此序列化方法
//        template.setValueSerializer(jackson2JsonRedisSerializer);
//        template.afterPropertiesSet();
//        return template;
//    }

    @Bean(CacheConfig.CacheManagerName.REDIS_CACHE_MANAGER)
    @Primary
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory).build();
//        RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
        //使用前缀
//        rcm.setUsePrefix(true);
        //缓存分割符 默认为 ":"
//        rcm.setCachePrefix(new DefaultRedisCachePrefix(":"));
        //设置缓存过期时间
        //rcm.setDefaultExpiration(60);//秒
//        return rcm;
    }

    @Bean(CacheConfig.CacheManagerName.EHCACHE_CACHE_MAANGER)
    public EhCacheCacheManager ehcacheManager() {
        EhCacheCacheManager ehCacheManager = new EhCacheCacheManager();
        return ehCacheManager;
    }

}

