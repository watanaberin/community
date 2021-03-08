package com.social.community.community;

import com.social.community.community.dao.DiscussPostMapper;
import com.social.community.community.entity.DiscussPost;
import com.social.community.community.service.DiscussPostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CaffineTests {

    @Autowired
    private DiscussPostService discussPostService;

    @Test
    public void initDataForTest(){
        for(int i=0;i<300000;i++){
            DiscussPost post=new DiscussPost();
            post.setUserId(111);
            post.setTitle("有问题，就会有答案。");
            post.setContent("有问题，就会有答案。");
            post.setCreateTime(new Date());
            post.setScore(Math.random()*2000);
            discussPostService.addDiscussPost(post);
        }
    }
    @Test
    public void testCache(){
        System.out.println(discussPostService.findDiscussPost(0,0,10,1));
        System.out.println(discussPostService.findDiscussPost(0,0,10,1));

        System.out.println(discussPostService.findDiscussPost(0,0,10,1));
        System.out.println(discussPostService.findDiscussPost(0,0,10,0));


    }

}
