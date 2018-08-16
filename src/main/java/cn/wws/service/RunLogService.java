package cn.wws.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RunLogService {
	public static final Logger LOGGER = LoggerFactory.getLogger(RunLogService.class);
	
	@Autowired
	BaseService baseService;
	
	public int writeRunLog(Map<String, String> param) {
		return baseService.executeInsert("runlog.insertRunLog", param);
	}
}
