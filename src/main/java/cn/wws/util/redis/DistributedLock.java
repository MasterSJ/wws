package cn.wws.util.redis;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

public class DistributedLock {
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedLock.class);
    
    /**
     * 加锁
     * @param locaName  锁的key
     * @param acquireTimeout  获取请求超时时间
     * @param timeout   获得锁后的超时时间
     * @return 锁标识
     */
    public String lockWithTimeout(String locaName,
            long acquireTimeout, long timeout) {
        String ret = null;
        Jedis conn = JedisUtil.getResource();  
        conn.select(3);
        // 随机生成一个value
        String identifier = UUID.randomUUID().toString();
        // 锁名，即key值
        String lockKey = "lock:" + locaName;
        // 超时时间，上锁后超过此时间则自动释放锁
        int lockExpire = (int)(timeout / 1000);
        // 获取锁的超时时间，超过这个时间则放弃获取锁
        long end = System.currentTimeMillis() + acquireTimeout;
        while (System.currentTimeMillis() < end) {
            if (conn.setnx(lockKey, identifier) == 1) {
                LOGGER.info(Thread.currentThread().getName() + "获得了锁"+identifier);
                conn.expire(lockKey, lockExpire);
                // 返回value值，用于释放锁时间确认
                ret = identifier;
                return ret;
            }
            // 返回-1代表key没有设置超时时间，为key设置一个超时时间
            if (conn.ttl(lockKey) == -1) {
                conn.expire(lockKey, lockExpire);
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        return ret;
    }
    
    /**
     * 释放锁
     * @param lockName 锁的key
     * @param identifier    释放锁的标识
     * @return
     */
    public boolean releaseLock(String lockName, String identifier) {
        Jedis conn = null;
        String lockKey = "lock:" + lockName;
        boolean retFlag = false;
        try {
            conn = JedisUtil.getResource();
            conn.select(3);
            while (true) {
                // 监视lock，准备开始事务
                conn.watch(lockKey);
                // 通过前面返回的value值判断是不是该锁，若是该锁，则删除，释放锁
                if (identifier.equals(conn.get(lockKey))) {
                    Transaction transaction = conn.multi();
                    transaction.del(lockKey);
                    List<Object> results = transaction.exec();
                    if (results == null) {
                        LOGGER.info(Thread.currentThread().getName() + "释放失败"+identifier);
                        continue;
                    } else {
                        LOGGER.info(Thread.currentThread().getName() + "释放了锁"+identifier);
                    }
                    retFlag = true;
                } else {
                    LOGGER.info(Thread.currentThread().getName() + "锁不存在，无法释放"+identifier);
                }
                conn.unwatch();
                break;
            }
        } catch (JedisException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return retFlag;
    }
}
