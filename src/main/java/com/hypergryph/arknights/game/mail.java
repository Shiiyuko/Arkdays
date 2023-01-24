/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONArray
 *  com.alibaba.fastjson.JSONObject
 *  com.hypergryph.arknights.Admin
 *  com.hypergryph.arknights.ArknightsApplication
 *  com.hypergryph.arknights.core.dao.mailDao
 *  com.hypergryph.arknights.core.dao.userDao
 *  com.hypergryph.arknights.core.pojo.Account
 *  com.hypergryph.arknights.core.pojo.Mail
 *  com.hypergryph.arknights.game.mail
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestHeader
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.hypergryph.arknights.game;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.Admin;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.dao.mailDao;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.pojo.Account;
import com.hypergryph.arknights.core.pojo.Mail;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/mail"})
public class mail {
    @PostMapping(value={"/getMetaInfoList"}, produces={"application/json;charset=UTF-8"})
    public JSONObject getMetaInfoList(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /mail/getMetaInfoList");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        int from = JsonBody.getIntValue("from");
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
        JSONArray resultMail = new JSONArray();
        JSONArray listMailBox = JSONArray.parseArray((String)((Account)Accounts.get(0)).getMails());
        for (int i = 0; i < listMailBox.size(); ++i) {
            if (mailDao.queryMailById((int)listMailBox.getJSONObject(i).getIntValue("mailId")).size() != 1 || from > listMailBox.getJSONObject(i).getIntValue("expireAt") || listMailBox.getJSONObject(i).getIntValue("remove") != 0) continue;
            resultMail.add(listMailBox.getJSONObject(i));
        }
        JSONObject result = new JSONObject(true);
        result.put("result", resultMail);
        return result;
    }

    @PostMapping(value={"/listMailBox"}, produces={"application/json;charset=UTF-8"})
    public JSONObject listMailBox(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /mail/listMailBox");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        JSONArray sysMailIdList = JsonBody.getJSONArray("sysMailIdList");
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
        JSONArray mailList = new JSONArray();
        JSONArray listMail = JSONArray.parseArray((String)((Account)Accounts.get(0)).getMails());
        for (int i = 0; i < sysMailIdList.size(); ++i) {
            List mails = mailDao.queryMailById((int)sysMailIdList.getIntValue(i));
            if (mails.size() != 1) continue;
            JSONObject MailBox = (JSONObject)JSONObject.toJSON(mails.get(0));
            MailBox.put("items", JSONArray.parseArray((String)MailBox.getString("items")));
            for (int n = 0; n < listMail.size(); ++n) {
                if (listMail.getJSONObject(n).getIntValue("mailId") != sysMailIdList.getIntValue(i)) continue;
                JSONObject Mail2 = listMail.getJSONObject(n);
                MailBox.put("mailId", Mail2.getIntValue("mailId"));
                MailBox.put("createAt", Mail2.getIntValue("createAt"));
                MailBox.put("expireAt", Mail2.getIntValue("expireAt"));
                MailBox.put("state", Mail2.getIntValue("state"));
                MailBox.put("type", Mail2.getIntValue("type"));
                MailBox.put("hasItem", Mail2.getIntValue("hasItem"));
            }
            mailList.add(MailBox);
        }
        JSONObject result = new JSONObject(true);
        result.put("mailList", mailList);
        return result;
    }

