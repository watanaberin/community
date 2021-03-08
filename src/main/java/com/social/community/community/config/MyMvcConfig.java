package com.social.community.community.config;

import com.social.community.community.annotation.LoginRequired;
import com.social.community.community.controller.interceptor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyMvcConfig implements WebMvcConfigurer {
    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/", "classpath:/resources/",
            "classpath:/static/", "classpath:/public/" };
    @Autowired
    private AlphaIntercepter alphaIntercepter;

    @Autowired
    private LoginTicketIntercepor loginTicketIntercepor;

    @Autowired
    private DataInterceptor dataInterceptor;
 //   @Autowired
  //  private LoginRequiredIntercepter loginRequiredIntercepter;

    @Autowired
    private MessageInterceptor messageInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(alphaIntercepter)
                .excludePathPatterns("/*/*.css","/*/*.js","/*/*.jpg","/*/*.jpeg")
                .addPathPatterns("/register","/login");

        registry.addInterceptor(loginTicketIntercepor)
                .excludePathPatterns("/*/*.css","/*/*.js","/*/*.jpg","/*/*.jpeg");
     //   registry.addInterceptor(loginRequiredIntercepter)
      //          .excludePathPatterns("/*/*.css","/*/*.js","/*/*.jpg","/*/*.jpeg");
        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/*/*.css","/*/*.js","/*/*.jpg","/*/*.jpeg");
        registry.addInterceptor(dataInterceptor)
                .excludePathPatterns("/*/*.css","/*/*.js","/*/*.jpg","/*/*.jpeg");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!registry.hasMappingForPattern("/webjars/**")) {
            registry.addResourceHandler("/webjars/**").addResourceLocations(
                    "classpath:/META-INF/resources/webjars/");
        }
        if (!registry.hasMappingForPattern("/**")) {
            registry.addResourceHandler("/**").addResourceLocations(
                    CLASSPATH_RESOURCE_LOCATIONS);
        }
    }
}
