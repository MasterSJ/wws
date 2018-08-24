package cn.wws;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.wws.service.AnniversaryService;
import cn.wws.service.msg.EmailMsg;
import cn.wws.service.msg.TencentSms;
import cn.wws.util.Lunar;

@Component
@EnableScheduling
public class RemindEmailScheduleRun {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemindEmailScheduleRun.class);
    SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
    
    @Autowired
    AnniversaryService anniversaryService;

    @Autowired
    TencentSms tencentSms;

    @Autowired
    EmailMsg emailMsg;

    /**
     * 每天7点触发
     */
    @Scheduled(cron = "0 0 7 * * ?")
    //@Scheduled(fixedDelay = 1000 * 60 * 10)
    public void remind() {
        Date today = new Date();
        String solarMonthDay = sdf.format(today);
        Lunar lunar = new Lunar();
        String lunarMonthDay = lunar.getLunar(today, false);
        //发送当天提醒邮件
        sendRemindInfo(solarMonthDay, 0);
        sendRemindInfo(lunarMonthDay, 0);
        
        Date date1 = lunar.getSeveralDayLater(today, 1);
        String lunarMonthDay1 = lunar.getLunar(date1, false);
        String solarMonthDay1 = sdf.format(date1);
        //发送提前1天提醒邮件
        sendRemindInfo(solarMonthDay1, 1);
        sendRemindInfo(lunarMonthDay1, 1);
        
        Date date3 = lunar.getSeveralDayLater(today, 3);
        String lunarMonthDay3 = lunar.getLunar(date3, false);
        String solarMonthDay3 = sdf.format(date3);
        //发送提前3天提醒邮件
        sendRemindInfo(solarMonthDay3, 3);
        sendRemindInfo(lunarMonthDay3, 3);
        
        Date date7 = lunar.getSeveralDayLater(today, 7);
        String lunarMonthDay7 = lunar.getLunar(date7, false);
        String solarMonthDay7 = sdf.format(date7);
        //发送提前7天提醒邮件
        sendRemindInfo(solarMonthDay7, 7);
        sendRemindInfo(lunarMonthDay7, 7);
    }
    
    private void sendRemindInfo(String anniversaryMonthDate, int remindInterval) {
    	List<Map<String, String>> list = anniversaryService.getNeedRemindInfo(anniversaryMonthDate, remindInterval);
    	if (list != null && !list.isEmpty()) {
    		for (Map<String, String> map : list) {
				String smsContent = tencentSms.getSmsContent(remindInterval,  map.get("anniversary_month") + map.get("anniversary_date"), map.get("remind_content"));
				tencentSms.sendSms(map.get("remind_mobile"), smsContent);
				
				String emailContent = emailMsg.getEmailContent(remindInterval, map.get("anniversary_month") + map.get("anniversary_date"), map.get("remind_content"));
				emailMsg.sendEmail(map.get("remind_email"), emailContent);
    		}
    	}
    }
    
    
    

}
