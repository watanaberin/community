package com.social.community.community.util;

public interface CommunityConstant {

    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS=0;
    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT=1;
    /**
     * 激活失败
     */
    int ACTIVATION_FAIL=2;
    /**
     * 默认登录凭证超时时间
     */
    int DEFAULT_EXPIRED_SECOND=3600*10;
    /**
     * 记住登录配置超时时间
     */
    int REMEMBER_EXPIRED_SECOND=3600*24*100;

}
