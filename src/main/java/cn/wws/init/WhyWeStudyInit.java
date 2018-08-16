package cn.wws.init;

import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import cn.wws.entity.AutoIntoDbStack;
import cn.wws.mapper.BaseMapper;


@Component
public class WhyWeStudyInit implements  ServletContextAware{
	private static final Logger LOGGER = LoggerFactory.getLogger(WhyWeStudyInit.class);
	
	@Autowired
    private BaseMapper mapper;
	
	final long timeInterval = 1000 * 60 * 1;//5分钟 

	@Override
	public void setServletContext(ServletContext servletContext) {
		Runnable runnable = new Runnable() {  
            public void run() {  
            	while (true) {  
        	    	insertVisitLog();
        	    	updateBlogVisitCount();
        			try {  
        	            Thread.sleep(timeInterval);  
        	        } catch (InterruptedException e) {  
        	            e.printStackTrace();  
        	        }
        	    }   
            }  
        };  
        Thread thread = new Thread(runnable);  
        thread.start(); 
	}
	
	/**
	 * 写访问记录日志
	 */
	private void insertVisitLog(){
		Runnable runnable = new Runnable() {  
            public void run() {  
                for (String sql = AutoIntoDbStack.getVisitLogSql(); sql != null; sql = AutoIntoDbStack.getVisitLogSql()) {  
                	mapper.executeInsert(sql);
                	LOGGER.info("插入了一次访问日志:{}", sql);
                }  
            }  
        };  
        Thread thread = new Thread(runnable);  
        thread.start(); 
	}
	
	private void updateBlogVisitCount() {
		Runnable runnable = new Runnable() {  
            public void run() {  
            	List<String> sqls = AutoIntoDbStack.getBlogVisitSqls();
            	if (sqls != null) {
	                for (String sql : sqls) {  
	                	mapper.executeUpdate(sql);
	                	LOGGER.info("修改了一次博客访问数:{}", sql);
	                }  
            	}
            }  
        };  
        Thread thread = new Thread(runnable);  
        thread.start(); 
	}

}
