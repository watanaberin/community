package com.social.community.community.dao;

import com.social.community.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);//userid==0就是取全部（首页）
    //动态+一个参数并且在<if>里使用----取别名@Param
    int selectDiscussPostRows(@Param("userId") int userId);//页面数




}
