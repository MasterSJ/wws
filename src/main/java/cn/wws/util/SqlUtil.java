package cn.wws.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class SqlUtil {
    private static Properties props = new Properties();
    private static boolean isPrintSql = true;
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlUtil.class);
    /**
     * 存放sql模版.
     */
    private static Map<String,String> COMMON_SQL;
    
    
    /**
     * @Description 通过sqlid和参数，得到可执行的sql语句.
     * @author songjun
     * @date 2016年8月18日 上午9:47:59
     * @param sqlId sql模板ID
     * @param paramMap 查询参数
     * @throws DocumentException 获取sql模板失败
     */
    public static String getSql(String sqlId, Map<String, ?> paramMap) {
        String sqlTemplate = null;
        try {
            sqlTemplate = getSqlTemplate(sqlId);
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }
        String sql = analyzeSql(sqlTemplate, paramMap);
        if (isPrintSql) {
        	LOGGER.debug(sql);
        }
        return sql;
    }
    
    /**
     * @Description 扫描mybatis/sql/下的所有xml文件，初始化sqlmap模版.
     *     如果已经初始化，则跳出。
     * @author songjun
     * @date 2016年9月22日 下午3:50:33
     * @throws DocumentException 初始化sqlmap模版失败
     */
    public static void initCommonSql() throws DocumentException {
        if (COMMON_SQL != null) {
            return;
        }
        COMMON_SQL = new HashMap<String,String>();
        String rootPath = SqlUtil.class.getResource("/").getFile().toString(); 
        loadAllSqlFile(rootPath + "/sql");
    }
    
    private static boolean loadAllSqlFile(String filepath) throws DocumentException {
        File file = new File(filepath);
        if (!file.isDirectory()) {
            if (file.getName().endsWith(".sqlmap")) {
                analyzeXml(file);
            }
        } else if (file.isDirectory()) {
            //文件夹
            String[] filelist = file.list();
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(filepath + "/" + filelist[i]);
                if (!readfile.isDirectory()) {
                    if (readfile.getName().endsWith(".sqlmap")) {
                        analyzeXml(readfile);
                    }
                } else if (readfile.isDirectory()) {
                    loadAllSqlFile(filepath + "/" + filelist[i]);
                }
            }
        }
        return true;
    }
    
    /**
     * @Description 解析xml文件，得到sql模版.
     * @author songjun
     * @date 2016年9月22日 下午3:52:20
     * @param xmlFile 文件名
     * @throws DocumentException 解析文件失败
     */
    private static void analyzeXml(File xmlFile) throws DocumentException {
        int ind = xmlFile.getName().indexOf(".");
        String fileName = xmlFile.getName().substring(0,ind);
        SAXReader saxReader = new SAXReader();  
        Document document = saxReader.read(xmlFile);  
        Element root = document.getRootElement();  
        @SuppressWarnings("unchecked")
        List<Element> eleList =  root.elements();
        for (Element element:eleList) {  
            // 获取sql属性的值  
            Attribute idAttr = element.attribute("id");  
            if (idAttr != null) {  
                String id = idAttr.getValue();  
                if (id != null && !id.equals("")) {  
                    if (COMMON_SQL.get(fileName + "." + id) != null) {
                    	LOGGER.error("存在重复的sqlmap:" + fileName + "." + id);
                    }
                    COMMON_SQL.put(fileName + "." + id, element.getTextTrim());
                }
            } 
        }  
    }
    
    /**
     * @Description 根据sqlid，得到模版sql语句.
     * @author songjun
     * @date 2016年8月18日 上午11:46:59
     * @param sqlId sql模板ID
     * @return sql模板
     * @throws DocumentException  获取sql模板失败
     */
    
    private static String getSqlTemplate(String sqlId) throws DocumentException {
        String ret = null;
        if (COMMON_SQL == null ) {
            initCommonSql();
        }
        ret =  COMMON_SQL.get(sqlId);
        return ret == null ? "" : ret;
    }
    
    /**
     * @Description 通过sql模版和参数，解析得到可执行sql语句(freemarker版).
     * @author songjun
     * @date 2016年8月18日 上午10:06:54
     * @param sqlTemplate sql模版
     * @param paramMap 查询参数
     * @return sql语句
     */
    private static String analyzeSql(String sqlTemplate, Map<String, ?> paramMap) {
        String ret = null;
        @SuppressWarnings("deprecation")
        Configuration cfg = new Configuration();  
        StringTemplateLoader stringLoader = new StringTemplateLoader();  
        stringLoader.putTemplate("myTemplate",sqlTemplate);  
          
        cfg.setTemplateLoader(stringLoader);
        
        try {  
            Template template = cfg.getTemplate("myTemplate","utf-8");  
              
            StringWriter writer = new StringWriter();    
            try {  
                template.process(paramMap, writer);  
                ret = writer.toString();    
            } catch (TemplateException e1) {  
                System.out.println("sqlTemplate-----" + sqlTemplate);
                System.out.println("paramMap-----" + paramMap);
                e1.printStackTrace();
            }    
        } catch (IOException e2) {  
            e2.printStackTrace();  
        }
        return ret;
    }
    
    /**
     * @Description 通过sql模版和参数，解析得到可执行sql语句(velocity版).
     * @author songjun
     * @date 2016年8月18日 上午10:06:54
     * @param sqlTemplate sql模版
     * @param paramMap 查询参数
     * @return sql语句
     */
    /*private static String analyzeSql(String sqlTemplate, Map<String,Object> paramMap) {  
        // 初始化并取得Velocity引擎  
        VelocityEngine engine = new VelocityEngine(props);  
        // 取得velocity的上下文context  
        VelocityContext context = new VelocityContext();  
        // 把数据填入上下文  
        if (paramMap != null) {
            for (Map.Entry<String, Object> entry:paramMap.entrySet()) {
                context.put(entry.getKey(), entry.getValue());
            }
        }
        StringWriter writer = new StringWriter();  
        engine.evaluate(context, writer, "", sqlTemplate);  
        return writer.toString();  
          
    } 
*/
}
