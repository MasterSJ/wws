package cn.wws.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.wws.mapper.BaseMapper;
import cn.wws.util.MapperUtil;
import cn.wws.util.SqlUtil;



@Service
public class BaseService<T> {
    @Autowired
    private BaseMapper mapper;
    
    public int executeInsert(String sqlId, Map<String, ?> paramMap) {
        String sql = SqlUtil.getSql(sqlId, paramMap);
        return mapper.executeInsert(sql);
    }
    
    public int executeDelete(String sqlId, Map<String, ?> paramMap) {
        String sql = SqlUtil.getSql(sqlId, paramMap);
        return mapper.executeDelete(sql);
    }
    
    public int executeUpdate(String sqlId, Map<String, ?> paramMap) {
        String sql = SqlUtil.getSql(sqlId, paramMap);
        return mapper.executeUpdate(sql);
    }
    
    public List<Map<String, T>> executeQuery(String sqlId, Map<String, ?> paramMap) {
        String sql = SqlUtil.getSql(sqlId, paramMap);
        return mapper.executeQuery(sql);
    }
    
    public List<T> executeQuery(String sqlId, Map<String, ?> paramMap, Class<T> t) {
        String sql = SqlUtil.getSql(sqlId, paramMap);
        List<Map<String, Object>> list = mapper.executeQuery(sql);
        try{
            return MapperUtil.getInstance().mapperList(list, t);
        } catch(Exception e) {
            return null;
        }
        
    }
    
    public Map<String, T> executeQuerySingle(String sqlId, Map<String, ?> paramMap) {
        String sql = SqlUtil.getSql(sqlId, paramMap);
        List<Map<String, T>> list = mapper.executeQuery(sql);
        if (list == null || list.isEmpty()) {
            return new HashMap<String, T>();
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            throw new RuntimeException(sql+" 返回不止一条记录");
        }
    }
    
    public List<Map<String, T>> executeLimitQuery(String sqlId, 
            Map<String, ?> paramMap, Map<String,String> limitMap) {
        String sql = SqlUtil.getSql(sqlId, paramMap);
        if (limitMap != null) {
            String limitOffset = limitMap.get("_limitOffset");
            String limitRows = limitMap.get("_limitRows");
            StringBuffer limitStr = new StringBuffer("select sub.* from (");
            limitStr.append(sql).append(") sub");
            if (!StringUtils.isEmpty(limitMap.get("_order"))) {
                limitStr.append(" order by ").append(limitMap.get("_order"));
            }
            limitStr.append(" limit ").append(limitOffset).append(",").append(limitRows);
            sql = limitStr.toString();
        }
        return mapper.executeQuery(sql);
    }
}
