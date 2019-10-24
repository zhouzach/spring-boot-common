package org.rabbit.cache;

import org.rabbit.Application;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import net.sf.ehcache.Element;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)

public class CacheTester {
    @Resource
    private CacheManager cacheManager;

    @Test
    public void cacheTest() {
        // 显示所有的Cache空间
        System.out.println(StringUtils.join(cacheManager.getCacheNames(), ","));
        Cache cache = cacheManager.getCache("userCache");

//        cache.put("key", "123");
        System.out.println("缓存成功");
        String res = cache.get("key", String.class);

        System.out.println(res);

        // 获取EhCache的管理器
        org.springframework.cache.ehcache.EhCacheCacheManager cacheCacheManager = (EhCacheCacheManager) cacheManager;
        net.sf.ehcache.CacheManager ehCacheManager = cacheCacheManager.getCacheManager();
        net.sf.ehcache.Cache ehCache = ehCacheManager.getCache("userCache");

        ehCache.put(new Element("key1",2));
        ehCache.put(new Element("key2",234));
        ehCache.put(new Element("key2",34));
        ehCache.getAll(ehCache.getKeys()).forEach((k,v)->System.out.println(k + " : " + v.getObjectValue()));
    }
}
