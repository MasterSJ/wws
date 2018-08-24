package cn.wws.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.wws.entity.User;
import cn.wws.util.EnDecryptUtil;
import cn.wws.util.RequestUtil;

@Service
public class SigninService {

	@Autowired
	BaseService baseService;
	
	/**
	 * @Description: 登录（user信息写入session）
	 * @author: songjun 
	 * @date: 2018年5月2日 
	 * @param userName
	 * @param pwd
	 * @return
	 * @throws Exception 
	 */
	public boolean login(String userName, String pwd) throws Exception {
		boolean ret = false;
		pwd = EnDecryptUtil.encrypt(pwd);
		Map<String, String> map = new HashMap<String, String>();
		map.put("userName", userName);
		map.put("userPwd", pwd);
		List<Map<String, String>> list= baseService.executeQuery("user.login", map);
		if (list.size() == 1) {
			Map<String, String> userMap = list.get(0);
			User user = new User(userMap);
			HttpServletRequest request = ((ServletRequestAttributes) 
					RequestContextHolder.getRequestAttributes()).getRequest();
			HttpSession httpSession = request.getSession(true);
			httpSession.setAttribute("USER", user);
			/**session中写入pseudonym*/
			cachedPseudonym(map);
			ret =  true;
		} else {
			ret = false;
		}
		return ret;
	}
	
	/**
	 * @Description: session中存入笔名（入参map可以为null）
	 * @author: songjun 
	 * @date: 2018年5月3日 
	 * @param map
	 */
	public void cachedPseudonym(Map<String, String> map) {
		HttpSession httpSession = RequestUtil.getRequestSession();
		if (httpSession != null) {
			if (map == null || map.get("userName") == null) {
				User user = (User) httpSession.getAttribute("USER");
				map = new HashMap<String, String>();
				map.put("userName", user.getUserName());
			}
			List<Map<String, Object>> userPseudonyms = baseService.executeQuery("user.selectUserPseudonym", map);
			List<Map<Long, String>> pseudonyms = new ArrayList<Map<Long, String>>();
			if (!userPseudonyms.isEmpty()) {
				for(Map<String, Object> pseMap : userPseudonyms) {
					Map<Long, String> m = new HashMap<Long, String>();
					m.put((Long) pseMap.get("pseudonym_id"), (String) pseMap.get("pseudonym"));
					pseudonyms.add(m);
				}
			} 
			httpSession.setAttribute("PSEUDONYMS", userPseudonyms);
		}
	}
}
