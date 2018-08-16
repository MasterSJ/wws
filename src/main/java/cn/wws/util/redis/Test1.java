package cn.wws.util.redis;

public class Test1 {
    public static long baseTime = System.currentTimeMillis();
    public static void main(String[] args) {
        testDistributedLock();
    }
    
    /**    
    * @Description: 测试分布式锁 
    * @author songjun  
    * @date 2018年4月4日   
    */ 
    private static void testDistributedLock() {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            Thread thr = new Thread(){
                public void run(){
                    SecKill sk = new SecKill();
                    sk.doSecKill();
                }
            };
            thr.start();
        }
    }
}
