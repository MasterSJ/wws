package cn.wws.service.msg;

import java.io.IOException;

import com.alibaba.fastjson.JSONException;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;

public class TencentSms {
    final int  APP_ID = 1400121017;
    final String APP_KEY = "d9b65ee38c5de866c3481c2a1e1efb24";
    final int templateId = 7839;//短信模版id
    final String smsSign = "问学网";//短信签名内容（不是id）
    
    public void sendSms(String mobile, String content) {
        try {
            SmsSingleSender ssender = new SmsSingleSender(APP_ID, APP_KEY);
            SmsSingleSenderResult result = ssender.send(0, "86", mobile,
                    content, "", "");
            System.out.print(result);
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();
        }
    }

    
}
