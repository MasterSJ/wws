package cn.wws.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import cn.wws.entity.User;

public class BusiRulesUtils {
	public static String getStringValue(Object obj){
		if (obj == null) {
			return "";
		} else if (StringUtils.isBlank(obj.toString())) {
				return "";
		} else {
			return obj.toString();
		}
	}
	
	public static User getSigninUser(HttpServletRequest request) {
		HttpSession httpSession = request.getSession(false);
		if (httpSession == null) {
			return null;
		}
		User user = (User) httpSession.getAttribute("USER");
		return user;
	}
}
