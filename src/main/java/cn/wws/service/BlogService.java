package cn.wws.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.wws.entity.ReturnResult;
import cn.wws.util.Base64Util;

@Service
public class BlogService {
	public static final Logger LOGGER = LoggerFactory.getLogger(BlogService.class);
	@Autowired
	BaseService baseService;
	
	/**
	 * @Description: 根据参数判断执行新增操作还是修改操作
	 * @author: songjun 
	 * @date: 2018年4月30日 
	 * @param map
	 * @return
	 */
	public ReturnResult saveBlog(Map<String, Object> map) {
		ReturnResult ret;
		if (map.get("blogId") == null || "".equals(map.get("blogId"))) {
			ret = insertBlog(map);
		} else {
			ret = updateBlog(map);
		}
		return ret;
	}
	
	/**
	 * @Description: 新增博客
	 * @author: songjun 
	 * @date: 2018年4月30日 
	 * @param map
	 * @return
	 */
	public ReturnResult insertBlog(Map<String, Object> map) {
		ReturnResult ret = new ReturnResult();
		int count = baseService.executeInsert("blog.insertBlog", map);
		if (count == 1) {
			ret.setSuccessMsg("保存日志成功");
		} else {
			ret.setFailMsg("保存日志失败");
		}
		return ret;
	}
	
	/**
	 * @Description: 修改博客内容
	 * @author: songjun 
	 * @date: 2018年4月30日 
	 * @param map
	 * @return
	 */
	public ReturnResult updateBlog(Map<String, Object> map) {
		ReturnResult ret = new ReturnResult();
		try{
			baseService.executeInsert("blog.updateBlog", map);
		} catch(Exception e) {
			LOGGER.error("笔记修改发生异常{}", e);
			ret.setFailMsg("笔记修改失败");
			return ret;
		}
		ret.setSuccessMsg("笔记修改成功");
		return ret;
	}
	//删除博客
	public int deleteBlog(int id) {
		Map<String, Object> param = new HashMap<>();
    	param.put("blogId", String.valueOf(id));
        return baseService.executeUpdate("blog.deleteBlog", param);
	}
	
	public void encodeBlogBase64(List<Map<String, Object>> list) {
        for (Map<String, Object> map : list) {
            if (map.get("blog_text") != null) {
                map.put("blog_text", Base64Util.encode((String)map.get("blog_text")));
            }
            if (map.get("blog_html") != null) {
                map.put("blog_html", Base64Util.encode((String)map.get("blog_html")));
            }
        }
    }
	
	public String encodeBlogBase64(String str) {
        if (str != null) {
            return Base64Util.encode(str);
        } else {
            return "";
        }
    }
	
	public <T> void decodeBlogBase64(List<Map<String, T>> list) {
		for (Map<String, T> map : list) {
			if (map.get("blog_text") != null) {
				map.put("blog_text", (T)Base64Util.decode(String.valueOf(map.get("blog_text"))));
			}
			if (map.get("blog_html") != null) {
				map.put("blog_html", (T)Base64Util.decode(String.valueOf(map.get("blog_html"))));
			}
		}
	}
	
	public <T> void handleBlogShort(List<Map<String, T>> list) {
        for (Map<String, T> map : list) {
            if (map.get("blog_text") != null && ((String)map.get("blog_text")).length() > 200) {
                String shortBlog = ((String)map.get("blog_text")).substring(0, 200);
                map.put("blog_text", (T) (shortBlog + "..."));
            }
        }
    }
}
