package cn.itcast.travel.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.*;
import java.util.Properties;

public class JedisUtils {
    static Properties pro;
    static JedisPoolConfig jedisPoolConfig;
    static JedisPool jedisPool;

    static {
        try {
            //加载配置文件
            pro = new Properties();
            pro.load(JedisUtils.class.getClassLoader().getResourceAsStream("jedis.properties"));
            //加载配置参数
            jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxTotal(Integer.parseInt(pro.getProperty("redis.pool.maxTotal")));
            jedisPoolConfig.setMaxIdle(Integer.parseInt(pro.getProperty("redis.pool.maxIdle")));
            jedisPoolConfig.setMinIdle(Integer.parseInt(pro.getProperty("redis.pool.minIdle")));
            jedisPoolConfig.setMaxWaitMillis(Long.parseLong( pro.getProperty("redis.pool.maxWaitMillis")));
            jedisPoolConfig.setTestOnBorrow(Boolean.parseBoolean(pro.getProperty("redis.pool.testOnBorrow")));
            jedisPoolConfig.setTestOnCreate(Boolean.parseBoolean(pro.getProperty("redis.pool.testOnCreate")));
            jedisPoolConfig.setTestOnReturn(Boolean.parseBoolean(pro.getProperty("redis.pool.testOnReturn")));
            jedisPoolConfig.setTimeBetweenEvictionRunsMillis(Long.parseLong(pro.getProperty("redis.pool.timeBetweenEvictionRunsMillis")));
            jedisPoolConfig.setTestWhileIdle(Boolean.parseBoolean(pro.getProperty("redis.pool.testWhileIdle")));
            jedisPoolConfig.setNumTestsPerEvictionRun(Integer.parseInt(pro.getProperty("redis.pool.numTestsPerEvictionRun")));
            //加载连接池对象
            jedisPool = new JedisPool(jedisPoolConfig,pro.getProperty("redis.ip"),Integer.parseInt(pro.getProperty("redis1.port")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Jedis getJedis() {
        return jedisPool.getResource();
    }
}
