package cn.wws.interceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.wws.entity.AutoIntoDbStack;
import cn.wws.service.BaseService;
import cn.wws.util.RequestUtil;

public class BaseInterceptor implements HandlerInterceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(CheckRequestInterceptor.class);
	
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object arg2, Exception arg3)
			throws Exception {
		String uri = request.getRequestURI();
		if (uri.contains("/blog/info/")) {
			String blogId = uri.substring(uri.lastIndexOf("/blog/info/")+11);
			AutoIntoDbStack.addBlogVisitRecord(blogId);
		}
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object arg2) throws Exception {

		String ip = request.getHeader("x-forwarded-for");  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_CLIENT_IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getRemoteAddr();  
        }  
        if (RequestUtil.isInnerIp(ip)) {
        	/**内部ip直接跳过，不跟踪访问记录*/
        	return true;
        }
        String url = request.getRequestURL().toString();//获得客户端发送请求的完整url
        String params = request.getQueryString();//返回请求行中的参数部分
        Map<String, Object> visitLog = new HashMap<String, Object>();
        visitLog.put("clientIp", ip);
        visitLog.put("visitUrl", url);
        visitLog.put("visitParam", params);
        visitLog.put("operationTime", System.currentTimeMillis());
        AutoIntoDbStack.pushVisitLog(visitLog);
        LOGGER.debug("----------------------推入一条访问记录");
		return true;
	}

}
