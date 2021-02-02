package com.social.community.community.util;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger= LoggerFactory.getLogger(SensitiveFilter.class);

    private  static final String REPLEACEMENT="***";

    private TrieNode rootNode=new TrieNode();

    @PostConstruct
    public void init(){
        try(
                InputStream is=this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader=new BufferedReader(new InputStreamReader(is));
                ) {
            String keyword;
            while((keyword= reader.readLine())!=null){
                this.addKeyword(keyword);
            }

        } catch (IOException e) {
            logger.error("加载敏感词文件失败"+e.getMessage());
        }
    }

    private void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for(int i=0;i<keyword.length();i++){
            char c=keyword.charAt(i);
            TrieNode subNode=tempNode.getSubNode(c);
            if(subNode==null){
                subNode=new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            tempNode=subNode;

            if(i==keyword.length()-1){
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text 待过滤文本
     * @return 过滤后的文本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        TrieNode tempNode=rootNode;
        int begin=0;
        int position=0;
        StringBuilder sb=new StringBuilder();
        while (position < text.length()){
            char c=text.charAt(position);
            //跳过符号
            if(isSymbol(c)){
                if(tempNode==rootNode){
                    sb.append(c);
                    begin++;
                }
                position++;
                continue;
            }
            tempNode=tempNode.getSubNode(c);
            if(tempNode==null){
                //以begin为开头的字符串不是敏感词
                 sb.append(text.charAt(begin));
                 position=++begin;
                 tempNode=rootNode;
            }else if(tempNode.isKeyWordEnd()){
                //发现敏感词 将begin-position替换
                sb.append(REPLEACEMENT);
                begin=++position;
                tempNode=rootNode;
            }else{
                position++;
            }
        }
        //会有剩余的敏感词
        sb.append((text.substring(begin)));
        return sb.toString();
    }
    //判断符号
    private boolean isSymbol(Character c){
        //东亚文字
        return !CharUtils.isAsciiAlphanumeric(c) && (c<0x2E80 ||c>0x9FFF);

    }
    private class TrieNode{
        //关键词结束标识
        private boolean isKeyWordEnd =false;

        //key-下级字符，value是下级结点
        private Map<Character,TrieNode> subNodes=new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        //添加子节点
        public  void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }
        public
         TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}
