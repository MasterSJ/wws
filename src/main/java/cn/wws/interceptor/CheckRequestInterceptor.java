package cn.wws.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.wws.annotation.CheckLogin;

@Aspect
@Component
public class CheckRequestInterceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(CheckRequestInterceptor.class);
	
	/**
     * <B>方法名称：processHmallHttpRespError</B><BR>
     * <B>概要说明：处理http调用中台时，response非正常的日志记录</B><BR>
     * 
     */
    @Before(value = "execution(* cn.wws.controller..*.*(..))  and @annotation(checkLogin)")
    public void checkLogin(JoinPoint joinPoint, CheckLogin checkLogin) throws Throwable {
        /************          暂时没用              ****************/
    	/*HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();
        HttpSession session = request.getSession(false);
        if (session == null) {
        	LOGGER.debug("用户未登录");
        }*/
    }
}
