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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


import com.alibaba.fastjson.JSONObject;

import cn.wws.controller.base.BaseController;
import cn.wws.entity.SentenceList;
import cn.wws.entity.User;
import cn.wws.service.BaseService;
import cn.wws.service.BlogService;

@Controller  
public class MainController extends BaseController{
	@Autowired
	BaseService baseService;
	
	@Autowired
	SentenceList sentenceService;
	@Autowired
	BlogService blogService;
	
	@RequestMapping("/main")  
    public String toIndex(HttpServletRequest request,Model model){  
		Map<String, String> pagingParam = getPagingParam(request, model);
		String order = request.getParameter("_order");
		if(StringUtils.isEmpty(order)){
			order = "operation_time";
		}
		pagingParam.put("_order", order + " desc");
		model.addAttribute("_order", order);

		List<Map<String, String>> list = baseService.executeLimitQuery("blog.getTopBlog", null, pagingParam);
		blogService.decodeBlogBase64(list);
		blogService.handleBlogShort(list);
		if (list.size() >= 1) {
			model.addAttribute("topBlogList", list);
			//StringEscapeUtils.unescapeHtml4((String)blogMap.get("blog_html")));
		}
		return "blog/blog";  
    }  
	
	@RequestMapping("/")  
    public String toIndex1(HttpServletRequest request,Model model){  
        return toIndex(request, model);
    }  

	@RequestMapping("/showProfile")
	public String showProfile() {
		return "base/profile";
	}
	
	/**
	 * 缺省路径，取代因为404而自动跳转到/error的请求；
	 * 这样就能被BaseInterceptor捕获到真实的请求路径
	 * 否则一直捕获到跳转之后的/error这个路径
	 */
	@RequestMapping("/{suburi}")  
    public String test(HttpServletRequest request, @PathVariable String suburi, Model model){  
        return "base/error";
    }
		
}
