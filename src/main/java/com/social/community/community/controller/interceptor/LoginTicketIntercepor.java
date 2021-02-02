package com.social.community.community.controller.interceptor;

import com.social.community.community.entity.LoginTicket;
import com.social.community.community.entity.User;
import com.social.community.community.service.UserService;
import com.social.community.community.util.CookieUtil;
import com.social.community.community.util.HostHolder;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketIntercepor implements HandlerInterceptor {

    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从cookie中获取凭证
        String ticket= CookieUtil.getValue(request,"ticket");
        if(ticket!=null){
            //查询凭证
            LoginTicket loginTicket=userService.findLoginTicket(ticket);
            if(loginTicket!=null && loginTicket.getStatus()==0 &&loginTicket.getExpired().after(new Date())){
                User user=userService.findUserById(loginTicket.getUserId());
                //threadlocal 线程隔离
                hostHolder.setUsers(user);
            }
        }
        return true;
    }
    //Controller--《postHandler》-model
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user=hostHolder.getUser();
        if(user!=null &&modelAndView!=null){
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
