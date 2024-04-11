package jav.fenix.redis;

import cn.hutool.core.io.FileUtil;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;
import java.util.List;


public class RedisMain {

    public static void main(String[] args) throws InterruptedException {
        Jedis jedis = new Jedis("172.38.40.181", 6379);
        jedis.auth("GaosiDev");
        jedis.select(3);


        List<String> strings = FileUtil.readLines("/Users/feng/Downloads/json.log", StandardCharsets.UTF_8);
        for (String logJson : strings) {
            System.out.println(logJson);
            jedis.rpush("RequestStatistics", logJson);
            Thread.sleep(60 * 1000);
        }


//        jedis.set("hello", "world");
//        String hello = jedis.get("hello");
//        System.out.println(hello);

    }


}
