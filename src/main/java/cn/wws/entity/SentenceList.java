package cn.wws.entity;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.wws.service.BaseService;


@Service
public class SentenceList {
	private static List<Map<String, Object>> sentenceList = null;
	
	@Autowired
	private BaseService baseService;
	
    public void reloadSentence() {
    	if (sentenceList != null) {
    		synchronized (sentenceList) {
        		sentenceList = baseService.executeQuery("baseOperation.getSentence", null);
    		}
    	} else {
    		sentenceList = baseService.executeQuery("baseOperation.getSentence", null);
    	}
    }

	
	public Map<String, Object>  getSentence() {
		if (sentenceList == null) {
			reloadSentence();
		}
		return sentenceList.get((new Random()).nextInt(sentenceList.size()));
	}
}
