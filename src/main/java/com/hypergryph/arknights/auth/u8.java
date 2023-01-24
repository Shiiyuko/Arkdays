/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONObject
 *  com.hypergryph.arknights.ArknightsApplication
 *  com.hypergryph.arknights.auth.u8
 *  com.hypergryph.arknights.core.dao.userDao
 *  com.hypergryph.arknights.core.pojo.Account
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.hypergryph.arknights.auth;

import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.pojo.Account;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/u8"})
public class u8 {
    @PostMapping(value={"/user/v1/getToken"}, produces={"application/json;charset=UTF-8"})
    public JSONObject GetToken(@RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /u8/user/v1/getToken");
        String secret = JsonBody.getJSONObject("extension").getString("access_token");
        if (!ArknightsApplication.enableServer) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", ArknightsApplication.serverConfig.getJSONObject("server").getString("closeMessage"));
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
        result.put("result", 0);
        result.put("uid", uid);
        result.put("error", "");
        result.put("extension", "{\"isGuest\":false}");
        result.put("channelUid", uid);
        result.put("token", secret);
        result.put("isGuest", 0);
        return result;
    }

    @PostMapping(value={"/user/verifyAccount"}, produces={"application/json;charset=UTF-8"})
    public JSONObject VerifyAccount(@RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String secret = JsonBody.getJSONObject("extension").getString("access_token");
        if (!ArknightsApplication.enableServer) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", ArknightsApplication.serverConfig.getJSONObject("server").getString("closeMessage"));
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
        result.put("result", 0);
        result.put("uid", uid);
        result.put("error", "");
        result.put("extension", "{\"isGuest\":false}");
        result.put("channelUid", uid);
        result.put("token", secret);
        result.put("isGuest", 0);
        return result;
    }

    @RequestMapping(value={"/pay/getAllProductList"})
    public JSONObject GetAllProductList(HttpServletResponse response, HttpServletRequest request) {
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        return ArknightsApplication.AllProductList;
    }

    @PostMapping(value={"/pay/confirmOrderState"}, produces={"application/json;charset=UTF-8"})
    public JSONObject confirmOrderState(HttpServletResponse response, HttpServletRequest request) {
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        JSONObject result = new JSONObject(true);
        result.put("payState", 3);
        return result;
    }
}

