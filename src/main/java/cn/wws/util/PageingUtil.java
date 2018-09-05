package cn.wws.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageingUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(PageingUtil.class);
	  
	/**
     * @Description 获取分页参数.
     * @author songjun
     * @date 2017年12月6日 下午5:31:33
     * @param _page 页码
     * @param _interval 每页条数
     * @return 含offset和rows的map
     */
    public static Map<String, String> getLimitMap(int _page, int _interval) {
        Map<String, String> limitMap = new HashMap<String, String>();
        int _limitOffset = (_page - 1) * _interval;
        int _limitRows = _interval;
        limitMap.put("_limitOffset", String.valueOf(_limitOffset));
        limitMap.put("_limitRows", String.valueOf(_limitRows));
        return limitMap;
    }
    
    /**
     * @Description 获取分页参数.
     * @author songjun
     * @date 2017年12月6日 下午5:31:33
     * @param _page 页码
     * @param _interval 每页条数
     * @return 含offset和rows的map
     */
    public static Map<String, String> getLimitMap(Object _page, Object _interval) {
        Map<String, String> limitMap = new HashMap<String, String>();
        try {
            int intPage = 1;/*默认页码*/
            int intInterval = 10;/*默认每页条数*/
            if (_page != null) {
                intPage = Integer.valueOf((String)_page);
            }
            if (_interval != null) {
                intInterval = Integer.valueOf((String)_interval);
            }
            limitMap = getLimitMap(intPage, intInterval);
        } catch (Exception ex) {
        	LOGGER.error("读取页码或每页展示数异常,_page={}, _interval={}", _page, _interval);
        }
        return limitMap;
    }
}
