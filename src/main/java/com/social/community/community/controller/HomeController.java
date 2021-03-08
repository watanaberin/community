package com.social.community.community.controller;

import com.social.community.community.entity.DiscussPost;
import com.social.community.community.entity.Page;
import com.social.community.community.entity.User;
import com.social.community.community.service.DiscussPostService;
import com.social.community.community.service.LikeService;
import com.social.community.community.service.UserService;
import com.social.community.community.util.CommunityConstant;
import com.social.community.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path="/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page,@RequestParam(name="orderMode",defaultValue = "0") int orderMode){
        //springMvc会自动实例化model和page，并将page注入到model
        //所以thymeleaf可以直接访问page中的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderMode="+orderMode);

        List<DiscussPost> list=discussPostService.findDiscussPost(0, page.getOffset(), page.getLimit(),orderMode);
        List<Map<String,Object>> discussPosts=new ArrayList<>();
        if(list!=null){
            for(DiscussPost post:list){
                Map<String,Object> map=new HashMap<>();
                map.put("post",post);
                User user=userService.findUserById(post.getUserId());
                map.put("user",user);
                //赞
                long likeCount=likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount",likeCount);

                discussPosts.add(map);

            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("orderMode",orderMode);
        return "index";
    }

    @RequestMapping(path="/error",method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }

    @RequestMapping(path="/denied",method =RequestMethod.GET)
    public String getDeniedPage(){
        return "/error/404";
    }

}
