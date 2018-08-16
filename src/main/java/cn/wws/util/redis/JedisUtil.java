package cn.wws.util.redis;
import redis.clients.jedis.Jedis;  
import redis.clients.jedis.JedisPool;  
import redis.clients.jedis.JedisPoolConfig;

public class JedisUtil {
    private static JedisPool pool; //线程池对象  
    private static String ADDR = "127.0.0.1";//redis所在服务器地址（案例中是在本机）
    private static int PORT = 6379;//端口号  
    private static String AUTH = "123456";//密码
    private static int MAX_IDLE = 10;//连接池最大空闲连接数
    private static int MAX_ACTIVE = 50;//最大激活连接数
    private static int MAX_WAIT = 100000;//等待可用连接的最大时间(毫秒)，默认值-1，表示永不超时。若超过等待时间，则抛JedisConnectionException
    private static int TIMEOUT = 10000;//链接连接池的超时时间#使用连接时，检测连接是否成功
    private static boolean TEST_ON_BORROW = true;//使用连接时，测试连接是否可用 
    private static boolean TEST_ON_RETURN = true;//返回连接时，测试连接是否可用
    static{  
        JedisPoolConfig config = new JedisPoolConfig();  
        config.setMaxIdle(MAX_IDLE);  
        config.setTestOnBorrow(TEST_ON_BORROW);  
        config.setTestOnReturn(TEST_ON_RETURN);  
        config.setMaxTotal(MAX_ACTIVE);
        pool = new JedisPool(config, ADDR, PORT, TIMEOUT, AUTH); //新建连接池，如有密码最后加参数  
    }  
    
    public static Jedis getResource() {
        try {  
            if (pool!=null) {  
                Jedis resource = pool.getResource();  
                return resource;  
            }   
            return null;  
        } catch(Exception e) {  
            e.printStackTrace();  
            return null;  
        } 
    }
    
    public static void returnResource (Jedis used) {  
        
        if(pool!=null){  
            pool.returnResource(used);  
        }  
          
    }  
    
    public static void set(int db, String key, String value) {
        Jedis resource = getResource();  
        resource.select(db);  
        resource.set(key, value);
        System.out.println("插入的值为("+key+":"+value+")");
        returnResource(resource); 
    }
    
    public static String get(int db, String key) {
        Jedis resource = getResource();  
        resource.select(db); 
        String value = resource.get(key);
        System.out.println("取到的值为："+value);
        return value;
    }
}
