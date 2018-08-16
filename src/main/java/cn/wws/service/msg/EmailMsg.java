package cn.wws.service.msg;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import cn.wws.service.RunLogService;

@Service
public class EmailMsg {
	private static final String user = "wan____an@126.com";
	private static final String password = "dickbs18";
	
	@Autowired
    RunLogService runLogService;
	
	public void sendEmail(String receiver, String content) {
		if (StringUtils.isBlank(receiver)) {
			return;
        }
		try {
			sendEmail(receiver, "纪念日提醒", content);
			addEmailRunLog(receiver, "纪念日提醒", content, null);
		} catch (Exception e) {
			addEmailRunLog(receiver, "纪念日提醒", content, e.getMessage());
		}
	}
	
	public void sendEmail(String toAddr, String subject, String content) throws MessagingException
    {
         
        // 配置发送邮件的环境属性
        final Properties props = new Properties();
        /*
         * 可用的属性： mail.store.protocol / mail.transport.protocol / mail.host /
         * mail.user / mail.from
         */
        // 表示SMTP发送邮件，需要进行身份验证
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.126.com");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.debug", "true");//遇到最多的坑就是下面这行，不加要报“A secure connection is requiered”错。
        props.put("mail.smtp.starttls.enable", "true");
        // 发件人的账号
        props.put("mail.user", user);
        // 访问SMTP服务时需要提供的密码
        props.put("mail.password", password);
 
        // 构建授权信息，用于进行SMTP进行身份验证
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // 用户名、密码
                String userName = props.getProperty("mail.user");
                String password = props.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };
        // 使用环境属性和授权信息，创建邮件会话
        Session mailSession = Session.getInstance(props, authenticator);
        // 创建邮件消息
        MimeMessage message = new MimeMessage(mailSession);
        // 设置发件人
        InternetAddress form = new InternetAddress(
                props.getProperty("mail.user"));
        message.setFrom(form);
 
        // 设置收件人
        InternetAddress to = new InternetAddress(toAddr);
        message.setRecipient(RecipientType.TO, to);
 
        // 设置邮件标题
        message.setSubject(subject);
 
        // 设置邮件的内容体
        message.setContent(content, "text/html;charset=UTF-8");
 
        // 发送邮件
        Transport.send(message);
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
	
	/**
     * @Description: 生成短信内容
     * @author: songjun 
     * @date: 2018年8月16日 
     * @param remindInterval
     * @param monthDate
     * @param remindContent
     * @return
     */
    public String getEmailContent(int remindInterval, String monthDate, String remindContent) {
    	StringBuffer sb = new StringBuffer();
        if (remindInterval == 0) {
            sb.append("今天是");
        } else {
            sb.append(remindInterval).append("天后是");
        }
        sb.append(monthDate).append("，是您设置的纪念日，提醒内容：").append(remindContent).append("。") ;
        return sb.toString();
    }
}
