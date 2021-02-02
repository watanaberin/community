package com.social.community.community.util;

import com.social.community.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 线程隔离，代替session
 */

@Component
public class HostHolder {
    //以线程为key取值
    private ThreadLocal<User> users=new ThreadLocal<>();

    public void setUsers(User user){
        users.set(user);
    }
    public User getUser(){
        return users.get();
    }
    public void clear(){
        users.remove();
    }
}
