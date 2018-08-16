package cn.wws.service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.springframework.stereotype.Component;

/**
 * @ClassName: EmailSending  
 * @Description: 这个方法可能是要微信企业号才可以用吧
 * 普通的qq邮箱会报错，换了cn.wws.util.SendQqemail类来实现
 * @author: songjun 
 * @date: 2018年7月28日  
 *
 */
@Component
public class EmailSending {

    public EmailSending() {

    }

    /**
     * 
     * @Description 添加附件.
     * @author chenyuanxian
     * @date 2016年6月1日 下午4:30:24
     * @param fileList 文件列表
     * @param multipart 多媒体部件
     * @throws MessagingException 消息错误
     * @throws UnsupportedEncodingException 编码错误
     */
    public void addTach(String[] fileList, Multipart multipart)
            throws MessagingException, UnsupportedEncodingException {
        for (int index = 0; index < fileList.length; index++) {
            MimeBodyPart mailArchieve = new MimeBodyPart();
            FileDataSource fds = new FileDataSource(fileList[index]);
            mailArchieve.setDataHandler(new DataHandler(fds));
            mailArchieve.setFileName(MimeUtility.encodeText(fds.getName(), "GBK", "B"));
            multipart.addBodyPart(mailArchieve);
        }
    }
    
    /**
     * 
     * @Description 发送邮件.
     * @author chenyuanxian
     * @date 2016年6月1日 下午4:36:31
     * @param emailToArray  接收人列表
     * @param emailChaoToArray 抄送人列表
     * @param emailMiToArray 密送人列表
     * @param emailSubject 邮件标题
     * @param emailContent 邮件正文
     * @param emailFrom 发送人
     * @param filePathArray 文件列表
     */
    public void send(String[] emailToArray, String[] emailChaoToArray, String[] emailMiToArray,
            String emailSubject, String emailContent, String[] filePathArray) {
        try {
            //String mailSmtpHost = reportConfig.getMailSmtpHost().trim();
            String mailSmtpHost = "smtp.exmail.qq.com";
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");// 表示SMTP发送邮件，需要进行身份验证
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", mailSmtpHost);

            // 建立会话
            Session session = Session.getInstance(props);
            Message msg = new MimeMessage(session); // 建立信息
            
            String toList = null;
            String toListcs = null;
            String toListms = null;
            
            // 发送给收件人
            if (null != emailToArray && 0 != emailToArray.length) {
                toList = getMailList(emailToArray);
                InternetAddress[] iaToList = InternetAddress.parse(toList);
                msg.setRecipients(Message.RecipientType.TO, iaToList);
            }
            
            // 抄送给抄送人
            if (null != emailChaoToArray && 0 != emailChaoToArray.length) {
                toListcs = getMailList(emailChaoToArray);
                InternetAddress[] iaToListcs = InternetAddress.parse(toListcs);
                msg.setRecipients(Message.RecipientType.CC, iaToListcs);
            }
            
            // 密送给密送人
            if (null != emailMiToArray && 0 != emailMiToArray.length) {
                toListms = getMailList(emailMiToArray);
                InternetAddress[] iaToListms =  InternetAddress.parse(toListms);
                msg.setRecipients(Message.RecipientType.BCC, iaToListms);
            }
            msg.setSentDate(new Date()); // 发送日期
            msg.setSubject(emailSubject); // 主题
            msg.setText(emailContent); // 内容
            
            // 显示html格式的文本内容
            BodyPart messageBodyPart = new MimeBodyPart();
            Multipart multipart = new MimeMultipart();
            messageBodyPart.setContent(emailContent, "text/html;charset=gbk");
            multipart.addBodyPart(messageBodyPart);

            // 2.保存多个附件
            if (filePathArray != null) {
                addTach(filePathArray, multipart);
            }

            msg.setContent(multipart);
            // 邮件服务器进行验证
            //String senderUserName = reportConfig.getSenderUserName().trim();
            //String senderPassword = reportConfig.getSenderPassword().trim();
            String senderUserName = "1135790077@qq.com";
            String senderPassword = "iypmdxludhciifcd";
          //emailFrom = reportConfig.getSenderUserName().trim(); 
            msg.setFrom(new InternetAddress(senderUserName)); // 发件人
            Transport tran = session.getTransport("smtp");
            tran.connect(senderUserName, senderPassword);
            tran.sendMessage(msg, msg.getAllRecipients()); // 发送

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 
     * @Description 获取邮箱列表.
     * @author chenyuanxian
     * @date 2016年6月1日 下午4:44:43
     * @param mailArray 邮箱列表
     * @return 邮箱列表
     */
    private String getMailList(String[] mailArray) {
        StringBuffer toList = new StringBuffer();
        int length = mailArray.length;
        if (mailArray != null && length < 2) {
            toList.append(mailArray[0]);
        } else {
            for (int i = 0; i < length; i++) {
                toList.append(mailArray[i]);
                if (i != (length - 1)) {
                    toList.append(",");
                }
            }
        }
        return toList.toString();
    }

}