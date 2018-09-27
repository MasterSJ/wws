package cn.wws;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.wws.consts.WechatConfigConst;
import cn.wws.service.SystemParamService;
import cn.wws.service.WechatOperateService;

@Component
@EnableScheduling
public class ScheduleManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleManager.class);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Autowired
    SystemParamService systemParamService;
    @Autowired
    WechatOperateService wechatOperateService;
    
    @Scheduled(fixedDelay = 1000 * 60 * 3)
    public void refreshAccessToken(){
        String strUpdateTime = systemParamService.getUpdateTime(WechatConfigConst.ACCESS_TOKEN);
        Date dateUpdateTime = new Date();
        long lastRefreshTime = -1;
        try {
            if (!StringUtils.isEmpty(strUpdateTime)) {
                dateUpdateTime = sdf.parse(strUpdateTime);
            }
        } catch (ParseException e) {
            LOGGER.info("access_token刷新时间转换异常，e={}", e);
            e.printStackTrace();
        }
        lastRefreshTime = dateUpdateTime.getTime();
        long longCurrentTime = System.currentTimeMillis();
        if (longCurrentTime - lastRefreshTime >= 100 * 60 * 1000) {
            LOGGER.info("已经满足刷新access_token时间条件，可以刷新lastRefreshTime={},longCurrentTime={}", lastRefreshTime, longCurrentTime);
            wechatOperateService.refreshAccessToken();
        } else {
            LOGGER.info("不需要刷新access_token。lastRefreshTime={},longCurrentTime={}", lastRefreshTime, longCurrentTime);
        }
    }
}
