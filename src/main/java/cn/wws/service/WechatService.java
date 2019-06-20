package cn.wws.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WechatService {
    @Autowired
    BaseService baseService;
    
    /**
     * @Description: 根据openId获取userName
     * @author: songjun 
     * @date: 2018年5月2日 
     * @param userName
     * @param pwd
     * @return
     * @throws Exception 
     */
    public String getUserNameByOpenId(String openId) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("openId", openId);
        List<Map<String, String>> list = baseService.executeQuery("wechat.getUserNameByOpenId", map);
        if (list.size() == 1) {
            return list.get(0).get("user_name");
        }
        return null;
    }
    
    public List<Map<String, Object>> getAnniversariesByOpenid(String openId){
        Map<String, Object> map = new HashMap<>();
        map.put("openId", openId);
        List<Map<String, Object>> rst = baseService.executeQuery("wechat.getAnniversariesByOpenid", map);
        return rst;
    }
}
