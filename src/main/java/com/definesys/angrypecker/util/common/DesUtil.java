package com.definesys.angrypecker.util.common;

import com.definesys.angrypecker.properties.DragonConstants;
import com.definesys.mpaas.common.exception.MpaasRuntimeException;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DesUtil {
    private final static String DES = "DES";
    public static void main(String[] args) throws Exception{
//        String data = "2018-11-22 18:50:55";
//        String key = DragonUserController.secrets;
//        System.out.println("加密后："+enctypt(data, key));
//
//        System.out.println("解密后："+decrypt(enctypt(data, key), key));
//        System.out.println(System.currentTimeMillis() - new SimpleDateFormat(DragonConstants.TASK_TIME_PARSETIME).parse("2018-11-22 18:35:55").getTime());
//        Date expirationDate = new Date(System.currentTimeMillis() +  7200000);
//        System.out.println(new SimpleDateFormat(DragonConstants.TASK_TIME_PARSETIME).format(expirationDate));
        Date expirationDate = new Date(System.currentTimeMillis());
        String data = new SimpleDateFormat(DragonConstants.TASK_TIME_PARSETIME).format(expirationDate);
        String compact = DesUtil.enctypt(data, "define_forget");
        System.out.println(compact);
//compact = "igRRTfBEaHM PBhm/R9EJo8raAKnYlc3";
        compact = DesUtil.decrypt(compact,"define_forget");
        Date parse = new SimpleDateFormat(DragonConstants.TASK_TIME_PARSETIME).parse(compact);
        System.out.println(parse);
        System.out.println(compact != null && System.currentTimeMillis() - parse.getTime() > 0);
    }

    public static String enctypt(String data, String key) {
        byte[] bt = encrypt(data.getBytes(), key.getBytes());
        String strs = new BASE64Encoder().encode(bt);
        return strs;
    }

    public static String decrypt(String data, String key) {
        if (data == null) {
            return null;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] buf = null;
        try {
            buf = decoder.decodeBuffer(data);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println(1);
            e.printStackTrace();
        }
        byte[] bt = decrypt(buf, key.getBytes());
        return new String(bt);
    }

    /*
     * 加密
     */
    private static byte[] encrypt(byte[] data, byte[] key) {
        // 生成一个可信任随机的数源
        try {
            SecureRandom sr = new SecureRandom();

            // 从原始密码数据创建DESkeySpec
            DESKeySpec dks = new DESKeySpec(key);

            // 创建一个密钥工厂，然后用它把DESKeySpec转换为SecrekKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            SecretKey secretKey = keyFactory.generateSecret(dks);

            // Cipher对象实机完成加密操作
            Cipher cipher = Cipher.getInstance(DES);

            // 用密钥初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);

            return cipher.doFinal(data);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /*
     * 解密
     */
    private static byte[] decrypt(byte[] data, byte[] key) {
        try {
            // 生成一个可信任随机的数源
            SecureRandom sr = new SecureRandom();

            // 从原始密码数据创建DESkeySpec
            DESKeySpec dks = new DESKeySpec(key);

            // 创建一个密钥工厂，然后用它把DESKeySpec转换为SecrekKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            SecretKey secretKey = keyFactory.generateSecret(dks);

            // Cipher对象实机完成加密操作
            Cipher cipher = Cipher.getInstance(DES);

            // 用密钥初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);

            return cipher.doFinal(data);
        } catch (Exception e) {
           throw new MpaasRuntimeException("密钥不对");
        }
    }
}
