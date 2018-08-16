package cn.wws.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AutoIntoDbStack {
	private static int size = 0;
	private static ConcurrentLinkedQueue<Map<String, Object>> visitLogQueue = new ConcurrentLinkedQueue<Map<String, Object>>();
	private static Map<String, Integer> visitCountMap = new HashMap<String, Integer>();
	
	private AutoIntoDbStack(){
		
	}
	
	/**
	 * 推入一条访问日志记录.
	 */
	public static void pushVisitLog(Map<String, Object> visitLog) {
		visitLogQueue.add(visitLog);
		size ++;
	}
	
	/**
	 * 获取单条访问日志记录.
	 */
	public static String getVisitLogSql() {
		String sql = null;
		Map<String, Object> map = visitLogQueue.poll();
		if (map != null) {
			size --;
			StringBuffer sb = new StringBuffer("insert into t_visit_log (client_ip, visit_url, visit_param, if_success, operation_time, create_time) values ");
			for( ; map != null || sb.length() >= 10000; ){
				sb.append("('").append(map.get("clientIp")).append("','").append(map.get("visitUrl")).append("','");
				sb.append(map.get("visitParam")).append("',1,'").append(map.get("operationTime")).append("',now()),");
				map = visitLogQueue.poll();
				if (map != null) {
					size --;
				}
			}
			sb.deleteCharAt(sb.length() - 1);
			sql = sb.toString();
		}
		return sql;
	}
	
	/**
	 * 添加一次日志访问记录.
	 */
	public static synchronized void addBlogVisitRecord(String blogId) {
		if (!visitCountMap.containsKey(blogId)){
			visitCountMap.put(blogId, 1);
		} else {
			visitCountMap.put(blogId, visitCountMap.get(blogId) + 1);
		}
	}
	
	/**
	 * 获取多条修改日志访问次数的sql.
	 */
	public static synchronized List<String> getBlogVisitSqls(){
		List<String> ret = null;
		if (visitCountMap.isEmpty()) {
			return ret;
		} else {
			ret = new ArrayList<String>();
		}
		for (Entry<String, Integer> entry : visitCountMap.entrySet()) {
			String sql = "update t_blog_info set visit_num = visit_num+" + entry.getValue() + " where id = " + entry.getKey();
			ret.add(sql);
		}
		visitCountMap.clear();
		return ret;
	}
	
}
