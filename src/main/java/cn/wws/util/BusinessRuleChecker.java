package cn.wws.util;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;

import cn.wws.entity.KeyWord;
import cn.wws.entity.ReturnResult;

/**
 * @ClassName: BusinessRuleChecker  
 * @Description: 业务规则校验器
 * @author: songjun 
 * @date: 2018年4月27日  
 */
public class BusinessRuleChecker {
	/**
	 * @Description: 密码校验
	 * @author: songjun 
	 * @date: 2018年4月27日 
	 * @param pwd
	 * @return
	 */
	public static ReturnResult pwdCheck(String pwd) {
		ReturnResult ret = new ReturnResult();
		if (Strings.isNullOrEmpty(pwd)) {
			ret.setMsg(KeyWord.ERROR_MSG_1);
		} else if (pwd.length() > 20) {
			ret.setMsg(KeyWord.ERROR_MSG_2);
		} else {
			ret.setSuccess();
		}
		return ret;
	}
	
	/**
	 * @Description: 用户名校验
	 * @author: songjun 
	 * @date: 2018年4月27日 
	 * @param userName
	 * @return
	 */
	public static ReturnResult userNameCheck(String userName) {
		ReturnResult ret = new ReturnResult();
		if (Strings.isNullOrEmpty(userName)) {
			ret.setMsg(KeyWord.ERROR_MSG_3);
		} else if (userName.length() > 30) {
			ret.setMsg(KeyWord.ERROR_MSG_4);
		} else if (!checkStringConsist(userName)) {
			ret.setMsg(KeyWord.ERROR_MSG_5);
		} else {
			ret.setSuccess();
		}
		return ret;
	}
	
	/**
	 * @Description: 判断字符串是否只由数字、字母、下划线、“@”、“.”构成
	 * @author: songjun 
	 * @date: 2018年4月27日 
	 * @param str
	 * @return 
	 */
	public static boolean checkStringConsist(String str) {
		Pattern pt = Pattern.compile("^[0-9a-zA-Z_@.]+$");
		Matcher mt = pt.matcher(str);
		if(mt.matches()){
			return true;
	    }
	    return false;
	}
	
	/**
	 * @Description: 两次输入密码是否一致。
	 * @author: songjun 
	 * @date: 2018年4月27日 
	 * @param userPwd
	 * @param reUserPwd
	 * @return
	 */
	public static ReturnResult isSamePassword(String userPwd, String reUserPwd) {
		ReturnResult ret = new ReturnResult();
		if (!userPwd.equals(reUserPwd)) {
			ret.setMsg(KeyWord.ERROR_MSG_6);
		} else {
			ret.setSuccess();
		}
		return ret;
	}
	
}
