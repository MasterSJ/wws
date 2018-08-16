package cn.wws.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;

import cn.wws.entity.KeyWord;
import cn.wws.entity.ReturnResult;
import cn.wws.entity.User;
import cn.wws.service.BaseService;
import cn.wws.service.SigninService;
import cn.wws.util.BusinessRuleChecker;
import cn.wws.util.EnDecryptUtil;


@Controller  
public class RegisterController {
	@Autowired
	BaseService baseService;
	
	@Autowired
	SigninService signinService;

	@RequestMapping("/showRegister")  
    public String showSignin(HttpServletRequest request, Model model) {  
		return "base/register"; 
    } 
	
	/**
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping("/doRegister")
    public void doSignin(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String userName = request.getParameter("userName");
		String userPwd = request.getParameter("userPwd");
		String reUserPwd = request.getParameter("reUserPwd");
		String pseudonym = request.getParameter("pseudonym");
		ReturnResult ret = new ReturnResult();
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		/**校验用户名*/
		ret = BusinessRuleChecker.userNameCheck(userName);
		if (!ret.isSuccess()) {
			out.write(JSONObject.toJSONString(ret));
			return;
		}
		/**校验密码*/
		ret = BusinessRuleChecker.pwdCheck(userPwd);
		if (!ret.isSuccess()) {
			out.write(JSONObject.toJSONString(ret));
			return;
		}
		/**校验第二次输入密码*/
		ret = BusinessRuleChecker.isSamePassword(userPwd, reUserPwd);
		if (!ret.isSuccess()) {
			out.write(JSONObject.toJSONString(ret));
			return;
		}

        System.out.println("注册-传入的用户名"+userName+"  密码"+userPwd);
		userPwd = EnDecryptUtil.encrypt(userPwd);
        System.out.println("注册-加密之后的用户名"+userName+"  密码"+userPwd);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userName", userName);
		map.put("userPwd", userPwd);
		map.put("pseudonym", pseudonym);
		/**查询用户名是否已经存在*/
		List<Map<String, Object>> list = baseService.executeQuery("user.selectExistsUser", map);
		if (list.size() >= 1) {
			ret.setFail();
			ret.setMsg(KeyWord.ERROR_MSG_7);
			out.write(JSONObject.toJSONString(ret));
			return;
		}
		/**查询笔名是否已经存在*/
		List<Map<String, Object>> list1= baseService.executeQuery("user.selectExistsPseudonym", map);
		if (list1.size() >= 1) {
			ret.setFailMsg("此笔名已被使用");
			out.write(JSONObject.toJSONString(ret));
			return;
		}
		int count = baseService.executeInsert("user.register", map);
		int count1 = baseService.executeInsert("user.grabPseudonym", map);
		if (count != 1 || count1 != 1) {
			ret.setFail();
			ret.setMsg(KeyWord.ERROR_MSG_8);
			out.write(JSONObject.toJSONString(ret));
			return;
		} else {
			signinService.login(userName, userPwd);
			ret.setSuccessMsg(KeyWord.SECCESS_MSG_1);
			out.write(JSONObject.toJSONString(ret));
			return;
		}
    } 
	

	  
	  @RequestMapping("/showGrabPseudonym")  
	  public String showGrabPseudonym(HttpServletRequest request,Model model){  
	    return "base/grabPseudonym";  
	  }
	  
	  


	  
}
