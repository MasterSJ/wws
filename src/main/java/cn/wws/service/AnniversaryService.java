package cn.wws.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnniversaryService {
    public static final Logger LOGGER = LoggerFactory.getLogger(AnniversaryService.class);
    @Autowired
    BaseService baseService;
    
    /**
     * @descript 根据userName获取纪念日详情
     * @param userName
     * @return
     */
    public List<Map<String, String>> getAnniversaryByUserName(String userName) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userName", userName);
        List<Map<String, String>> list = baseService.executeQuery(
                "anniversary.selectAnniversaries", param);
        return list;
    }
    
    /**
     * @descript 根据id获取纪念日详情
     * @param id
     * @return
     */
    public Map<String, String> getAnniversaryById(String id) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("id", id);
        Map<String, String> map = baseService.executeQuerySingle("anniversary.selectAnniversaries", param);
        return map;
    }
    
    public int insertAnniversary(Map<String, String> param) {
        return baseService.executeInsert("anniversary.insertAnniversaries", param);
    }
    
    public int updateAnniversary(Map<String, String> param) {
        return baseService.executeUpdate("anniversary.updateAnniversaries", param);
    }
    
    /**
     * @Description: 查看要提醒的纪念日信息.
     * @author: songjun 
     * @date: 2018年7月28日 
     * @param anniversaryMonthDate
     * @param remindInterval
     * @return
     */
    public List<Map<String, String>> getNeedRemindInfo(String anniversaryMonthDate, int remindInterval) {
    	Map<String, Object> param = new HashMap<String, Object>();
        param.put("anniversaryMonthDate", anniversaryMonthDate);
        param.put("remindInterval", remindInterval);
        List<Map<String, String>> remindList = baseService.executeQuery("anniversary.selectNeedRemindInfo", param);
        return remindList;
    }
}
