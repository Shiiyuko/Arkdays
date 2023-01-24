/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONArray
 *  com.alibaba.fastjson.JSONObject
 *  com.hypergryph.arknights.ArknightsApplication
 *  com.hypergryph.arknights.core.dao.userDao
 *  com.hypergryph.arknights.core.pojo.Account
 *  com.hypergryph.arknights.game.account
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Date
 *  java.util.List
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestHeader
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.hypergryph.arknights.game;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.pojo.Account;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/account"})
public class account {
    private static final Logger LOGGER = LogManager.getLogger();

    @PostMapping(value={"/login"}, produces={"application/json; charset=utf-8"})
    public JSONObject Login(@RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /account/login");
        String secret = JsonBody.getString("token");
        String assetsVersion = JsonBody.getString("assetsVersion");
        String clientVersion = JsonBody.getString("clientVersion");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            JSONObject result = new JSONObject(true);
            result.put("result", 1);
            result.put("error", "您已被此服务器封禁");
            return result;
        }
        if (!clientVersion.equals(ArknightsApplication.serverConfig.getJSONObject("version").getJSONObject("android").getString("clientVersion"))) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "客户端版本需要更新");
            return result;
        }
        if (!assetsVersion.equals(ArknightsApplication.serverConfig.getJSONObject("version").getJSONObject("android").getString("resVersion"))) {
            JSONObject result = new JSONObject(true);
            result.put("result", 4);
            result.put("error", "资源需要更新");
            return result;
        }
        if (((Account)Accounts.get(0)).getUser().equals("{}")) {
            ArknightsApplication.DefaultSyncData.getJSONObject("status").put("registerTs", (new Date().getTime() / 1000L));
            ArknightsApplication.DefaultSyncData.getJSONObject("status").put("lastApAddTime", (new Date().getTime() / 1000L));
            userDao.setUserData((Long)uid, (JSONObject)ArknightsApplication.DefaultSyncData);
        }
        JSONObject result = new JSONObject(true);
        result.put("result", 0);
        result.put("uid", uid);
        result.put("secret", secret);
        result.put("serviceLicenseVersion", 0);
        return result;
    }

    @PostMapping(value={"/syncData"}, produces={"application/json;charset=UTF-8"})
    public JSONObject SyncData(@RequestHeader(value="secret") String secret, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /account/syncData");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        Long ts = ArknightsApplication.getTimestamp();
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        UserSyncData.getJSONObject("status").put("lastOnlineTs", (new Date().getTime() / 1000L));
        UserSyncData.getJSONObject("status").put("lastRefreshTs", ts);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        result.put("result", 0);
        result.put("user", UserSyncData);
        result.put("ts", ts);
        return result;
    }

    @PostMapping(value={"/syncStatus"}, produces={"application/json;charset=UTF-8"})
    public JSONObject SyncStatus(@RequestHeader(value="secret") String secret, HttpServletResponse response, HttpServletRequest request) {
        JSONArray FriendRequest;
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
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
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        UserSyncData.getJSONObject("status").put("lastOnlineTs", (new Date().getTime() / 1000L));
        UserSyncData.getJSONObject("status").put("lastRefreshTs", ArknightsApplication.getTimestamp());
        UserSyncData.getJSONObject("pushFlags").put("hasGifts", 0);
        UserSyncData.getJSONObject("pushFlags").put("hasFriendRequest", 0);
        JSONArray listMailBox = JSONArray.parseArray((String)((Account)Accounts.get(0)).getMails());
        for (int i = 0; i < listMailBox.size(); ++i) {
            if (listMailBox.getJSONObject(i).getIntValue("state") != 0) continue;
            if (new Date().getTime() / 1000L <= listMailBox.getJSONObject(i).getLongValue("expireAt")) {
                UserSyncData.getJSONObject("pushFlags").put("hasGifts", 1);
                break;
            }
            listMailBox.getJSONObject(i).put("remove", 1);
        }
        if ((FriendRequest = JSONObject.parseObject((String)((Account)Accounts.get(0)).getFriend()).getJSONArray("request")).size() != 0) {
            UserSyncData.getJSONObject("pushFlags").put("hasFriendRequest", 1);
        }
        userDao.setMailsData((Long)uid, (JSONArray)listMailBox);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        modified.put("status", UserSyncData.getJSONObject("status"));
        modified.put("gacha", UserSyncData.getJSONObject("gacha"));
        modified.put("inventory", UserSyncData.getJSONObject("inventory"));
        modified.put("pushFlags", UserSyncData.getJSONObject("pushFlags"));
        modified.put("consumable", UserSyncData.getJSONObject("consumable"));
        modified.put("rlv2", UserSyncData.getJSONObject("rlv2"));
        playerDataDelta.put("modified", modified);
        playerDataDelta.put("deleted", new JSONObject(true));
        result.put("playerDataDelta", playerDataDelta);
        JSONObject result_announcement = new JSONObject(true);
        result_announcement.put("4", ArknightsApplication.serverConfig.getJSONObject("announce").getJSONObject("status"));
        result.put("result", result_announcement);
        result.put("ts", ArknightsApplication.getTimestamp());
        return result;
    }
}

