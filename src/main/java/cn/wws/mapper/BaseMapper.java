package cn.wws.mapper;

import java.util.List;
import java.util.Map;

public interface BaseMapper<T> {

    public int executeInsert(String paramSql);
    
    public int executeDelete(String paramSql);
    
    public int executeUpdate(String paramSql);

    public List<Map<String, T>> executeQuery(String paramSql);
    
    public List<Map<String, T>> executeQueryReturnLinked(String paramSql);
    
    public Map<String, T> getRecordCount(String paramSql);
}
