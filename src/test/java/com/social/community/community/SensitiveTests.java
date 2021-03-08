package com.social.community.community;

import com.social.community.community.util.SensitiveFilter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String text="#这里可以赌###博、吸#毒、抽烟、嫖#娼、哈哈";
        text=sensitiveFilter.filter(text);
        System.out.println(text);
    }
}
