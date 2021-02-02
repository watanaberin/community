package com.social.community.community.controller;

import com.social.community.community.annotation.LoginRequired;
import com.social.community.community.entity.User;
import com.social.community.community.service.UserService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
public class UserController {

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

    @LoginRequired
    @RequestMapping(path="/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

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

}
