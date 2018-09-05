package cn.wws.controller.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.util.Strings;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.wws.entity.User;
import cn.wws.util.PageingUtil;
import cn.wws.util.RequestUtil;

public class BaseController {
	
	/**    
    * @Description: 向前端页面传入用户信息. 
    * @author songjun  
    * @date 2018年5月2日   
    */ 
	protected void mergeUserInfo(Model model) {
		User user = RequestUtil.getRequestUser();
		if (user != null) {
			model.addAttribute("user", user);
			List<String> pseudonyms = RequestUtil.getRequestPseudonyms();
			model.addAttribute("pseudonyms", pseudonyms);
		}
	}
	
	protected Map<String, String> getPagingParam(HttpServletRequest request, Model model) {
		Map<String, String> pagingParam = null;
		String _page = request.getParameter("_page");
		model.addAttribute("_page", _page);
		if (Strings.isBlank(_page)) {
			_page = "1";
		}
		pagingParam = PageingUtil.getLimitMap(_page, "10");
		return pagingParam;
	}
}
