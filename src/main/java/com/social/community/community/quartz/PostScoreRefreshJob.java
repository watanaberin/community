package com.social.community.community.quartz;

import com.social.community.community.entity.DiscussPost;
import com.social.community.community.service.CommentService;
import com.social.community.community.service.DiscussPostService;
import com.social.community.community.service.ElasticsearchService;
import com.social.community.community.service.LikeService;
import com.social.community.community.util.CommunityConstant;
import com.social.community.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostScoreRefreshJob implements Job , CommunityConstant {

    private  static final Logger logger= LoggerFactory.getLogger(PostScoreRefreshJob.class);
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private ElasticsearchService elasticsearchService;

    //起始时间
    private static  final Date epoch;

    static {
        try {
            epoch=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2019-01-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化起始时间失败"+e.getMessage());
        }
    }
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey= RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations=redisTemplate.boundSetOps(redisKey);
        if(operations.size()==0){
            logger.info("[任务取消]，没有需要刷新的问题。");
            return;
        }
        logger.info("[任务开始] 正在刷新问题分数："+operations.size());
            while(operations.size()>0){
                this.refresh((Integer) operations.pop());
            }

        logger.info("[任务结束] 刷新问题分数完毕。");

    }
    private void refresh(int postId){
        DiscussPost post=discussPostService.findDiscussPostById(postId);

        if(post==null){
            logger.error("该问题不存在："+postId);
            return;
        }
        boolean wonderful=post.getStatus()==1;

        int commentCount=post.getCommentCount();

        long likeCount=likeService.findEntityLikeCount(ENTITY_TYPE_POST,postId);

        //权重
        double w= (wonderful? 70 : 0 ) +commentCount * 10 +likeCount * 2;
        //分数=权重+距离天数
        double score=Math.log10(Math.max(w,1))
                +(post.getCreateTime().getTime()-epoch.getTime())/(1000*3600*24);
        discussPostService.updateScore(postId,score);
        //同步搜索数据
        post.setScore(score);
        elasticsearchService.savaDiscussPost(post);

    }
}
