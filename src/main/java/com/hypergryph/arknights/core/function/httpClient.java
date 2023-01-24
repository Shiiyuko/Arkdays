/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONObject
 *  com.hypergryph.arknights.core.decrypt.Utils
 *  com.hypergryph.arknights.core.function.httpClient
 *  java.io.BufferedReader
 *  java.io.IOException
 *  java.io.InputStreamReader
 *  java.io.PrintWriter
 *  java.io.Reader
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
 *  java.net.URL
 *  java.net.URLConnection
 *  java.net.URLEncoder
 */
package com.hypergryph.arknights.core.function;

import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.core.decrypt.Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class httpClient {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static JSONObject verifySmsCode(String phone, String smsCode) {
        String secKey = "FFFFFFFFFFFFFFFF";
        String encSecKey = "257348aecb5e556c066de214e531faadd1c55d814f9be95fd06d6bff9f4c7a41f831f6394d5a3fd2e3881736d94a02ca919d952872e7d0a50ebfa1769a7a62d512f5f1ca21aec60bc3819a9c3ffca5eca9a0dba6d6f7249b06f5965ecfff3695b54e1c28f3f624750ed39e7de08fc8493242e26dbc4484a01c76f739e135637c";
        String encText = Utils.aesEncrypt((String)Utils.aesEncrypt((String)("{\"countrycode\":\"86\",\"phone\":\"" + phone + "\",\"rememberLogin\":\"true\",\"captcha\":\"" + smsCode + "\",\"checkToken\":\"\",\"csrf_token\":\"\"}"), (String)"0CoJUm6Qyw8W8jud"), (String)secKey);
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            String line;
            URL realUrl = new URL("https://music.163.com/weapi/login/cellphone?csrf_token=");
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("Referer", "https://music.163.com/");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            out = new PrintWriter(connection.getOutputStream());
            out.print("params=" + URLEncoder.encode((String)encText, (String)"UTF-8") + "&encSecKey=" + encSecKey);
            out.flush();
            in = new BufferedReader((Reader)new InputStreamReader(connection.getInputStream(), "UTF-8"));
            while ((line = in.readLine()) != null) {
                result = result + line;
            }
        }
        catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + (e));
            e.printStackTrace();
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return JSONObject.parseObject((String)result);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static JSONObject sentSmsCode(String phone) {
        String secKey = "FFFFFFFFFFFFFFFF";
        String encSecKey = "257348aecb5e556c066de214e531faadd1c55d814f9be95fd06d6bff9f4c7a41f831f6394d5a3fd2e3881736d94a02ca919d952872e7d0a50ebfa1769a7a62d512f5f1ca21aec60bc3819a9c3ffca5eca9a0dba6d6f7249b06f5965ecfff3695b54e1c28f3f624750ed39e7de08fc8493242e26dbc4484a01c76f739e135637c";
        String encText = Utils.aesEncrypt((String)Utils.aesEncrypt((String)("{\"cellphone\":\"" + phone + "\",\"ctcode\":\"86\",\"csrf_token\":\"\"}"), (String)"0CoJUm6Qyw8W8jud"), (String)secKey);
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            String line;
            URL realUrl = new URL("https://music.163.com/weapi/sms/captcha/sent?csrf_token=");
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("Referer", "https://music.163.com/");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            out = new PrintWriter(connection.getOutputStream());
            out.print("params=" + URLEncoder.encode((String)encText, (String)"UTF-8") + "&encSecKey=" + encSecKey);
            out.flush();
            in = new BufferedReader((Reader)new InputStreamReader(connection.getInputStream(), "UTF-8"));
            while ((line = in.readLine()) != null) {
                result = result + line;
            }
        }
        catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + (e));
            e.printStackTrace();
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return JSONObject.parseObject((String)result);
    }
}

