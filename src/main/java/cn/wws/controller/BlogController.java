package cn.wws.controller;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.wws.annotation.CheckLogin;
import cn.wws.controller.base.BaseController;
import cn.wws.entity.ReturnResult;
import cn.wws.entity.User;
import cn.wws.service.BaseService;
import cn.wws.service.BlogService;
import cn.wws.util.Base64Util;
import cn.wws.util.BusiRulesUtils;


@Controller  
public class BlogController extends BaseController{
	@Autowired
	BaseService baseService;
	@Autowired
	BlogService blogService;
	

	  @RequestMapping("/showBlog")  
	  public String showBlog(HttpServletRequest request,Model model){  
		  Map<String, String> pagingParam = getPagingParam(request, model);
		  String order = request.getParameter("_order");
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
	  
	  /**
	 * @Description: 查看“我的博客”
	 * @author: songjun 
	 * @date: 2018年4月30日 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/showMyBlog")  
	public String showMyBlog(HttpServletRequest request,Model model) {
		  Map<String, String> pagingParam = getPagingParam(request, model);
		  pagingParam.put("_order", "operation_time DESC");
		  
		  User user = BusiRulesUtils.getSigninUser(request);
		  Map<String, String> map = new HashMap<String, String>();
		  map.put("userName", user.getUserName());
          String author = request.getParameter("author");
          map.put("author", author);
          String startTime = request.getParameter("startTime");
          map.put("startTime", startTime);
          String endTime = request.getParameter("endTime");
          map.put("endTime", endTime);
          String title = request.getParameter("title");
          map.put("title", title);
          String content = request.getParameter("content");
          map.put("content", blogService.encodeBlogBase64(content));
		  List<Map<String, Object>> list = baseService.executeLimitQuery("blog.getMyBlog", map, pagingParam);
		  blogService.decodeBlogBase64(list);
		  blogService.handleBlogShort(list);
		  if (list.size() >= 1) {
			  model.addAttribute("topBlogList", list);
			  //StringEscapeUtils.unescapeHtml4((String)blogMap.get("blog_html")));
		  }
		  return "user/myBlog";
	}
	@RequestMapping("/showMyBlogInfo")
	public String showMyBlogInfo(HttpServletRequest request, HttpServletResponse response, Model model) {
		mergeUserInfo(model);
		  String blogId = request.getParameter("blogId");
		  if (Strings.isBlank(blogId)) {
			  return "user/userInfo";
		  } 
		  Map<String, String> map = new HashMap<String, String>();
		  map.put("blogId", blogId);
		  List<Map<String, Object>> list = baseService.executeQuery("blog.getBlogById", map);
		  blogService.decodeBlogBase64(list);
		  if (list.size() == 1) {
			  model.addAttribute("blogId", list.get(0).get("blog_id"));
			  model.addAttribute("blogTitle", list.get(0).get("blog_title"));
			  model.addAttribute("blogText", list.get(0).get("blog_text"));
			  model.addAttribute("blogHtml", StringEscapeUtils.unescapeHtml4((String) list.get(0).get("blog_html")));
			  model.addAttribute("isVisible", list.get(0).get("is_visible"));
			  model.addAttribute("pseudonymId", list.get(0).get("pseudonym_id"));
			  model.addAttribute("pseudonymName", list.get(0).get("pseudonym_name"));
		  }
		  return "user/myBlogInfo";  
	}
	  
	  @RequestMapping("/showWriteBlog")  
	  public String showWriteBlog(HttpServletRequest request, HttpServletResponse response, Model model){  
		  mergeUserInfo(model);
		  String blogId = request.getParameter("blogId");
		  if (Strings.isBlank(blogId)) {
			  return "blog/writeBlog";
		  } 
		  Map<String, String> map = new HashMap<String, String>();
		  map.put("blogId", blogId);
		  List<Map<String, Object>> list = baseService.executeQuery("blog.getBlogById", map);
		  blogService.decodeBlogBase64(list);
		  if (list.size() == 1) {
			  model.addAttribute("blogId", list.get(0).get("blog_id"));
			  model.addAttribute("blogTitle", list.get(0).get("blog_title"));
			  model.addAttribute("blogText", list.get(0).get("blog_text"));
			  model.addAttribute("blogHtml", StringEscapeUtils.unescapeHtml4((String) list.get(0).get("blog_html")));
			  model.addAttribute("isVisible", list.get(0).get("is_visible"));
			  model.addAttribute("pseudonymId", list.get(0).get("pseudonym_id"));
			  model.addAttribute("pseudonymName", list.get(0).get("pseudonym_name"));
		  }
		  return "blog/writeBlog";  
	  }
	  
	  @RequestMapping("/doWriteBlog")  
	  public void writeBlog(HttpServletRequest request, HttpServletResponse response) throws Exception { 
		  JSONObject ret = new JSONObject();
		  PrintWriter out = response.getWriter();
		  String blogId = BusiRulesUtils.getStringValue(request.getParameter("blogId"));
		  String htmlV = Base64Util.encode(BusiRulesUtils.getStringValue(request.getParameter("htmlV")));
		  String textV = Base64Util.encode(BusiRulesUtils.getStringValue(request.getParameter("textV")));
		  String title = BusiRulesUtils.getStringValue(request.getParameter("title"));
		  String pseudonymId = BusiRulesUtils.getStringValue(request.getParameter("pseudonymId"));
		  String pseudonymName = BusiRulesUtils.getStringValue(request.getParameter("pseudonymName"));
		  String isVisible = BusiRulesUtils.getStringValue(request.getParameter("isVisible"));
		  htmlV = StringEscapeUtils.escapeHtml4(htmlV);
		  response.setCharacterEncoding("UTF-8");
		  if (StringUtils.isBlank(htmlV) || StringUtils.isBlank(textV) || StringUtils.isBlank(title)) {
			  ret.put("retCode", "0");
			  ret.put("retMessage", "笔记标题和内容不能为空");
			  out.write(ret.toJSONString());
			  out.close();
			  return;
		  }
		  Map<String, Object> map = new HashMap<String, Object>();
		  map.put("blogId", blogId);
		  map.put("htmlV", htmlV);
		  map.put("textV", textV);
		  map.put("title", title);
		  map.put("pseudonymId", pseudonymId);
		  map.put("pseudonymName", pseudonymName);
		  map.put("isVisible", isVisible);
		  User user = BusiRulesUtils.getSigninUser(request);
		  map.put("userName", user.getUserName());
		  ReturnResult saveRst = blogService.saveBlog(map);
		  out.write(saveRst.toJsonString());
		  out.close();
		  return;
	  }
	  
	  @RequestMapping("/blog/info/{blogId}")  
	  public String blogInfo(@PathVariable String blogId, Model model){  
		  Map<String, Object> map = new HashMap<String, Object>();
		  map.put("blogId", blogId);
		  List<Map<String, Object>> list = baseService.executeQuery("blog.getBlogInfo", map);
		  blogService.decodeBlogBase64(list);
		  if (list.size() == 1) {
			  Map<String, Object> blogMap = list.get(0);
			  model.addAttribute("blogHtml", StringEscapeUtils.unescapeHtml4((String)blogMap.get("blog_html")));
			  model.addAttribute("blogTitle", (String)blogMap.get("blog_title"));
		  }
		  
		  List<Map<String, String>> pseList = baseService.executeQuery("blog.getPseBlog", map);
		  model.addAttribute("pseBlogs", pseList);
	      return "blog/blogInfo";  
	  }
	  
}
