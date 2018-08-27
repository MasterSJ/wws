package cn.wws.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemParamService {
    @Autowired
    BaseService baseService;
    
    HashMap<String, Map<String, String>> systemParamMap = new HashMap<String, Map<String, String>>();
    
    /**
     * 初始化
     * */
    public void init() {
        List<Map<String, String>> list = baseService.executeQuery("systemParam.getAllSystemParam", null);
        synchronized (SystemParamService.class) {
            systemParamMap.clear();
            for (Map<String, String> map : list) {
                systemParamMap.put(map.get("param_code"), map);
            }
        }
    }
    
    /**
     * 获取Map类型系统变量
     * */
    public Map<String, String> get(String paramCode) {
        if (systemParamMap.isEmpty()) {
            init();
        }
        return systemParamMap.get(paramCode);
    }
    
    /**
     * 获取系统变量的值
     * */
    public String getParamValue(String paramCode) {
        if (systemParamMap.isEmpty()) {
            init();
        }
        Map<String, String> map = get(paramCode);
        if (map != null) {
            return map.get("param_value");
        }
        return null;
    }
    
    /**
     * 获取系统变量更新时间
     * */
    public String getUpdateTime(String paramCode) {
        if (systemParamMap.isEmpty()) {
            init();
        }
        Map<String, String> map = get(paramCode);
        if (map != null) {
            return map.get("updated_time");
        }
        return null;
    }
    
    /**
     * 设置系统变量的值，并重新初始化系统变量
     * */
    public void setParamValue(String paramCode, String paramValue) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("paramCode", paramCode);
        param.put("paramValue", paramValue);
        baseService.executeUpdate("systemParam.updateSystemParam", param);
        init();
    }
}
