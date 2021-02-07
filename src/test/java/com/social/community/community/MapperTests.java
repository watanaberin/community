package com.social.community.community;

import com.social.community.community.dao.CommentMapper;
import com.social.community.community.dao.DiscussPostMapper;
import com.social.community.community.dao.LoginTicketMapper;
import com.social.community.community.dao.UserMapper;
import com.social.community.community.entity.Comment;
import com.social.community.community.entity.DiscussPost;
import com.social.community.community.entity.LoginTicket;
import com.social.community.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private CommentMapper commentMapper;

    @Test
    public void  testSelectUser(){
        User user=userMapper.selectById(101);
        System.out.println(user);
        user =userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void  testInsertUser(){
        User user=new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void updateUser() {
        int rows = userMapper.updateStatus(151, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(151, "http://www.community.com/103.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(151, "hello");
        System.out.println(rows);
    }


    @Test
    public void discussPostTest(){
        List<DiscussPost> result=discussPostMapper.selectDiscussPosts(149,0,10);
        for(DiscussPost discussPost :result){
            System.out.println(discussPost);
        }
        int rows= discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Test
    public void loginTicketTest(){
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }
    @Test
    public void LoginTicketMapperTests(){
        LoginTicket loginTicket=loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abc",1);
        loginTicket=loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

    }
    @Test
    public void CommentMapperTests(){
        List<Comment> comments=commentMapper.selectCommentsByEntity(1,228,0,10);
        if(comments==null) System.out.println("no comments found");
        for(Comment comment :comments){
            System.out.println(comment);
        }
    }
}
