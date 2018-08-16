package cn.wws.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.wws.entity.ReturnResult;

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
}
