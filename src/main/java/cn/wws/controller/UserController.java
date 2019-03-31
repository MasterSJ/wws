package cn.wws.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.wws.annotation.CheckLogin;
import cn.wws.controller.base.BaseController;
import cn.wws.entity.ReturnResult;
import cn.wws.entity.User;
import cn.wws.service.BaseService;
import cn.wws.service.SigninService;
import cn.wws.util.RequestUtil;




@Controller  
@RequestMapping("/user")
public class UserController extends BaseController{
	@Autowired
    private BaseService baseService;
	@Autowired
	private SigninService signinService;

	@RequestMapping("/console")  
    public String showSignin(HttpServletRequest request, Model model) {  
		mergeUserInfo(model);
		return "user/console"; 
    } 
	
	@RequestMapping("/userInfo")  
    public String showUserInfo(HttpServletRequest request, Model model) {  
		mergeUserInfo(model);
		return "user/userInfo"; 
    } 

	@RequestMapping("/editInfo")  
    public void editInfo(HttpServletRequest request,  HttpServletResponse response) throws IOException {
		HttpSession httpSession = request.getSession(true);
		User user = (User)httpSession.getAttribute("USER");
		
		String realName = request.getParameter("realName");
		String nikeName = request.getParameter("nikeName");
		String email = request.getParameter("email");
		String mobile = request.getParameter("mobile");
		String birthday = request.getParameter("birthday");
		String resume = request.getParameter("resume");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("realName", realName);
		map.put("nikeName", nikeName);
		map.put("email", email);
		map.put("mobile", mobile);
		map.put("birthday", birthday);
		map.put("resume", resume);
		map.put("userName", user.getUserName());
		JSONObject ret = new JSONObject();
		PrintWriter out = response.getWriter();
		int count = baseService.executeUpdate("user.updateUserInfo", map);
		if (count <= 1) {
			user.setRealName(realName);
			user.setNikeName(nikeName);
			user.setEmail(email);
			user.setMobile(mobile);
			user.setBirthday(birthday);
			user.setResume(resume);
			httpSession.setAttribute("USER", user);
			ret.put("retCode", "1");
			ret.put("retMessage", "操作成功");
			out.write(ret.toJSONString());
			out.close();
		} else {
			ret.put("retCode", "0");
			ret.put("retMessage", "操作失败");
			out.write(ret.toJSONString());
			out.close();
		}
		return; 
    } 
	
	/**绑定笔名*/
	@RequestMapping("/showBindPseudonym")  
    public String showBindPseudonym(HttpServletRequest request, Model model) {  
		mergeUserInfo(model);
		return "user/pseudonymInfo"; 
    } 
	
	@RequestMapping("/doBindPseudonym")
	@ResponseBody
	public String doBindPseudonym(@RequestBody Map<String, String> param){
		ReturnResult ret = new ReturnResult();
		List<Map<String, Object>> list = baseService.executeQuery("user.selectExistsPseudonym", param);
		if (list.size() >= 1) {
			ret.setFailMsg("此笔名已存在");
			return ret.toJsonString();
		} 
		User user = RequestUtil.getRequestUser();
		param.put("userName", user.getUserName());
		int insCount = baseService.executeInsert("user.grabPseudonym", param);
		if (insCount == 1) {
			signinService.cachedPseudonym(null);
			ret.setSuccessMsg("绑定成功");
		} else {
			ret.setFailMsg("绑定失败");
		}
		return ret.toJsonString();
	}
	
	/**绑定笔名*/
	@RequestMapping("/blogCategory")  
    public String showBlogCategory(HttpServletRequest request, Model model) {  
		mergeUserInfo(model);
		return "user/blogCategory"; 
    } 
}
