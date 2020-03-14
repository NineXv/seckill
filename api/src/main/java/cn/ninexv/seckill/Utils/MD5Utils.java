package cn.ninexv.seckill.Utils;

import org.springframework.util.DigestUtils;


public class MD5Utils {
    // md5盐值字符串，用于混淆MD5
    private final static String slat = "hhh2333666ninexv**##1100qwerdf";

    public static String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());//spring提供的专门生成md5的工具类
        return md5;
    }

}
