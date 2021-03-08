package com.social.community.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.social.community.community.entity.Message;
import com.social.community.community.entity.Page;
import com.social.community.community.entity.User;
import com.social.community.community.service.MessageService;
import com.social.community.community.service.UserService;
import com.social.community.community.util.CommunityConstant;
import com.social.community.community.util.CommunityUtil;
import com.social.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {

    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    //私信列表
    @RequestMapping(path="/letter/list" ,method= RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        User user =hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        //会话列表
        List<Message> conversationList=messageService.findConversations(user.getId(),page.getOffset(),page.getLimit());
        List<Map<String,Object>> conversations=new ArrayList<>();
        if(conversationList !=null){
            for(Message message :conversationList){
                Map<String,Object> map=new HashMap<>();
                map.put("conversation",message);
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(),message.getConversationId()));
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                int targetId =user.getId() ==message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target",userService.findUserById(targetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);

        //查询未读消息数量
        int letterUnreadCount=messageService.findLetterUnreadCount(user.getId(),null);
        model.addAttribute(letterUnreadCount);
        int noticeUnreadCount= messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);
        return "/site/letter";
    }

    @RequestMapping(path="/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable String conversationId,Page page,Model model){
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        List<Message> letterList=messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> letters=new ArrayList<>();
        if(letterList !=null){
            for(Message message :letterList){
                Map<String,Object> map=new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);

        //设置已读
        List<Integer> ids =getLetterIds(letterList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }
        model.addAttribute("target",getLetterTarget(conversationId));
        return "site/letter-detail";
    }
    private List<Integer> getLetterIds(List<Message> LetterList){
        List<Integer> ids= new ArrayList<>();
        if(LetterList != null){
            for(Message message :LetterList){
                if(hostHolder.getUser().getId() ==message.getToId() && message.getStatus() ==0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }


    private User getLetterTarget(String conversationId){
        String[] ids=conversationId.split("_");
        int id0=Integer.parseInt(ids[0]);
        int id1=Integer.parseInt(ids[1]);
        if(hostHolder.getUser().getId() ==id0){
            return userService.findUserById(id1);
        }else{
            return userService.findUserById(id0);
        }
    }
    @RequestMapping(path="/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLitter(String toName,String content){
        User target=userService.findUserByName(toName);
        if(target ==null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在");
        }
        Message message=new Message();
        message.setToId(target.getId());
        message.setFromId(hostHolder.getUser().getId());
        if(message.getFromId() <message.getToId()){
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }else{
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);
    }

    @RequestMapping(path = "/notice/list",method =RequestMethod.GET)
    public String getNoticeList(Model model){
        User user=hostHolder.getUser();
        //comment
        Message message= messageService.findLatestNotice(user.getId(),TOPIC_COMMENT );
        if(message!=null){
            Map<String,Object> messageVo=new HashMap<>();

            messageVo.put("message",message);
            String content= HtmlUtils.htmlUnescape(message.getContent());
            HashMap<String,Object> data=JSONObject.parseObject(content,HashMap.class);
            messageVo.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));
            messageVo.put("postId",data.get("postId"));

            int count=messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVo.put("count",count);
            count=messageService.findNoticeUnreadCount(user.getId() ,TOPIC_COMMENT);
            messageVo.put("unread",count);
            model.addAttribute("commentNotice",messageVo);

        }
        //like
        message= messageService.findLatestNotice(user.getId(),TOPIC_LIKE );
        if(message!=null){
            Map<String,Object> messageVo=new HashMap<>();
            messageVo.put("message",message);
            String content= HtmlUtils.htmlUnescape(message.getContent());
            HashMap<String,Object> data=JSONObject.parseObject(content,HashMap.class);
            messageVo.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));
            messageVo.put("postId",data.get("postId"));

            int count=messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVo.put("count",count);
            count=messageService.findNoticeUnreadCount(user.getId(),TOPIC_LIKE);
            messageVo.put("unread",count);
            model.addAttribute("likeNotice",messageVo);

        }
        //follow
        message= messageService.findLatestNotice(user.getId(),TOPIC_FOLLOW );
        if(message!=null){
            Map<String,Object> messageVo=new HashMap<>();
            messageVo.put("message",message);
            String content= HtmlUtils.htmlUnescape(message.getContent());
            HashMap<String,Object> data=JSONObject.parseObject(content,HashMap.class);
            messageVo.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));

            int count=messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("count",count);
            count=messageService.findNoticeUnreadCount(user.getId(),TOPIC_FOLLOW);
            messageVo.put("unread",count);
            model.addAttribute("followNotice",messageVo);

        }

        //查询未读消息数量
        int letterUnreadCount=messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        int noticeUnreadCount= messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        return "/site/notice";
    }
    @RequestMapping(path = "/notice/detail/{topic}",method = RequestMethod.GET)
    public String getNotices(@PathVariable("topic") String topic,Page page,Model model){
        User user=hostHolder.getUser();

        page.setLimit(5);
        page.setPath("/notice/detail"+topic);
        page.setRows(messageService.findNoticeCount(user.getId(),topic));

        List<Message> noticeList=messageService.findNotices(user.getId(), topic , page.getOffset(), page.getLimit());
        List<Map<String,Object>> noticeVoList=new ArrayList<>();
        if(noticeList!=null){
            for(Message notice :noticeList){
                Map<String,Object> map=new HashMap<>();
                //通知
                map.put("notice",notice);
                //内容
                String content=HtmlUtils.htmlUnescape(notice.getContent());
                Map<String,Object> data=JSONObject.parseObject(content,HashMap.class);
                map.put("user",userService.findUserById((Integer) data.get("userId")));
                map.put("entityType",data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                map.put("postId",data.get("postId"));
                //通知作者
                map.put("fromUser",userService.findUserById(notice.getFromId()));
                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices",noticeVoList);

        //设置已读
        List<Integer> ids=getLetterIds(noticeList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }
        return "/site/notice-detail";
    }
}
