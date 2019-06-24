package cn.wws.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
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

import cn.wws.service.AnniversaryService;
import cn.wws.service.BaseService;
import cn.wws.service.PlanService;
import cn.wws.service.SystemParamService;
import cn.wws.service.WechatOperateService;
import cn.wws.service.WechatService;
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
      WechatService wehchatService;
      @Autowired
      AnniversaryService anniversaryService;
      @Autowired
      PlanService planService;
      
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
          String openId = (String)paramMap.get("FromUserName");
          
          LOGGER.info("传入参数：paramMap={}", paramMap);
          StringBuilder msg = new StringBuilder();
          if ("text".equals(paramMap.get("MsgType"))) {
              String content = (String)paramMap.get("Content");
              if(content.startsWith("cmd")){
                  content = content.substring(3);
                  if(content.startsWith("查询")){
                      content = content.substring(2);
                      if(content.startsWith("纪念日")){
                          msg.append("纪念日    提醒内容    编号\n");
                          List<Map<String, Object>> rst = wehchatService.getAnniversariesByOpenid(openId);
                          for(Map<String, Object> ann : rst){
                              msg.append("" + ann.get("anniversary_month") + ann.get("anniversary_date")+
                                      "    " + ann.get("remind_content") + "    " +ann.get("id") + "\n");
                          }
                      } else if(content.startsWith("计划")){
                          msg.append("计划    进度    提醒日期    编号\n");
                          String userName = wehchatService.getUserNameByOpenId(openId);
                          List<Map<String, Object>> list = planService.getUserPlanRemind(userName);
                          for(Map<String, Object> planRemind : list){
                              msg.append("" + planRemind.get("plan_name") + "    " + 
                                      planRemind.get("finished_times") + "/" + planRemind.get("plan_times") + 
                                      "    " +planRemind.get("remind_date") + "    " +planRemind.get("id") + "\n");
                          }
                      }
                  } else if(content.startsWith("修改")) {
                      String userName = wehchatService.getUserNameByOpenId(openId);
                      content = content.substring(2);
                      if(content.startsWith("纪念日")){
                          content = content.substring(3).trim();
                          String[] params = content.split(" ");
                          Map<String, String> map = new HashMap<>();
                          map.put("userName", userName);
                          for(int i=0; i<params.length; i++){
                              if(params[i].startsWith("-i")){
                                  map.put("id", params[i].substring(2).trim());
                              } else if(params[i].startsWith("-m")){
                                  map.put("anniversaryMonth", params[i].substring(2).trim());
                              } else if(params[i].startsWith("-d")){
                                  map.put("anniversaryDate", params[i].substring(2).trim());
                              } else if(params[i].startsWith("-c")){
                                  map.put("remindContent", params[i].substring(2).trim());
                              }
                          }
                          if(map.containsKey("id")){
                              anniversaryService.updateAnniversary(map);
                              msg.append("操作成功");
                          }
                      }
                  } else if(content.startsWith("完成计划")){
                	  content = content.substring(4);
                	  int id = 0;
                	  String[] params = content.split(" ");
                	  for(int i=0; i<params.length; i++){
                          if(params[i].startsWith("-i")){
                              try{
                        		  id = Integer.valueOf(params[i].substring(2).trim());
                        		  planService.finishPlanRemind(id);
                        		  msg.append("操作成功");
                        	  }catch(Exception e){
                        		  msg.append("命令错误");
                        	  }
                          } 
                      }
                  } else {
                	  msg.append("查询纪念日: cmd查询纪念日\n");
                	  msg.append("修改纪念日: cmd修改纪念日 -i10002 -m六月 -d廿五 -c普天同庆\n");
                	  msg.append("查询计划: cmd查询计划\n");
                	  msg.append("完成计划: cmd完成计划 -i5");
                  }
              } else {
                  msg.append("你发送的消息是："+content);
              }
          } else {
              msg.append("你发送的不是文本消息");
          }
          wechatOperateService.sendMsgToUser(String.valueOf(paramMap.get("FromUserName")), msg.toString());
          
          return echostr;
      }
      
      @RequestMapping("/operateExample")  
      public String operateExample(HttpServletRequest request){
          return "base/wechatOperateExample"; 
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
