package cn.wws.util;

import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**  
* @ClassName: PropertiesUtil  
* @Description: wws.properties读取类  
* @author songjun 
* @date 2018年4月19日  
*    
*/
public class PropertiesUtil {
    
    private static Properties props;

    static {
        String fileName = "wws.properties";
        if (props == null) {
            readProperties(fileName);
        }
    }
    
    private static void readProperties(String fileName) {
        try {
            props = new Properties();
            InputStreamReader inputStream = new InputStreamReader(
                    PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName), "UTF-8");
            props.load(inputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static String get(String key) {
        return props.getProperty(key);
    }
    
    public static Map<?, ?> getAll() {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration<?> enu = props.propertyNames();
        while (enu.hasMoreElements()) {
            String key = (String) enu.nextElement();
            String value = props.getProperty(key);
            map.put(key, value);
        }
        return map;
    }
}
