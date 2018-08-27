package cn.wws.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.StringUtils;

import cn.wws.service.BaseService;
import cn.wws.service.SystemParamService;
import cn.wws.service.WechatOperateService;
import cn.wws.util.HttpExecutor;
import cn.wws.util.PropertiesUtil;

@Controller
@RequestMapping("/wechat")  
public class WechatController {
      private static final Logger LOGGER = LoggerFactory.getLogger(WechatController.class);
      @Autowired
      BaseService service;
      
      @Autowired
      WechatOperateService wechatOperateService;
      @Autowired
      SystemParamService systemParamService;
      
      @RequestMapping("/checkvalid")  
      @ResponseBody
      public String checkValid(HttpServletRequest request, @RequestBody(required=false) Map<String, ?> paramMap){  
          String signature = request.getParameter("signature");
          String timestamp = request.getParameter("timestamp");
          String nonce = request.getParameter("nonce");
          String echostr = request.getParameter("echostr");
          if (!StringUtils.isNullOrEmpty(echostr)) {
              LOGGER.info("进入/wechat/checkvalid，signature, timestamp, nonce, echostr={},{},{},{}", 
                      signature, timestamp, nonce, echostr);
              return echostr;
          }
          
          LOGGER.info("传入参数：paramMap={}", paramMap);
          if ("text".equals(paramMap.get("MsgType"))) {
              wechatOperateService.sendMsgToUser(String.valueOf(paramMap.get("FromUserName")), "你发送的消息是："+paramMap.get("Content"));
          }
          
          return echostr;
      }
      
      @RequestMapping("/notify")  
      @ResponseBody
      public String showBlog(@RequestBody Map<String, ?> paramMap){  
          LOGGER.info("进入/wechat/notify，paramMap={}", paramMap);
          Map<String, String> insertParam = new HashMap<String, String>();
          insertParam.put("param", paramMap.toString());
          int rst = service.executeInsert("callback.insertRecord", insertParam);
          if (rst != 1) {
              LOGGER.error("插入回调记录失败，paramMap={}", paramMap);
          }
          return "<xml><return_code>SUCCESS</return_code></xml>";  
      }
      
      @RequestMapping("/getAccessToken")  
      @ResponseBody
      public void refreshAccessToken() {
          HttpExecutor executor = new HttpExecutor();
          try {
            String rst = executor.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+
                    systemParamService.getParamValue("app_id")+"&secret="+systemParamService.getParamValue("app_secret"), null);
            LOGGER.info("获取accessToken="+rst);
          } catch (IOException e) {
            // TODO Auto-generated catch block
              e.printStackTrace();
          }
      }
       
}
