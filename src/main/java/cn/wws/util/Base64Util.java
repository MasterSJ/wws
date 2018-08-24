package cn.wws.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Base64Util {
    private final static Base64.Decoder decoder = Base64.getDecoder();
    private final static Base64.Encoder encoder = Base64.getEncoder();
    
    //编码
    public static String encode(String text) {
        byte[] textByte = null;
        try {
            textByte = text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encoder.encodeToString(textByte);
    }

    //解码
    public static String decode(String encodedText){
        try {
            return new String(decoder.decode(encodedText), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
