/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONObject
 *  com.hypergryph.arknights.ArknightsApplication
 *  com.hypergryph.arknights.core.dao.userDao
 *  com.hypergryph.arknights.core.pojo.Account
 *  com.hypergryph.arknights.game.users
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
@RequestMapping(value={"/user"})
public class users {
    private static final Logger LOGGER = LogManager.getLogger();

    @PostMapping(value={"/bindNickName"}, produces={"application/json;charset=UTF-8"})
    public JSONObject bindNickName(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /user/bindNickName");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        String nickName = JsonBody.getString("nickName");
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
        if (nickName.length() > 8) {
            JSONObject result = new JSONObject(true);
            result.put("result", 1);
            return result;
        }
        if (nickName.indexOf("/") != -1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        String nickNumber = String.format((String)"%04d", (Object[])new Object[]{userDao.queryNickName((String)nickName).size() + 1});
        UserSyncData.getJSONObject("status").put("nickNumber", nickNumber);
        UserSyncData.getJSONObject("status").put("uid", uid);
        UserSyncData.getJSONObject("status").put("nickName", nickName);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        JSONObject status = new JSONObject(true);
        status.put("nickName", nickName);
        modified.put("status", status);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/rebindNickName"}, produces={"application/json;charset=UTF-8"})
    public JSONObject reBindNickName(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /user/rebindNickName");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        String nickName = JsonBody.getString("nickName");
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
        UserSyncData.getJSONObject("status").put("nickName", nickName);
        UserSyncData.getJSONObject("inventory").put("renamingCard", (UserSyncData.getJSONObject("inventory").getIntValue("renamingCard") - 1));
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        JSONObject status = new JSONObject(true);
        JSONObject inventory = new JSONObject(true);
        inventory.put("renamingCard", UserSyncData.getJSONObject("inventory").getIntValue("renamingCard"));
        status.put("nickName", nickName);
        modified.put("status", status);
        modified.put("inventory", inventory);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/exchangeDiamondShard"}, produces={"application/json;charset=UTF-8"})
    public JSONObject exchangeDiamondShard(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /user/exchangeDiamondShard");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        int count = JsonBody.getIntValue("count");
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
        if (UserSyncData.getJSONObject("status").getIntValue("androidDiamond") < count) {
            JSONObject result = new JSONObject(true);
            result.put("result", 1);
            result.put("errMsg", "剩余源石无法兑换合成玉");
            return result;
        }
        UserSyncData.getJSONObject("status").put("androidDiamond", (UserSyncData.getJSONObject("status").getIntValue("androidDiamond") - count));
        UserSyncData.getJSONObject("status").put("iosDiamond", (UserSyncData.getJSONObject("status").getIntValue("iosDiamond") - count));
        UserSyncData.getJSONObject("status").put("diamondShard", (UserSyncData.getJSONObject("status").getIntValue("diamondShard") + count * 180));
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        JSONObject status = new JSONObject(true);
        status.put("androidDiamond", UserSyncData.getJSONObject("status").getIntValue("androidDiamond"));
        status.put("iosDiamond", UserSyncData.getJSONObject("status").getIntValue("iosDiamond"));
        status.put("diamondShard", UserSyncData.getJSONObject("status").getIntValue("diamondShard"));
        modified.put("status", status);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/changeResume"}, produces={"application/json;charset=UTF-8"})
    public JSONObject changeResume(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /user/changeResume");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        String resume = JsonBody.getString("resume");
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
        UserSyncData.getJSONObject("status").put("resume", resume);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        JSONObject status = new JSONObject(true);
        status.put("resume", UserSyncData.getJSONObject("status").getString("resume"));
        modified.put("status", status);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/changeSecretary"}, produces={"application/json;charset=UTF-8"})
    public JSONObject changeSecretary(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /user/changeSecretary");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        int charInstId = JsonBody.getIntValue("charInstId");
        String skinId = JsonBody.getString("skinId");
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
        UserSyncData.getJSONObject("status").put("secretary", UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)charInstId)).getString("charId"));
        UserSyncData.getJSONObject("status").put("secretarySkinId", skinId);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        JSONObject status = new JSONObject(true);
        status.put("secretary", UserSyncData.getJSONObject("status").getString("secretary"));
        status.put("secretarySkinId", UserSyncData.getJSONObject("status").getString("secretarySkinId"));
        modified.put("status", status);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/buyAp"}, produces={"application/json;charset=UTF-8"})
    public JSONObject buyAp(@RequestHeader(value="secret") String secret, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /user/buyAp");
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
        int nowTime = (int)(new Date().getTime() / 1000L);
        int addAp = (nowTime - UserSyncData.getJSONObject("status").getIntValue("lastApAddTime")) / 360;
        if (UserSyncData.getJSONObject("status").getIntValue("ap") < UserSyncData.getJSONObject("status").getIntValue("maxAp")) {
            if (UserSyncData.getJSONObject("status").getIntValue("ap") + addAp >= UserSyncData.getJSONObject("status").getIntValue("maxAp")) {
                UserSyncData.getJSONObject("status").put("ap", UserSyncData.getJSONObject("status").getIntValue("maxAp"));
                UserSyncData.getJSONObject("status").put("lastApAddTime", nowTime);
            } else if (addAp != 0) {
                UserSyncData.getJSONObject("status").put("ap", (UserSyncData.getJSONObject("status").getIntValue("ap") + addAp));
                UserSyncData.getJSONObject("status").put("lastApAddTime", nowTime);
            }
        }
        UserSyncData.getJSONObject("status").put("androidDiamond", (UserSyncData.getJSONObject("status").getIntValue("androidDiamond") - 1));
        UserSyncData.getJSONObject("status").put("iosDiamond", (UserSyncData.getJSONObject("status").getIntValue("iosDiamond") - 1));
        UserSyncData.getJSONObject("status").put("ap", (UserSyncData.getJSONObject("status").getIntValue("ap") + UserSyncData.getJSONObject("status").getIntValue("maxAp")));
        UserSyncData.getJSONObject("status").put("lastApAddTime", nowTime);
        UserSyncData.getJSONObject("status").put("buyApRemainTimes", UserSyncData.getJSONObject("status").getIntValue("buyApRemainTimes"));
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        JSONObject status = new JSONObject(true);
        status.put("androidDiamond", UserSyncData.getJSONObject("status").getIntValue("androidDiamond"));
        status.put("iosDiamond", UserSyncData.getJSONObject("status").getIntValue("iosDiamond"));
        status.put("ap", UserSyncData.getJSONObject("status").getIntValue("ap"));
        status.put("lastApAddTime", UserSyncData.getJSONObject("status").getIntValue("lastApAddTime"));
        status.put("buyApRemainTimes", UserSyncData.getJSONObject("status").getIntValue("buyApRemainTimes"));
        modified.put("status", status);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/changeAvatar"}, produces={"application/json;charset=UTF-8"})
    public JSONObject changeAvatar(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /user/changeAvatar");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        String id = JsonBody.getString("id");
        String type = JsonBody.getString("type");
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
        UserSyncData.getJSONObject("status").getJSONObject("avatar").put("id", id);
        UserSyncData.getJSONObject("status").getJSONObject("avatar").put("type", type);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        JSONObject status = new JSONObject(true);
        status.put("avatar", UserSyncData.getJSONObject("status").getJSONObject("avatar"));
        modified.put("status", status);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }
}

