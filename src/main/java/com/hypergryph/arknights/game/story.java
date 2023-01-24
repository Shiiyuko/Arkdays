/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONObject
 *  com.hypergryph.arknights.ArknightsApplication
 *  com.hypergryph.arknights.core.dao.userDao
 *  com.hypergryph.arknights.core.pojo.Account
 *  com.hypergryph.arknights.game.story
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

import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.pojo.Account;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/story"})
public class story {
    @PostMapping(value={"/finishStory"}, produces={"application/json;charset=UTF-8"})
    public JSONObject FinishStory(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /story/finishStory");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        String storyId = JsonBody.getString("storyId");
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
        UserSyncData.getJSONObject("status").getJSONObject("flags").put(storyId, 1);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        modified.put("status", UserSyncData.getJSONObject("status"));
        playerDataDelta.put("modified", modified);
        playerDataDelta.put("deleted", new JSONObject(true));
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }
}

