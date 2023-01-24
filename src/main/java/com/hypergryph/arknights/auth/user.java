/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONObject
 *  com.hypergryph.arknights.ArknightsApplication
 *  com.hypergryph.arknights.auth.user
 *  com.hypergryph.arknights.core.dao.userDao
 *  com.hypergryph.arknights.core.function.httpClient
 *  com.hypergryph.arknights.core.pojo.Account
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.util.DigestUtils
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.hypergryph.arknights.auth;

import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.function.httpClient;
import com.hypergryph.arknights.core.pojo.Account;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/user"})
public class user {
    private static String Key = "IxMMveJRWsxStJgX";

    @RequestMapping(value={"/info/v1/need_cloud_auth"})
    public JSONObject need_cloud_auth() {
        JSONObject result = new JSONObject(true);
        result.put("status", 0);
        result.put("msg", "faq");
        return result;
    }

    @RequestMapping(value={"/v1/guestLogin"})
    public JSONObject GuestLogin() {
        JSONObject result = new JSONObject(true);
        result.put("result", 6);
        result.put("message", "单机版禁止游客登录");
        return result;
    }

    @RequestMapping(value={"/authenticateUserIdentity"})
    public JSONObject AuthenticateUserIdentity() {
        JSONObject result = new JSONObject(true);
        result.put("result", 0);
        result.put("message", "OK");
        result.put("isMinor", false);
        return result;
    }

    @RequestMapping(value={"/updateAgreement"})
    public JSONObject updateAgreement() {
        JSONObject result = new JSONObject(true);
        result.put("result", 0);
        result.put("message", "OK");
        result.put("isMinor", false);
        return result;
    }

    @RequestMapping(value={"/checkIdCard"})
    public JSONObject CheckIdCard() {
        JSONObject result = new JSONObject(true);
        result.put("result", 0);
        result.put("message", "OK");
        result.put("isMinor", false);
        return result;
    }

    @RequestMapping(value={"/sendSmsCode"})
    public JSONObject SendSmsCode(@RequestBody JSONObject JsonBody, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /v1/sendSmsCode");
        String account2 = JsonBody.getString("account");
        if (!ArknightsApplication.serverConfig.getJSONObject("server").getBooleanValue("captcha")) {
            JSONObject result = new JSONObject(true);
            result.put("result", 4);
            return result;
        }
        if (httpClient.sentSmsCode((String)account2).getIntValue("code") == 200) {
            JSONObject result = new JSONObject(true);
            result.put("result", 0);
            return result;
        }
        JSONObject result = new JSONObject(true);
        result.put("result", 4);
        return result;
    }

    @PostMapping(value={"/register"}, produces={"application/json;charset=UTF-8"})
    public JSONObject Register(@RequestBody JSONObject JsonBody, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /user/register");
        String account2 = JsonBody.getString("account");
        String password = JsonBody.getString("password");
        String smsCode = JsonBody.getString("smsCode");
        String secret = DigestUtils.md5DigestAsHex((byte[])(account2 + Key).getBytes());
        if (userDao.queryAccountByPhone((String)account2).size() != 0) {
            JSONObject result = new JSONObject(true);
            result.put("result", 5);
            result.put("errMsg", "该用户已存在，请确认注册信息");
            return result;
        }
        if (ArknightsApplication.serverConfig.getJSONObject("server").getBooleanValue("captcha") && httpClient.verifySmsCode((String)account2, (String)smsCode).getIntValue("code") == 503) {
            JSONObject result = new JSONObject(true);
            result.put("result", 5);
            result.put("errMsg", "验证码错误");
            return result;
        }
        if (userDao.RegisterAccount((String)account2, (String)DigestUtils.md5DigestAsHex((byte[])(password + Key).getBytes()), (String)secret) != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 5);
            result.put("errMsg", "注册失败，未知错误");
            return result;
        }
        JSONObject result = new JSONObject(true);
        result.put("result", 0);
        result.put("uid", 0);
        result.put("token", secret);
        result.put("isAuthenticate", true);
        result.put("isMinor", false);
        result.put("needAuthenticate", false);
        result.put("isLatestUserAgreement", true);
        return result;
    }

    @PostMapping(value={"/login"}, produces={"application/json;charset=UTF-8"})
    public JSONObject Login(@RequestBody JSONObject JsonBody, HttpServletRequest request) {
        String account2 = JsonBody.getString("account");
        String password = JsonBody.getString("password");
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /user/login");
        List accounts = userDao.LoginAccount((String)account2, (String)DigestUtils.md5DigestAsHex((byte[])(password + Key).getBytes()));
        if (accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 1);
            return result;
        }
        JSONObject result = new JSONObject(true);
        result.put("result", 0);
        result.put("uid", ((Account)accounts.get(0)).getUid());
        result.put("token", ((Account)accounts.get(0)).getSecret());
        result.put("isAuthenticate", true);
        result.put("isMinor", false);
        result.put("needAuthenticate", false);
        result.put("isLatestUserAgreement", true);
        return result;
    }

    @PostMapping(value={"/auth"}, produces={"application/json;charset=UTF-8"})
    public JSONObject Auth(@RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /user/auth");
        String secret = JsonBody.getString("token");
        if (secret == null && secret.length() < 0) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "invalid token");
            return result;
        }
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        JSONObject result = new JSONObject(true);
        result.put("uid", uid);
        result.put("isMinor", false);
        result.put("isAuthenticate", true);
        result.put("isGuest", false);
        result.put("needAuthenticate", false);
        result.put("isLatestUserAgreement", true);
        return result;
    }
}

