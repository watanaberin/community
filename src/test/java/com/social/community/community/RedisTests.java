package com.social.community.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings(){
        String redisKey ="test:count";
        redisTemplate.opsForValue().set(redisKey,1);

        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));

    }
    @Test
    public void testHash(){
        String redisKey ="test:h";
        redisTemplate.opsForHash().put(redisKey,"id",1);
        redisTemplate.opsForHash().put(redisKey,"name","rin");

        System.out.println(redisTemplate.opsForHash().get(redisKey,"name"));
        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));

    }
    @Test
    public void testList(){
        String redisKey ="test:l";
        redisTemplate.opsForList().leftPush(redisKey,101);
        redisTemplate.opsForList().leftPush(redisKey,102);
        redisTemplate.opsForList().rightPush(redisKey,100);

        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey,2));
        System.out.println(redisTemplate.opsForList().range(redisKey,0,2));

        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));


    }

    @Test
    public void testSets(){
        String redisKey ="test:s";
        redisTemplate.opsForSet().add(redisKey,"nihap","rin","nana","axiang","ya");
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().randomMember(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().size(redisKey));

    }
    @Test
    public void testSortedSets(){
        String redisKey ="test:ss";
        redisTemplate.opsForZSet().add(redisKey,"rin",10);
        redisTemplate.opsForZSet().add(redisKey,"xiang",20);
        redisTemplate.opsForZSet().add(redisKey,"nana",30);
        redisTemplate.opsForZSet().add(redisKey,"leah",9);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().rank(redisKey,"rin"));
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey,"rin"));
        System.out.println(redisTemplate.opsForZSet().range(redisKey,0,3));
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey,0,3));
    }
    @Test
    public void testKeys() {
        System.out.println(redisTemplate.keys("*"));
        redisTemplate.delete("test:ss");
        System.out.println(redisTemplate.hasKey("test:h"));
        System.out.println(redisTemplate.keys("*"));
        redisTemplate.expire("test:ss", 10, TimeUnit.SECONDS);
    }


}
