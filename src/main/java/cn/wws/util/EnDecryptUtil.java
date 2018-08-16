package cn.wws.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.Key;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * 加密工具
 */
public class EnDecryptUtil {


    private final static Logger LOGGER = LoggerFactory.getLogger(EnDecryptUtil.class);

    private static char[] INT2CHAR = new char[]{'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static Map<Character, Integer> CHAR2INT = new HashMap<Character, Integer>();
    private static String keySpec = "bWmGSxm90dJ7SirRfA4VE9GkLIblnvBd";

    static {
        INT2CHAR.toString();
        for (int i = 0; i < INT2CHAR.length; i++) {
            CHAR2INT.put(INT2CHAR[i], i);
        }
    }

    public static String encrypt(String str) throws Exception {
        DESKeySpec desKeySpec = new DESKeySpec(keySpec.getBytes());
        Key key = SecretKeyFactory.getInstance("DES").generateSecret(desKeySpec);

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return byte2string(cipher.doFinal(str.getBytes()));
    }

    public static String decrypt(String str) throws Exception {
        DESKeySpec desKeySpec = new DESKeySpec(keySpec.getBytes());
        Key key = SecretKeyFactory.getInstance("DES").generateSecret(desKeySpec);

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        return new String(cipher.doFinal(string2byte(str)));
    }

    private static String byte2string(byte[] buff) {
        char[] str = new char[buff.length * 2];

        for (int i = 0; i < buff.length; i++) {
            str[i * 2] = INT2CHAR[(buff[i] >>> 4) & 0xF];
            str[i * 2 + 1] = INT2CHAR[buff[i] & 0xF];
        }

        return new String(str);
    }

    private static byte[] string2byte(String string) {
        byte[] buff = new byte[string.length() / 2];
        char[] str = string.toCharArray();

        for (int i = 0; i < buff.length; i++) {
            buff[i] = (byte) ((CHAR2INT.get(str[i * 2]).intValue() << 4)
                    | (CHAR2INT.get(str[i * 2 + 1]).intValue()));
        }

        return buff;
    }

    /**
     * 摘要，默认小写
     *
     * @param source
     * @return
     */
    public static String digest(String source) {
        return digest(source, false);
    }

    /**
     * 摘要
     *
     * @param source  原文
     * @param isUpper 是否大写
     * @return
     */
    public static String digest(String source, boolean isUpper) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("md5");
        } catch (Exception e) {
            LOGGER.error("digest error, " + e.getMessage());
            return "";
        }
        md5.update(source.getBytes());
        return isUpper ? byte2string(md5.digest()) : byte2string(md5.digest()).toLowerCase();
    }
    
    /**
     * 通过key和字符串组合进行md5
     *
     * @param source
     * @return
     */
    public static String digestWithKey(String source) {
        return digest(EnDecryptUtil.keySpec+source, true);
    }

}
