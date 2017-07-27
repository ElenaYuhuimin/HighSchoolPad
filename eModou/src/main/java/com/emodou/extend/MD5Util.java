package com.emodou.extend;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by woody on 2015/7/24.
 */
public class MD5Util {


        /**
         * @param str
         * @return
         * @Date: 2013-9-6
         * @Author: woody
         * @Description:  32λСдMD5
         */
        public static String parseStrToMd5L32(String str){
            String reStr = null;
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                byte[] bytes = md5.digest(str.getBytes());
                StringBuffer stringBuffer = new StringBuffer();
                for (byte b : bytes){
                    int bt = b&0xff;
                    if (bt < 16){
                        stringBuffer.append(0);
                    }
                    stringBuffer.append(Integer.toHexString(bt));
                }
                reStr = stringBuffer.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return reStr;
        }


    /**
     * login md5 method
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }



}
