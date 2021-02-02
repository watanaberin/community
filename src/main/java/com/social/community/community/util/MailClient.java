package com.social.community.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailClient {
    private static final Logger logger=LoggerFactory.getLogger(MailClient.class);
    //mineMassager和send
    //massgaer由mailsender创建 内容由helper完成
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to,String subject,String content)  {
        try {
            MimeMessage mailMessage=mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mailMessage);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setCc(from);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(content,true);
            mailSender.send(mimeMessageHelper.getMimeMessage());
        }catch (MessagingException e){
            logger.error("发送邮件失败"+e.getMessage());
        }
        }
}
