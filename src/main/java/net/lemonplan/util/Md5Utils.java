package net.lemonplan.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author TieJianKuDan
 * @Date 2021/11/8 11:22
 * @Description md5 工具类
 * @Since version-1.0
 */
public class Md5Utils {
    /**
     * 使用 md5 加密
     * @param target
     * @return
     */
    public static String encode(String target) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(target.getBytes());
            StringBuilder builder = new StringBuilder();
            for (byte b : digest) {
                // 将 b 变成正数，与 mysql 的优化有关
                int temp = b & 255;
                if (temp < 16) {
                    builder.append("0");
                }
                builder.append(Integer.toHexString(temp));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
