package cn.wws.util;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.wws.entity.User;

public class RequestUtil {
	
	/**
	 * @Description: 获取session
	 * @author: songjun 
	 * @date: 2018年5月3日 
	 * @return
	 */
	public static HttpSession getRequestSession() {
		HttpServletRequest request = ((ServletRequestAttributes) 
				RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession httpSession = request.getSession(false);
		return httpSession;
	}
	
	/**
	 * @Description: 获取User
	 * @author: songjun 
	 * @date: 2018年5月3日 
	 * @return
	 */
	public static User getRequestUser() {
		HttpSession httpSession = getRequestSession();
		if (httpSession == null) {
			return null;
		}
		return (User) httpSession.getAttribute("USER");
	}
	
	/**
	 * @Description: 获取笔名列表
	 * @author: songjun 
	 * @date: 2018年5月3日 
	 * @return
	 */
	public static List<String> getRequestPseudonyms() {
		HttpSession httpSession = getRequestSession();
		if (httpSession == null) {
			return null;
		}
		return (List<String>) httpSession.getAttribute("PSEUDONYMS");
	}
	
	/**
	 * @Description: 判断是否是内部ip
	 * @author: songjun 
	 * @date: 2018年5月3日 
	 * @return ret
	 */
	public static boolean isInnerIp(String ip) {
		boolean ret = false;
		if (ip.startsWith("10.") || ip.startsWith("192.168")) {
            ret = true;
        } else if (ip.startsWith("172.")) {
        	String[] ips = ip.split("\\.");
            int sec = Integer.parseInt(ips[1]);
            if (sec >= 16 && sec <= 31) {
                ret = true;
            } else {
            	ret = false;
            }
        } else {
        	ret = false;
        }
		return ret;
	}
}
