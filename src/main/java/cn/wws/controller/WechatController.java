package cn.wws.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.StringUtils;

import cn.wws.service.BaseService;
import cn.wws.service.SystemParamService;
import cn.wws.service.WechatOperateService;
import cn.wws.util.CookieUtil;
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
      
      /**    
       * @Description: 初始化，自动跳转到微信授权. 
       */ 
       @RequestMapping("/init")
       public String payApply(HttpServletRequest request, ModelMap model) throws Exception {
           model.addAttribute("appid", systemParamService.getParamValue("app_id"));
           model.addAttribute("redirectUri", URLEncoder.encode(systemParamService.getParamValue("authorize_notify_url"), "UTF-8"));
           model.addAttribute("state", UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32));
           return "/wechat/init";
       }

       /**    
       * @Description: 通过code获取openid. 
       * @author songjun  
       * @date 2018年4月27日   
       */ 
       @RequestMapping("/authorize")
       String authorizeAppid(HttpServletRequest request, HttpServletResponse response,
               @RequestParam(value = "code", required = true) String code, 
               @RequestParam(value = "state", required = false)  String state,
               Model model) throws Exception {
           LOGGER.debug("临时票据code={}", code);
           //是否需要检查state
           String openId = wechatOperateService.getOpenIdByCode(code);
           CookieUtil.writeCookie(response, "openId", openId);
           Map<String, String> param = new HashMap<String, String>();
           param.put("openId", openId);
           return "redirect:/profile";
           
       }
       
}