    @PostMapping(value={"/removeAllReceivedMail"}, produces={"application/json;charset=UTF-8"})
    public JSONObject removeAllReceivedMail(@RequestHeader(value="secret") String secret, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /mail/removeAllReceivedMail");
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
        JSONArray listMail = JSONArray.parseArray((String)((Account)Accounts.get(0)).getMails());
        for (int i = 0; i < listMail.size(); ++i) {
            int state = listMail.getJSONObject(i).getIntValue("state");
            if (state != 1) continue;
            listMail.getJSONObject(i).put("remove", 1);
        }
        userDao.setMailsData((Long)uid, (JSONArray)listMail);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("modified", new JSONObject(true));
        playerDataDelta.put("deleted", new JSONObject(true));
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/receiveMail"}, produces={"application/json;charset=UTF-8"})
    public JSONObject receiveMail(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        int i;
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /mail/receiveMail");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        int mailId = JsonBody.getIntValue("mailId");
        List mailList = mailDao.queryMailById((int)mailId);
        if (mailList.size() != 1) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
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
        JSONArray listMail = JSONArray.parseArray((String)((Account)Accounts.get(0)).getMails());
        JSONArray items = new JSONArray();
        for (int n = 0; n < listMail.size(); ++n) {
            if (listMail.getJSONObject(n).getIntValue("mailId") != mailId) continue;
            JSONArray mailItems = JSONArray.parseArray((String)((Mail)mailList.get(0)).getItems());
            for (int i2 = 0; i2 < mailItems.size(); ++i2) {
                String reward_id = mailItems.getJSONObject(i2).getString("id");
                String reward_type = mailItems.getJSONObject(i2).getString("type");
                int reward_count = mailItems.getJSONObject(i2).getIntValue("count");
                Admin.GM_GiveItem((JSONObject)UserSyncData, (String)reward_id, (String)reward_type, (int)reward_count, (JSONArray)items);
            }
        }
        for (i = 0; i < listMail.size(); ++i) {
            if (listMail.getJSONObject(i).getIntValue("mailId") != mailId) continue;
            listMail.getJSONObject(i).put("state", 1);
            UserSyncData.getJSONObject("pushFlags").put("hasGifts", 0);
            break;
        }
        for (i = 0; i < listMail.size(); ++i) {
            if (listMail.getJSONObject(i).getIntValue("state") != 0) continue;
            UserSyncData.getJSONObject("pushFlags").put("hasGifts", 1);
            break;
        }
        userDao.setMailsData((Long)uid, (JSONArray)listMail);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        modified.put("status", UserSyncData.getJSONObject("status"));
        modified.put("inventory", UserSyncData.getJSONObject("inventory"));
        modified.put("troop", UserSyncData.getJSONObject("troop"));
        modified.put("skin", UserSyncData.getJSONObject("skin"));
        modified.put("pushFlags", UserSyncData.getJSONObject("pushFlags"));
        playerDataDelta.put("deleted", new JSONObject(true));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("items", items);
        result.put("result", 0);
        return result;
    }

    @PostMapping(value={"/receiveAllMail"}, produces={"application/json;charset=UTF-8"})
    public JSONObject receiveAllMail(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        int i;
        int mailId;
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /mail/receiveAllMail");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        JSONArray sysMailIdList = JsonBody.getJSONArray("sysMailIdList");
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
        JSONArray listMail = JSONArray.parseArray((String)((Account)Accounts.get(0)).getMails());
        JSONArray items = new JSONArray();
        for (int n = 0; n < sysMailIdList.size(); ++n) {
            mailId = sysMailIdList.getIntValue(n);
            List mailList = mailDao.queryMailById((int)mailId);
            JSONArray mailItems = mailList.size() != 1 ? new JSONArray() : JSONArray.parseArray((String)((Mail)mailList.get(0)).getItems());
            for (int i2 = 0; i2 < mailItems.size(); ++i2) {
                String reward_id = mailItems.getJSONObject(i2).getString("id");
                String reward_type = mailItems.getJSONObject(i2).getString("type");
                int reward_count = mailItems.getJSONObject(i2).getIntValue("count");
                Admin.GM_GiveItem((JSONObject)UserSyncData, (String)reward_id, (String)reward_type, (int)reward_count, (JSONArray)items);
            }
        }
        block2: for (i = 0; i < sysMailIdList.size(); ++i) {
            mailId = sysMailIdList.getIntValue(i);
            for (int n = 0; n < listMail.size(); ++n) {
                if (listMail.getJSONObject(n).getIntValue("mailId") != mailId) continue;
                listMail.getJSONObject(n).put("state", 1);
                UserSyncData.getJSONObject("pushFlags").put("hasGifts", 0);
                continue block2;
            }
        }
        for (i = 0; i < listMail.size(); ++i) {
            if (listMail.getJSONObject(i).getIntValue("state") != 0) continue;
            UserSyncData.getJSONObject("pushFlags").put("hasGifts", 1);
            break;
        }
        userDao.setMailsData((Long)uid, (JSONArray)listMail);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        modified.put("status", UserSyncData.getJSONObject("status"));
        modified.put("inventory", UserSyncData.getJSONObject("inventory"));
        modified.put("troop", UserSyncData.getJSONObject("troop"));
        modified.put("skin", UserSyncData.getJSONObject("skin"));
        modified.put("pushFlags", UserSyncData.getJSONObject("pushFlags"));
        playerDataDelta.put("deleted", new JSONObject(true));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("items", items);
        result.put("result", 0);
        return result;
    }
}

