package cn.wws.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;

import cn.wws.entity.ReturnResult;
import cn.wws.entity.User;
import cn.wws.service.BaseService;
import cn.wws.service.SigninService;


@Controller  
public class SigninController {

	@Autowired
	BaseService baseService;
	
	@Autowired
	SigninService signinService;
	
	@RequestMapping("/showSignin")  
    public String showSignin(HttpServletRequest request, Model model) {  
		return "base/signin"; 
    } 
	
	@RequestMapping("/doSignin")  
    public void doSignin(HttpServletRequest request, HttpServletResponse response) throws Exception{
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String userName = request.getParameter("userName");
		String userPwd = request.getParameter("userPwd");
		ReturnResult ret = new ReturnResult();
		if (StringUtils.isBlank(userName)) {
			ret.setFailMsg("用户名为空");
			out.write(ret.toJsonString());
			return;
		}
		if (StringUtils.isBlank(userPwd)) {
			ret.setFailMsg("密码为空");
			out.write(ret.toJsonString());
			return;
		}
		boolean loginRet = signinService.login(userName, userPwd);
		if (loginRet) {
			ret.setSuccessMsg("登录成功");
		} else {
			ret.setFailMsg("用户名或密码错误");
		}
		out.write(ret.toJsonString());
    } 
	
	@RequestMapping("/logOut")  
    public void logOut(HttpServletRequest request,  HttpServletResponse response) throws IOException {  
		HttpSession httpSession = request.getSession(true);
		httpSession.invalidate();
		response.sendRedirect("/main");
		return ; 
    } 
}
