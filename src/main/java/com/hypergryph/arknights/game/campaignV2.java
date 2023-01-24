/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONArray
 *  com.alibaba.fastjson.JSONObject
 *  com.hypergryph.arknights.ArknightsApplication
 *  com.hypergryph.arknights.core.dao.userDao
 *  com.hypergryph.arknights.core.decrypt.Utils
 *  com.hypergryph.arknights.core.pojo.Account
 *  com.hypergryph.arknights.game.campaignV2
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Date
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
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.decrypt.Utils;
import com.hypergryph.arknights.core.pojo.Account;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/campaignV2"})
public class campaignV2 {
    @PostMapping(value={"/battleStart"}, produces={"application/json;charset=UTF-8"})
    public JSONObject BattleStart(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /campaignV2/battleStart");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        String stageId = JsonBody.getString("stageId");
        int isReplay = JsonBody.getIntValue("isReplay");
        int startTs = JsonBody.getIntValue("startTs");
        int usePracticeTicket = JsonBody.getIntValue("usePracticeTicket");
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
        JSONObject stage_table = ArknightsApplication.stageTable.getJSONObject(stageId);
        if (!UserSyncData.getJSONObject("dungeon").getJSONObject("stages").containsKey(stageId)) {
            JSONObject stagesData = new JSONObject(true);
            stagesData.put("completeTimes", 0);
            stagesData.put("hasBattleReplay", 0);
            stagesData.put("noCostCnt", 1);
            stagesData.put("practiceTimes", 0);
            stagesData.put("stageId", stageId);
            stagesData.put("startTimes", 0);
            stagesData.put("state", 0);
            UserSyncData.getJSONObject("dungeon").getJSONObject("stages").put(stageId, stagesData);
        }
        if (usePracticeTicket == 1) {
            UserSyncData.getJSONObject("status").put("practiceTicket", (UserSyncData.getJSONObject("status").getIntValue("practiceTicket") - 1));
            UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId).put("practiceTimes", 1);
        }
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        JSONObject dungeon = new JSONObject(true);
        JSONObject stages = new JSONObject(true);
        stages.put(stageId, UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId));
        dungeon.put("stages", stages);
        modified.put("dungeon", dungeon);
        modified.put("status", UserSyncData.getJSONObject("status"));
        playerDataDelta.put("deleted", new JSONObject(true));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("result", 0);
        result.put("battleId", stageId);
        result.put("isApProtect", 0);
        result.put("apFailReturn", stage_table.getIntValue("apFailReturn"));
        if (UserSyncData.getJSONObject("dungeon").getJSONObject("stages").getJSONObject(stageId).getIntValue("noCostCnt") == 1) {
            result.put("isApProtect", 1);
            result.put("apFailReturn", stage_table.getIntValue("apCost"));
        }
        if (stage_table.getIntValue("apCost") == 0) {
            result.put("isApProtect", 0);
            result.put("apFailReturn", 0);
        }
        if (usePracticeTicket == 1) {
            result.put("isApProtect", 0);
            result.put("apFailReturn", 0);
        }
        return result;
    }

    @PostMapping(value={"/battleFinish"}, produces={"application/json;charset=UTF-8"})
    public JSONObject BattleFinish(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /campaignV2/battleFinish");
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
        JSONObject BattleData = Utils.BattleData_decrypt((String)JsonBody.getString("data"), (String)UserSyncData.getJSONObject("pushFlags").getString("status"));
        String stageId = BattleData.getString("battleId");
        int DropRate = ArknightsApplication.serverConfig.getJSONObject("battle").getIntValue("dropRate");
        int killCnt = BattleData.getIntValue("killCnt");
        int completeState = BattleData.getIntValue("completeState");
        if (ArknightsApplication.serverConfig.getJSONObject("battle").getBooleanValue("debug")) {
            killCnt = 400;
        }
        JSONArray firstRewards = new JSONArray();
        JSONObject diamondShard = new JSONObject(true);
        int count = 0;
        int apFailReturn = 0;
        if (killCnt < 100) {
            count = 0;
            apFailReturn = 25;
        } else if (killCnt < 200) {
            count = 80;
            apFailReturn = 17;
        } else if (killCnt < 250) {
            count = 155;
            apFailReturn = 10;
        } else if (killCnt < 300) {
            count = 200;
            apFailReturn = 7;
        } else if (killCnt < 325) {
            count = 235;
            apFailReturn = 5;
        } else if (killCnt < 350) {
            count = 275;
            apFailReturn = 3;
        } else if (killCnt < 375) {
            count = 300;
            apFailReturn = 2;
        } else if (killCnt < 400) {
            count = 330;
            apFailReturn = 1;
        } else if (killCnt == 400) {
            count = 365;
            apFailReturn = 0;
        }
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
        UserSyncData.getJSONObject("status").put("ap", (UserSyncData.getJSONObject("status").getIntValue("ap") - ArknightsApplication.stageTable.getJSONObject(stageId).getIntValue("apCost")));
        if (apFailReturn != 0) {
            UserSyncData.getJSONObject("status").put("ap", (UserSyncData.getJSONObject("status").getIntValue("ap") + apFailReturn));
            UserSyncData.getJSONObject("status").put("lastApAddTime", nowTime);
        }
        diamondShard.put("count", (count * DropRate));
        diamondShard.put("id", "4003");
        diamondShard.put("type", "DIAMOND_SHD");
        firstRewards.add(diamondShard);
        UserSyncData.getJSONObject("status").put("diamondShard", (UserSyncData.getJSONObject("status").getIntValue("diamondShard") + count * DropRate));
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        modified.put("status", UserSyncData.getJSONObject("status"));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("rewards", firstRewards);
        result.put("apFailReturn", apFailReturn);
        result.put("result", 0);
        return result;
    }
}

