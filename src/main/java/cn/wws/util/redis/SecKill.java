package cn.wws.util.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**  
* @ClassName: SecKill  
* @Description: 秒杀（模拟对资源进行抢占型操作）
* @author songjun 
* @date 2018年4月4日  
*    
*/
public class SecKill{
    private static Logger LOGGER = LoggerFactory.getLogger(SecKill.class);
    
    private String method = "com.xx.someMethod";
    
    /**    
    * @Description: 秒杀操作. 
    * @author songjun  
    * @date 2018年4月4日   
    */ 
    public void doSecKill(){
        DistributedLock lock = new DistributedLock();
        //锁最长等待时间5秒，过期时间2秒
        String indentifier = lock.lockWithTimeout(method, 5000, 2000);
        if (!StringUtils.isEmpty(indentifier)) {
            try {
                //如果获得了锁，随机持有1秒或3秒（使持有时间随机大于或小于过期时间）
                if (Math.random() > 0.5) {
                    Thread.sleep(3000);
                } else {
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.releaseLock(method, indentifier);
        } else {
            LOGGER.info(Thread.currentThread().getName() + "放弃了锁");
        }
        
    }
}
