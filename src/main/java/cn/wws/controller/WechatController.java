package cn.wws.controller;

import java.util.HashMap;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.wws.service.BaseService;

@Controller
@RequestMapping("/wechat")  
public class WechatController {
	  private static final Logger LOGGER = LoggerFactory.getLogger(WechatController.class);
	  @Autowired
	  BaseService service;
	  
	  @RequestMapping("/notify")  
	  @ResponseBody
	  public String showBlog(@RequestBody Map<String, ?> paramMap){  
		  LOGGER.info("进入/callback/notify，paramMap={}", paramMap);
		  Map<String, String> insertParam = new HashMap<String, String>();
		  insertParam.put("param", paramMap.toString());
		  int rst = service.executeInsert("callback.insertRecord", insertParam);
		  if (rst != 1) {
			  LOGGER.error("插入回调记录失败，paramMap={}", paramMap);
		  }
	      return "<xml><return_code>SUCCESS</return_code></xml>";  
	  }
	  
	  @RequestMapping("/analysisJson")  
      @ResponseBody
	  public String analysisJson(@RequestBody JSONObject param) {
	      JSONArray ja = param.getJSONArray("resp");
	      StringBuffer sb = new StringBuffer();
	      for (int i=0; i<ja.size(); i++) {
	          JSONObject json = ja.getJSONObject(i);
	          if ("该商品可确认收货".equals(json.getString("reason"))
	                  && "2018-07-27".compareTo(json.getString("minTradeFinishTm"))>=0) {
	              sb.append("(select order_code,order_status from zmall_order_info where order_code = ").append(json.getString("orderCode")).append(") union all\n");
	          }
	      }
	      return sb.toString();
	  }
}
