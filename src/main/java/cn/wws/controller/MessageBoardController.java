package cn.wws.controller;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


import com.alibaba.fastjson.JSONObject;

import cn.wws.service.BaseService;
import cn.wws.util.BusiRulesUtils;
import cn.wws.util.EnDecryptUtil;

@Controller  
public class MessageBoardController {
		@Autowired
		BaseService baseService;
	  
	  @RequestMapping("/showMessageBoard")  
	  public String showMessageBoard(HttpServletRequest request,Model model){  
	    List<Map<String, Object>> list= baseService.executeQuery("messageBoard.getMessageBoard", null);
	    model.addAttribute("messageList",list);
	  	
	    return "user/showMessageBoard";  
	  }
	  
	  @RequestMapping("/showNewMessage")  
	  public String showNewMessage(HttpServletRequest request,Model model){  
	    return "user/showNewMessage";  
	  }
	  
	  @RequestMapping("/writeMessage")  
	  public void writeMessage(HttpServletRequest request, HttpServletResponse response) throws Exception{  
		String toName = BusiRulesUtils.getStringValue(request.getParameter("toName"));
		String content = BusiRulesUtils.getStringValue(request.getParameter("content"));
		String fromName = BusiRulesUtils.getStringValue(request.getParameter("fromName"));
		String fromPwd = BusiRulesUtils.getStringValue(request.getParameter("fromPwd"));
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("toName", toName);
		map.put("content", content);
		
		JSONObject ret = new JSONObject();
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		if (fromName != null && !"".equals(fromName)) {
			if (StringUtils.isBlank(fromPwd)) {
				ret.put("retCode", "0");
				ret.put("retMessage", "口令为空");
				out.write(ret.toJSONString());
				return;
			}
			map.put("fromName", fromName);
			fromPwd = EnDecryptUtil.encrypt(fromPwd);
			map.put("fromPwd", fromPwd);
			List<Map<String, Object>> list = baseService.executeQuery("user.checkPseudonymToken", map);
			if (list.size() != 1) {
				ret.put("retCode", "0");
				ret.put("retMessage", "笔名验证失败");
				out.write(ret.toJSONString());
				return;
			}
		} else {
			map.put("fromName", fromName);
			
		}
		
		
		int count = baseService.executeInsert("messageBoard.insertMessageBoard", map);
		if (count == 1) {
			ret.put("retCode", "1");
			ret.put("retMessage", "操作成功");
			out.write(ret.toJSONString());
			return;
		} else {
			ret.put("retCode", "0");
			ret.put("retMessage", "操作失败");
			out.write(ret.toJSONString());
			return;
		}
	  }
}
