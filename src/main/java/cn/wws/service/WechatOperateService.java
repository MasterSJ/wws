package cn.wws.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.wws.util.HttpExecutor;
import cn.wws.util.PropertiesUtil;

@Service
public class WechatOperateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WechatOperateService.class);
    @Autowired
    SystemParamService systemParamService;
    
    /**    
    * @Description 刷新access_token. 
    * @author songjun  
    * @date 2018年8月27日   
    */ 
    public void refreshAccessToken() {
        HttpExecutor executor = new HttpExecutor();
        try {
          String rst = executor.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+
                  systemParamService.getParamValue("app_id")+"&secret="+systemParamService.getParamValue("app_secret"), null);
          LOGGER.info("获取accessToken="+rst);
          JSONObject json = JSON.parseObject(rst);
          systemParamService.setParamValue("access_token", json.getString("access_token"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**    
    * @Description 向用户发送消息. 
    * @author songjun  
    * @date 2018年8月27日   
    * @param openId
    * @param content
    */ 
    public void sendMsgToUser(String openId, String content) {
        HttpExecutor executor = new HttpExecutor();
        try {
            JSONObject jsonContent = new JSONObject();
            jsonContent.put("touser", openId);
            jsonContent.put("msgtype", "text");
            JSONObject text = new JSONObject();
            text.put("content", content);
            jsonContent.put("text", text);
            String rst = executor.post("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+
                    systemParamService.getParamValue("access_token"), jsonContent.toJSONString());
            LOGGER.info("发送消息结果：rst=", rst);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
