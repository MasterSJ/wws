package cn.wws.service.msg;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;

import cn.wws.service.RunLogService;

@Service
public class TencentSms {
    final int  APP_ID = 1400121017;
    final String APP_KEY = "d9b65ee38c5de866c3481c2a1e1efb24";
    
    @Autowired
    RunLogService runLogService;
    /**
     * @Description: 发送短信
     * @author: songjun 
     * @date: 2018年8月16日 
     * @param mobile
     * @param content
     */
    public void sendSms(String mobile, String content) {
    	if (StringUtils.isBlank(mobile)) {
    		return;
    	}
        try {
            SmsSingleSender ssender = new SmsSingleSender(APP_ID, APP_KEY);
            SmsSingleSenderResult result = ssender.send(0, "86", mobile,
                    content, "", "");
            if (result != null) {
            	if(result.result == 0) {
            		addSmsRunLog(mobile, content, null);
            	} else {
            		System.out.println("短信发送失败：" + result);
            		addSmsRunLog(mobile, content, result.toString());
            	}
            } else {
            	addSmsRunLog(mobile, content, "返回结果为null");
            }
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
            addSmsRunLog(mobile, content, e.getMessage());
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
            addSmsRunLog(mobile, content, e.getMessage());
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();
            addSmsRunLog(mobile, content, e.getMessage());
        }
    }
    
    /**
     * @Description: 添加短信发送记录
     * @author: songjun 
     * @date: 2018年8月16日 
     * @param mobile
     * @param smsContent
     * @param errMsg
     */
    public void addSmsRunLog(String mobile, String smsContent, String errMsg) {
    	JSONObject json = new JSONObject();
    	json.put("mobile", mobile);
    	json.put("smsContent", smsContent);
    	
    	Map<String, String> param = new HashMap<String, String>();
    	param.put("params", json.toJSONString());
    	param.put("logType", "sms");
    	param.put("logDesc", "发送纪念日提醒短信");
    	if (errMsg == null) {
        	param.put("logResult", "SUCCESS");
    	} else {
        	param.put("logResult", "FAIL");
    	}
    	param.put("errorMsg", errMsg);
    	param.put("operationTime", String.valueOf(System.currentTimeMillis()));
    	runLogService.writeRunLog(param);
    }

    /**
     * @Description: 生成短信内容
     * @author: songjun 
     * @date: 2018年8月16日 
     * @param remindInterval
     * @param monthDate
     * @param remindContent
     * @return
     */
    public String getSmsContent(int remindInterval, String monthDate, String remindContent) {
    	StringBuffer sb = new StringBuffer();
        if (remindInterval == 0) {
            sb.append("今天是");
        } else {
            sb.append(remindInterval).append("天后是");
        }
        sb.append(monthDate).append("，是您设置的纪念日，提醒内容：").append(remindContent).append("。回T退订。") ;
        return sb.toString();
    }
}
