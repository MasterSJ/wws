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
import cn.wws.service.BlogService;
import cn.wws.util.Base64Util;
import cn.wws.util.BusiRulesUtils;
import java.text.SimpleDateFormat;


@Controller  
public class BlogController extends BaseController {
	@Autowired
	BaseService baseService;
	@Autowired
	BlogService blogService;

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置格式

	@RequestMapping("/showBlog")
	public String showBlog(HttpServletRequest request, Model model) {
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

	/**
	 * @param request
	 * @param model
	 * @return
	 * @Description: 查看“我的博客”
	 * @author: songjun
	 * @date: 2018年4月30日
	 */
	@RequestMapping("/showMyBlog")
	public String showMyBlog(HttpServletRequest request, Model model) {
		Map<String, String> pagingParam = getPagingParam(request, model);
		pagingParam.put("_order", "operation_time DESC");

		User user = BusiRulesUtils.getSigninUser(request);
		Map<String, String> map = new HashMap<String, String>();
		map.put("userName", user.getUserName());
		String startTime = request.getParameter("startTime");
		map.put("startTime", startTime);
		String endTime = request.getParameter("endTime");
		map.put("endTime", endTime);
		String title = request.getParameter("title");
		map.put("title", title);
		List<Map<String, Object>> list = baseService.executeLimitQuery("blog.getMyBlog", map, pagingParam);
		blogService.decodeBlogBase64(list);
		blogService.handleBlogShort(list);
		if (list.size() >= 1) {
			model.addAttribute("topBlogList", list);
			model.addAttribute("startTime", startTime);
			model.addAttribute("endTime", endTime);
			model.addAttribute("title", title);
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
	
	/**删除纪念日*/
    @RequestMapping("/delBlog") 
    @ResponseBody
    public ReturnResult delBlog(HttpServletRequest request,
            @RequestParam int id) {
    	ReturnResult rst = new ReturnResult();
        if (id <= 0) {
            rst.setFailMsg("参数异常！");
            return rst;
        }
        int count = blogService.deleteBlog(id);
        if(count > 0){
        	rst.setSuccess();
        }
        return rst; 
    }

	@RequestMapping("/showWriteBlog")
	public String showWriteBlog(HttpServletRequest request, HttpServletResponse response, Model model) {
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
    @ResponseBody
	public ReturnResult writeBlog(HttpServletRequest request, @RequestBody Map<String, String> param) throws Exception {
		ReturnResult rst = new ReturnResult();
        if (param == null || param.isEmpty()) {
            rst.setFailMsg("参数为空！");
            return rst;
        }
        try{
			String blogId = BusiRulesUtils.getStringValue(param.get("blogId"));
			String htmlV = Base64Util.encode(BusiRulesUtils.getStringValue(param.get("htmlV")));
			String textV = Base64Util.encode(BusiRulesUtils.getStringValue(param.get("textV")));
			String title = BusiRulesUtils.getStringValue(param.get("title"));
			String pseudonymId = BusiRulesUtils.getStringValue(param.get("pseudonymId"));
			String pseudonymName = BusiRulesUtils.getStringValue(param.get("pseudonymName"));
			String isVisible = BusiRulesUtils.getStringValue(param.get("isVisible"));
			htmlV = StringEscapeUtils.escapeHtml4(htmlV);
			if (StringUtils.isBlank(htmlV) || StringUtils.isBlank(textV) || StringUtils.isBlank(title)) {
				rst.setFailMsg("笔记标题和内容不能为空");
				return rst;
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
			rst.setSuccess();
        } catch(Exception e){
        	rst.setFailMsg("保存笔记时发生异常");
        }
        return rst; 
	}

	@RequestMapping("/blog/info/{blogId}")
	public String blogInfo(@PathVariable String blogId, Model model) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("blogId", blogId);
		List<Map<String, Object>> list = baseService.executeQuery("blog.getBlogInfo", map);
		blogService.decodeBlogBase64(list);
		if (list.size() == 1) {
			Map<String, Object> blogMap = list.get(0);
			model.addAttribute("id", blogMap.get("id"));
			model.addAttribute("blogHtml", StringEscapeUtils.unescapeHtml4((String) blogMap.get("blog_html")));
			model.addAttribute("blogTitle", (String) blogMap.get("blog_title"));
			model.addAttribute("likeNum", blogMap.get("like_num"));
			model.addAttribute("visitNum", blogMap.get("visit_num"));
			long opTime = (Long) blogMap.get("operation_time");
			model.addAttribute("operationTime", format.format(opTime * 1000));
		}

		List<Map<String, String>> pseList = baseService.executeQuery("blog.getPseBlog", map);
		model.addAttribute("pseBlogs", pseList);
		return "blog/blogInfo";
	}

	@RequestMapping("/likeFlag/{blogId}")
	@ResponseBody
	public String likeFlag(@PathVariable String blogId, Model model) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("blogId", blogId);
		baseService.executeQuery("blog.likeFlag", map);
		return "success";
	}
}
