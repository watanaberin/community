package com.social.community.community.controller;

import com.social.community.community.util.CommunityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @RequestMapping(value = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String cookie(HttpServletResponse response){

        Cookie cookie=new Cookie("code", CommunityUtil.generateUUID());
        //cookie有效范围 默认时间为关闭浏览器前
        cookie.setPath("/community/alpha");
        cookie.setMaxAge(60*10);
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping(value = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie";
    }
    @RequestMapping(value = "/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id",1);
        session.setAttribute("name","sessionTest");
        return "set session";
    }
    @RequestMapping(value = "/session/get",method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    @RequestMapping(path="/ajax",method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0,"操作成功！");
    }
}
