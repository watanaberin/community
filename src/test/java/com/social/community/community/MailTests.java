package com.social.community.community;


import com.social.community.community.util.MailClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        mailClient.sendMail("87049633@qq.com","Test","132");
    }
    @Test
    public void testHtmlMail(){
        Context context=new Context();
        context.setVariable("username","sadrin");
        String content=templateEngine.process("/mail/demo",context);
        System.out.println(content);
        mailClient.sendMail("87049633@qq.com","htmlTest",content);
    }
}
