/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONObject
 *  com.hypergryph.arknights.core.decrypt.Utils
 *  java.io.ByteArrayInputStream
 *  java.io.ByteArrayOutputStream
 *  java.io.InputStream
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 *  java.security.Key
 *  java.security.spec.AlgorithmParameterSpec
 *  java.util.Base64
 *  java.util.zip.ZipInputStream
 *  javax.crypto.Cipher
 *  javax.crypto.spec.IvParameterSpec
 *  javax.crypto.spec.SecretKeySpec
 *  org.springframework.util.DigestUtils
 */
package com.hypergryph.arknights.core.decrypt;

import com.alibaba.fastjson.JSONObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;
import java.util.zip.ZipInputStream;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.util.DigestUtils;

/*
 * Exception performing whole class analysis ignored.
 */
public class Utils {
    public static byte[] hexToByte(String hex) {
        int m = 0;
        int n = 0;
        int byteLen = hex.length() / 2;
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; ++i) {
            m = i * 2 + 1;
            n = m + 1;
            int intVal = Integer.decode((String)("0x" + hex.substring(i * 2, m) + hex.substring(m, n)));
            ret[i] = (byte)intVal;
        }
        return ret;
    }

    public static String byteToHex(byte[] bytes) {
        String strHex = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < bytes.length; ++n) {
            strHex = Integer.toHexString((int)(bytes[n] & 0xFF));
            sb.append(strHex.length() == 1 ? "0" + strHex : strHex);
        }
        return sb.toString().trim();
    }

    public static String aesEncrypt(String src, String key) {
        String encodingFormat = "UTF-8";
        String iv = "0102030405060708";
        try {
            Cipher cipher = null;
            cipher = Cipher.getInstance((String)"AES/CBC/PKCS5Padding");
            byte[] raw = key.getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(1, (Key)secretKeySpec, (AlgorithmParameterSpec)ivParameterSpec);
            byte[] encrypted = cipher.doFinal(src.getBytes(encodingFormat));
            return Base64.getEncoder().encodeToString(encrypted);
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static JSONObject BattleData_decrypt(String EncodeData, String login_time) {
        String LOG_TOKEN_KEY = "pM6Umv*^hVQuB6t&";
        byte[] BattleData = Utils.hexToByte((String)EncodeData.substring(0, EncodeData.length() - 32));
        SecretKeySpec Key2 = new SecretKeySpec(Utils.hexToByte((String)DigestUtils.md5DigestAsHex((byte[])(LOG_TOKEN_KEY + login_time).getBytes())), "AES");
        IvParameterSpec Iv = new IvParameterSpec(Utils.hexToByte((String)EncodeData.substring(EncodeData.length() - 32)));
        try {
            Cipher cipher = Cipher.getInstance((String)"AES/CBC/NoPadding");
            cipher.init(2, (Key)Key2, (AlgorithmParameterSpec)Iv);
            return JSONObject.parseObject((String)new String(cipher.doFinal(BattleData)));
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static JSONObject BattleReplay_decrypt(String battleReplay) {
        byte[] data = Base64.getDecoder().decode(battleReplay);
        byte[] b = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ZipInputStream zip = new ZipInputStream((InputStream)bis);
            while (zip.getNextEntry() != null) {
                byte[] buf = new byte[1024];
                int num = -1;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((num = zip.read(buf, 0, buf.length)) != -1) {
                    baos.write(buf, 0, num);
                }
                b = baos.toByteArray();
                baos.flush();
                baos.close();
            }
            zip.close();
            bis.close();
            return JSONObject.parseObject((String)new String(b, "UTF-8"));
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

