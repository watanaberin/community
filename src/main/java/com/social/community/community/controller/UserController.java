package com.social.community.community.controller;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.social.community.community.annotation.LoginRequired;
import com.social.community.community.entity.User;
import com.social.community.community.service.FollowService;
import com.social.community.community.service.LikeService;
import com.social.community.community.service.UserService;
import com.social.community.community.util.CommunityConstant;
import com.social.community.community.util.CommunityUtil;
import com.social.community.community.util.HostHolder;
import javafx.application.HostServices;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger= LoggerFactory.getLogger(UserController.class);
    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FollowService followService;

    @Value("${qiniu.key.access}")
    private String accessKey;
    @Value("${qiniu.key.secret}")
    private String secretKey;
    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;
    @Value("${qiniu.bucket.header.url}")
    private String headerBucketUrl;

    @LoginRequired
    @RequestMapping(path="/setting", method = RequestMethod.GET)
    public String getSettingPage(Model model){
        //上传文件名称
        String fileName=CommunityUtil.generateUUID();
        //设置响应信息
        StringMap policy=new StringMap();
        policy.put("returnBody",CommunityUtil.getJSONString(0));
        //生成上传凭证
        Auth auth= Auth.create(accessKey,secretKey);
        String uploadToken =auth.uploadToken(headerBucketName,fileName,3600,policy);
        model.addAttribute("uploadToken",uploadToken);
        model.addAttribute("fileName",fileName);
        return "/site/setting";
    }
    //更新图像路径
    @RequestMapping(path = "/header/url",method = RequestMethod.POST)
    @ResponseBody
    public String updateHeaderUrl(String fileName){
        if(StringUtils.isBlank(fileName)){
            return CommunityUtil.getJSONString(0,"你还没有选择图片吗");
        }
        String url=headerBucketUrl+"/"+fileName;
        userService.updateHeader(hostHolder.getUser().getId(),url);
        return CommunityUtil.getJSONString(0);
    }
    //废弃
    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage==null){
            model.addAttribute("error","您还没有选择图片。");
            return "/user/setting";
        }

        String filename=headerImage.getOriginalFilename();
        String suffix=filename.substring(filename.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件的格式不正确。");
            return "/site/setting";
        }
        //没有判断文件类型
        //随机文件名
        filename=CommunityUtil.generateUUID()+suffix;
        //确定文件存放路径
        File dest=new File(uploadPath+"/"+ filename);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败"+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常。",e);
        }
        //更新当前用户头像的路径
        //http://localhost:8080/community/user/header/xxx.png
        User user=hostHolder.getUser();
        String headerUrl=domain+contextPath+"/user/header/"+filename;
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/index";
    }
    //废弃
    @RequestMapping(path = "/header/{filename}" ,method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename")String filename, HttpServletResponse response){
        filename=uploadPath+"/"+filename;
        String suffix=filename.substring(filename.lastIndexOf("."));
        response.setContentType("image/"+suffix);
        try (
                OutputStream outputStream=response.getOutputStream();
                FileInputStream fis=new FileInputStream(filename);//会加入final关闭
        ){
            byte[] buffer=new byte[1024];//缓冲
            int b=0;//游标
            while((b=fis.read(buffer))!=-1){
                outputStream.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败："+e.getMessage());
        }
    }
    @RequestMapping(path = "/updatepassword" ,method = RequestMethod.POST)
    public String updatePassword(Model model,String newPassword,String oldpassword){
        User user=hostHolder.getUser();
        if(user==null){
            model.addAttribute("nullMsg","暂时无访问权限。");
            return "/login";
        }
        Map<String,Object> map=new HashMap<>();
        map= userService.updatePassword(user,newPassword,oldpassword);
        if(map==null || map.isEmpty()){
            String index=contextPath+"/login";
            model.addAttribute("msg","修改成功，请重新进行登录操作。");
            model.addAttribute("target",index);
            return "/site/operate-result-modify";
        }else{
            model.addAttribute("newpswdMsg",map.get("newpswdMsg"));
            model.addAttribute("oldpswdMsg",map.get("oldpswdMsg"));
            return "/site/setting";
        }

    }
    //个人主页
    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId,Model model){
        User user=userService.findUserById(userId);
        if(user==null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",user);
        int likeCount=likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);

        //关注数量
        long followeeCount = followService.findFolloweeCount(userId,ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER,userId);
        model.addAttribute("followerCount",followerCount);
        //是否已关注
        boolean hasFollowed=false;
        if(hostHolder.getUser()!=null){
            hasFollowed=followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);
        return "/site/profile";
    }
}
