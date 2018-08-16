package cn.wws;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import cn.wws.service.AnniversaryService;
import cn.wws.service.RunLogService;
import cn.wws.util.Lunar;
import cn.wws.util.SendQqemail;

@Component
@EnableScheduling
public class RemindEmailScheduleRun {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemindEmailScheduleRun.class);
    SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
    
    @Autowired
    AnniversaryService anniversaryService;

    @Autowired
    RunLogService runLogService;
    
            

//	@Scheduled(fixedDelay = 1000 * 60 * 5)
//	public void initKeyWords() throws Exception {
//		mpKeyword.init();
//	}

    /**
     * 每天7点触发
     */
    @Scheduled(cron = "0 0 7 * * ?")
    //@Scheduled(fixedDelay = 1000 * 60 * 1)
    public void remind() {
        Date today = new Date();
        String solarMonthDay = sdf.format(today);
        Lunar lunar = new Lunar();
        String lunarMonthDay = lunar.getLunar(today, false);
        //发送当天提醒邮件
        sendRemindEmail(solarMonthDay, 0);
        sendRemindEmail(lunarMonthDay, 0);
        
        Date date1 = lunar.getSeveralDayLater(today, 1);
        String lunarMonthDay1 = lunar.getLunar(date1, false);
        String solarMonthDay1 = sdf.format(date1);
        //发送提前1天提醒邮件
        sendRemindEmail(solarMonthDay1, 1);
        sendRemindEmail(lunarMonthDay1, 1);
        
        Date date3 = lunar.getSeveralDayLater(today, 3);
        String lunarMonthDay3 = lunar.getLunar(date3, false);
        String solarMonthDay3 = sdf.format(date3);
        //发送提前3天提醒邮件
        sendRemindEmail(solarMonthDay3, 3);
        sendRemindEmail(lunarMonthDay3, 3);
        
        Date date7 = lunar.getSeveralDayLater(today, 7);
        String lunarMonthDay7 = lunar.getLunar(date7, false);
        String solarMonthDay7 = sdf.format(date7);
        //发送提前7天提醒邮件
        sendRemindEmail(solarMonthDay7, 7);
        sendRemindEmail(lunarMonthDay7, 7);
        
    }
    
    private void sendRemindEmail(String anniversaryMonthDate, int remindInterval) {
    	List<Map<String, String>> list = anniversaryService.getNeedRemindInfo(anniversaryMonthDate, remindInterval);
    	if (list != null && !list.isEmpty()) {
    		for (Map<String, String> map : list) {
    			String receiver = map.get("remind_email");
    			String title = "纪念日提醒";
    			String content = getMsgContext(remindInterval, 
    			        map.get("anniversary_month") + map.get("anniversary_date"), map.get("remind_content"));
    			try {
					SendQqemail.sendEmail(receiver, title, content);
					addEmailRunLog(receiver, title, content, null);
				} catch (MessagingException e) {
					e.printStackTrace();
					LOGGER.error("发送邮件时发生了异常e={}", e);
					addEmailRunLog(receiver, title, content, e.toString());
				}
    		}
    	}
    }
    
    private String getMsgContext(int remindInterval, String anniversaryMonthDate, String remindContent) {
        StringBuffer sb = new StringBuffer();
        if (remindInterval == 0) {
            sb.append("今天");
        } else {
            sb.append(remindInterval).append("天后");
        }
        sb.append("是").append(anniversaryMonthDate).append("，是您设置的纪念日，提醒内容：").append(remindContent) ;
        return sb.toString();
    }
    
    private void addEmailRunLog(String receiver, String title, String content, String errorMsg) {
    	JSONObject json = new JSONObject();
    	json.put("receiver", receiver);
    	json.put("title", title);
    	json.put("content", content);
    	
    	Map<String, String> param = new HashMap<String, String>();
    	param.put("params", json.toJSONString());
    	param.put("logType", "email");
    	param.put("logDesc", "发送纪念日提醒邮件");
    	if (errorMsg == null) {
        	param.put("logResult", "SUCCESS");
    	} else {
        	param.put("logResult", "FAIL");
    	}
    	param.put("errorMsg", errorMsg);
    	param.put("operationTime", String.valueOf(System.currentTimeMillis()));
    	runLogService.writeRunLog(param);
    }

}
