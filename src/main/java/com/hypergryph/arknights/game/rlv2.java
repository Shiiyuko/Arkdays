/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONArray
 *  com.alibaba.fastjson.JSONObject
 *  com.alibaba.fastjson.serializer.SerializerFeature
 *  com.hypergryph.arknights.Admin
 *  com.hypergryph.arknights.ArknightsApplication
 *  com.hypergryph.arknights.core.dao.userDao
 *  com.hypergryph.arknights.core.decrypt.Utils
 *  com.hypergryph.arknights.core.pojo.Account
 *  com.hypergryph.arknights.game.rlv2
 *  java.lang.Boolean
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collections
 *  java.util.Date
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map$Entry
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
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hypergryph.arknights.Admin;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.decrypt.Utils;
import com.hypergryph.arknights.core.pojo.Account;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
@RequestMapping(value={"/rlv2"})
public class rlv2 {
    private static final Logger LOGGER = LogManager.getLogger();

    @PostMapping(value={"/createGame"}, produces={"application/json;charset=UTF-8"})
    public JSONObject createGame(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /rlv2/createGame");
        String theme = JsonBody.getString("theme");
        String predefinedId = JsonBody.getString("predefinedId");
        String mode = JsonBody.getString("mode");
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
        JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");
        current.put("buff", new JSONObject());
        current.put("game", new JSONObject());
        current.put("inventory", new JSONObject());
        current.put("map", new JSONObject());
        current.put("player", new JSONObject());
        current.put("record", new JSONObject());
        current.put("troop", new JSONObject());
        current.getJSONObject("buff").put("tmpHP", 0);
        current.getJSONObject("buff").put("capsule", null);
        JSONObject support = new JSONObject();
        support.put("support", false);
        current.getJSONObject("game").put("outer", support);
        current.getJSONObject("game").put("mode", mode);
        current.getJSONObject("game").put("predefined", predefinedId);
        current.getJSONObject("game").put("theme", theme);
        current.getJSONObject("game").put("start", (new Date().getTime() / 1000L));
        current.getJSONObject("inventory").put("relic", new JSONObject());
        current.getJSONObject("inventory").put("recruit", new JSONObject());
        current.getJSONObject("inventory").put("trap", null);
        current.getJSONObject("map").put("zones", new JSONObject());
        current.getJSONObject("record").put("brief", null);
        current.getJSONObject("troop").put("chars", new JSONObject());
        current.getJSONObject("player").put("chgEnding", false);
        current.getJSONObject("player").put("trace", new JSONArray());
        current.getJSONObject("player").put("toEnding", "ro_ending_1");
        current.getJSONObject("player").put("state", "INIT");
        JSONObject cursor = new JSONObject();
        cursor.put("position", null);
        cursor.put("zone", 1);
        current.getJSONObject("player").put("property", new JSONObject());
        current.getJSONObject("player").getJSONObject("property").put("population", new JSONObject());
        current.getJSONObject("player").put("status", new JSONObject());
        current.getJSONObject("player").put("pending", new JSONArray());
        current.getJSONObject("player").getJSONObject("property").put("capacity", 6);
        current.getJSONObject("player").getJSONObject("property").put("conPerfectBattle", 0);
        current.getJSONObject("player").getJSONObject("property").put("level", 1);
        current.getJSONObject("player").getJSONObject("property").put("exp", 0);
        current.getJSONObject("player").getJSONObject("property").put("hp", 6);
        current.getJSONObject("player").getJSONObject("property").put("gold", 8);
        current.getJSONObject("player").getJSONObject("property").getJSONObject("population").put("cost", 0);
        current.getJSONObject("player").getJSONObject("property").getJSONObject("population").put("max", 6);
        current.getJSONObject("player").getJSONObject("status").put("bankPut", 0);
        current.getJSONObject("player").put("cursor", cursor);
        JSONObject buff = UserSyncData.getJSONObject("rlv2").getJSONObject("outer").getJSONObject(theme).getJSONObject("buff");
        for (Map.Entry entry : buff.getJSONObject("unlocked").entrySet()) {
            JSONArray buffDisplayInfo = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme).getJSONObject("developments").getJSONObject(entry.getKey().toString()).getJSONArray("buffDisplayInfo");
            for (int i = 0; i < buffDisplayInfo.size(); ++i) {
                String displayType = buffDisplayInfo.getJSONObject(i).getString("displayType");
                String displayForm = buffDisplayInfo.getJSONObject(i).getString("displayForm");
                int displayNum = buffDisplayInfo.getJSONObject(i).getIntValue("displayNum");
                if (!displayForm.equals("ABSOLUTE_VAL")) continue;
                if (displayType.equals("display_gold")) {
                    current.getJSONObject("player").getJSONObject("property").put("gold", (current.getJSONObject("player").getJSONObject("property").getIntValue("gold") + displayNum));
                }
                if (displayType.equals("display_hp")) {
                    current.getJSONObject("player").getJSONObject("property").put("hp", (current.getJSONObject("player").getJSONObject("property").getIntValue("hp") + displayNum));
                }
                if (displayType.equals("display_squad_capacity")) {
                    current.getJSONObject("player").getJSONObject("property").put("capacity", (current.getJSONObject("player").getJSONObject("property").getIntValue("capacity") + displayNum));
                }
                if (!displayType.equals("display_temp_hp")) continue;
                current.getJSONObject("buff").put("tmpHP", (current.getJSONObject("buff").getIntValue("tmpHP") + displayNum));
            }
        }
        JSONObject content = new JSONObject();
        JSONObject tmpObj = new JSONObject();
        JSONObject initRelic = new JSONObject();
        initRelic.put("step", JSONArray.parseArray((String)"[1,3]"));
        initRelic.put("items", JSONObject.parseObject((String)"{\"0\":{\"id\":\"rogue_1_band_1\",\"count\":1},\"1\":{\"id\":\"rogue_1_band_2\",\"count\":1},\"2\":{\"id\":\"rogue_1_band_3\",\"count\":1},\"3\":{\"id\":\"rogue_1_band_4\",\"count\":1},\"4\":{\"id\":\"rogue_1_band_5\",\"count\":1},\"5\":{\"id\":\"rogue_1_band_6\",\"count\":1},\"6\":{\"id\":\"rogue_1_band_7\",\"count\":1},\"7\":{\"id\":\"rogue_1_band_8\",\"count\":1},\"8\":{\"id\":\"rogue_1_band_9\",\"count\":1},\"9\":{\"id\":\"rogue_1_band_10\",\"count\":1}}"));
        tmpObj.put("initRelic", initRelic);
        content.put("content", tmpObj);
        content.put("index", "e_0");
        content.put("type", "GAME_INIT_RELIC");
        current.getJSONObject("player").getJSONArray("pending").add(content);
        content = new JSONObject();
        tmpObj = new JSONObject();
        JSONObject initRecruitSet = new JSONObject();
        initRecruitSet.put("step", JSONArray.parseArray((String)"[2,3]"));
        initRecruitSet.put("option", JSONArray.parseArray((String)"[\"recruit_group_1\",\"recruit_group_2\",\"recruit_group_3\",\"recruit_group_random\"]"));
        tmpObj.put("initRecruitSet", initRecruitSet);
        content.put("content", tmpObj);
        content.put("index", "e_1");
        content.put("type", "GAME_INIT_RECRUIT_SET");
        current.getJSONObject("player").getJSONArray("pending").add(content);
        content = new JSONObject();
        tmpObj = new JSONObject();
        JSONObject initRecruit = new JSONObject();
        initRecruit.put("step", JSONArray.parseArray((String)"[3,3]"));
        initRecruit.put("tickets", new JSONArray());
        tmpObj.put("initRecruit", initRecruit);
        content.put("content", tmpObj);
        content.put("index", "e_2");
        content.put("type", "GAME_INIT_RECRUIT");
        current.getJSONObject("player").getJSONArray("pending").add(content);
        if (mode.equals("MONTH_TEAM")) {
            // empty if block
        }
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        JSONObject rlv22 = new JSONObject();
        rlv22.put("current", UserSyncData.getJSONObject("rlv2").getJSONObject("current"));
        JSONObject outer = new JSONObject();
        JSONObject rogue_1 = new JSONObject();
        rogue_1.put("record", UserSyncData.getJSONObject("rlv2").getJSONObject("outer").getJSONObject(theme).getJSONObject("record"));
        outer.put(theme, rogue_1);
        rlv22.put("outer", outer);
        modified.put("rlv2", rlv22);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/giveUpGame"}, produces={"application/json;charset=UTF-8"})
    public JSONObject giveUpGame(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /rlv2/giveUpGame");
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
        JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");
        current.put("buff", null);
        current.put("game", null);
        current.put("inventory", null);
        current.put("map", null);
        current.put("player", null);
        current.put("record", null);
        current.put("troop", null);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        JSONObject rlv22 = new JSONObject();
        rlv22.put("current", current);
        modified.put("rlv2", rlv22);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/chooseInitialRelic"}, produces={"application/json;charset=UTF-8"})
    public JSONObject chooseInitialRelic(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /rlv2/chooseInitialRelic");
        String select = String.valueOf((int)(JsonBody.getIntValue("select") + 1));
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
        JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");
        String theme = current.getJSONObject("game").getString("theme");
        JSONObject selectRelics = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme).getJSONObject("relics").getJSONObject(theme + "_band_" + select);
        JSONObject roguelikeItems = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme).getJSONObject("items");
        JSONObject r_0 = new JSONObject();
        r_0.put("count", 1);
        r_0.put("id", (theme + "_band_" + select));
        r_0.put("index", "r_0");
        r_0.put("ts", (new Date().getTime() / 1000L));
        current.getJSONObject("inventory").getJSONObject("relic").put("r_0", r_0);
        JSONArray buffs = selectRelics.getJSONArray("buffs");
        JSONObject property = current.getJSONObject("player").getJSONObject("property");
        for (int i = 0; i < buffs.size(); ++i) {
            int count;
            String type;
            String valueStr;
            String key = buffs.getJSONObject(i).getString("key");
            JSONArray blackboard = buffs.getJSONObject(i).getJSONArray("blackboard");
            if (key.equals("level_life_point_add")) {
                current.getJSONObject("buff").put("tmpHP", (current.getJSONObject("buff").getIntValue("tmpHP") + blackboard.getJSONObject(0).getIntValue("value")));
            }
            if (key.equals("immediate_reward")) {
                valueStr = blackboard.getJSONObject(0).getString("valueStr");
                type = roguelikeItems.getJSONObject(valueStr).getString("type");
                count = blackboard.getJSONObject(1).getIntValue("value");
                if (type.equals("HP")) {
                    property.put("hp", (property.getIntValue("hp") + count));
                }
                if (type.equals("GOLD")) {
                    property.put("gold", (property.getIntValue("gold") + count));
                }
                if (type.equals("SQUAD_CAPACITY")) {
                    property.put("capacity", (property.getIntValue("capacity") + count));
                }
                if (type.equals("POPULATION")) {
                    property.getJSONObject("population").put("max", (property.getJSONObject("population").getIntValue("max") + count));
                }
            }
            if (!key.equals("item_cover_set")) continue;
            valueStr = blackboard.getJSONObject(0).getString("valueStr");
            type = roguelikeItems.getJSONObject(valueStr).getString("type");
            count = blackboard.getJSONObject(1).getIntValue("value");
            if (!type.equals("HP")) continue;
            property.put("hp", count);
        }
        current.getJSONObject("player").getJSONArray("pending").remove(0);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        JSONObject rlv22 = new JSONObject();
        rlv22.put("current", current);
        modified.put("rlv2", rlv22);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/chooseInitialRecruitSet"}, produces={"application/json;charset=UTF-8"})
    public JSONObject chooseInitialRecruitSet(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /rlv2/chooseInitialRecruitSet");
        String select = JsonBody.getString("select");
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
        JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");
        if (select.equals("recruit_group_1")) {
            current.getJSONObject("inventory").put("recruit", JSONObject.parseObject((String)"{\"t_1\":{\"index\":\"t_1\",\"id\":\"rogue_1_recruit_ticket_pioneer\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641721789,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false},\"t_2\":{\"index\":\"t_2\",\"id\":\"rogue_1_recruit_ticket_pioneer\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641721789,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false},\"t_3\":{\"index\":\"t_3\",\"id\":\"rogue_1_recruit_ticket_special\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641721789,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false}}"));
        }
        if (select.equals("recruit_group_2")) {
            current.getJSONObject("inventory").put("recruit", JSONObject.parseObject((String)"{\"t_1\":{\"index\":\"t_1\",\"id\":\"rogue_1_recruit_ticket_tank\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641722549,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false},\"t_2\":{\"index\":\"t_2\",\"id\":\"rogue_1_recruit_ticket_caster\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641722549,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false},\"t_3\":{\"index\":\"t_3\",\"id\":\"rogue_1_recruit_ticket_sniper\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641722549,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false}}"));
        }
        if (select.equals("recruit_group_3")) {
            current.getJSONObject("inventory").put("recruit", JSONObject.parseObject((String)"{\"t_1\":{\"index\":\"t_1\",\"id\":\"rogue_1_recruit_ticket_warrior\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641722628,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false},\"t_2\":{\"index\":\"t_2\",\"id\":\"rogue_1_recruit_ticket_support\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641722628,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false},\"t_3\":{\"index\":\"t_3\",\"id\":\"rogue_1_recruit_ticket_medic\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641722628,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false}}"));
        }
        if (select.equals("recruit_group_random")) {
            current.getJSONObject("inventory").put("recruit", JSONObject.parseObject((String)"{\"t_1\":{\"index\":\"t_1\",\"id\":\"rogue_1_recruit_ticket_medic_sp\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641722698,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false},\"t_2\":{\"index\":\"t_2\",\"id\":\"rogue_1_recruit_ticket_all\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641722698,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false},\"t_3\":{\"index\":\"t_3\",\"id\":\"rogue_1_recruit_ticket_medic\",\"state\":0,\"list\":[],\"result\":null,\"ts\":1641722698,\"from\":\"initial\",\"mustExtra\":0,\"needAssist\":false}}"));
        }
        current.getJSONObject("player").getJSONArray("pending").remove(0);
        current.getJSONObject("player").getJSONArray("pending").getJSONObject(0).getJSONObject("content").getJSONObject("initRecruit").put("tickets", JSONArray.parseArray((String)"[\"t_1\",\"t_2\",\"t_3\"]"));
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        JSONObject rlv22 = new JSONObject();
        rlv22.put("current", current);
        modified.put("rlv2", rlv22);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/activeRecruitTicket"}, produces={"application/json;charset=UTF-8"})
    public JSONObject activeRecruitTicket(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /rlv2/activeRecruitTicket");
        String id = JsonBody.getString("id");
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
        JSONArray list = new JSONArray();
        JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");
        String theme = current.getJSONObject("game").getString("theme");
        JSONObject recruitTickets = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme).getJSONObject("recruitTickets");
        current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(id).put("state", 1);
        String ticketId = current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(id).getString("id");
        JSONObject tempChar = JSONObject.parseObject((String)"{\"instId\":\"0\",\"charId\":\"temp\",\"type\":\"THIRD_LOW\",\"evolvePhase\":1,\"level\":55,\"exp\":0,\"favorPoint\":25570,\"potentialRank\":0,\"mainSkillLvl\":7,\"skills\":[],\"defaultSkillIndex\":0,\"skin\":\"temp\",\"upgradeLimited\":false,\"upgradePhase\":0,\"isUpgrade\":false,\"population\":0}");
        JSONObject ticket = recruitTickets.getJSONObject(ticketId);
        JSONArray professionList = ticket.getJSONArray("professionList");
        JSONArray rarityList = ticket.getJSONArray("rarityList");
        JSONArray extraFreeRarity = ticket.getJSONArray("extraFreeRarity");
        JSONArray extraCharIds = ticket.getJSONArray("extraCharIds");
        JSONArray extraFreeList = new JSONArray();
        for (int i = 0; i < extraCharIds.size(); ++i) {
            tempChar.put("instId", (list.size() + 1));
            tempChar.put("charId", extraCharIds.getString(i));
            tempChar.put("skin", (extraCharIds.getString(i) + "#1"));
            list.add(JSONObject.parseObject((String)JSONObject.toJSONString(tempChar, (SerializerFeature[])new SerializerFeature[]{SerializerFeature.DisableCircularReferenceDetect})));
        }
        String relicsId = current.getJSONObject("inventory").getJSONObject("relic").getJSONObject("r_0").getString("id");
        JSONObject selectRelics = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme).getJSONObject("relics").getJSONObject(relicsId);
        JSONArray relicsBuffs = selectRelics.getJSONArray("buffs");
        JSONArray dynamicUpdateList = new JSONArray();
        for (int i = 0; i < relicsBuffs.size(); ++i) {
            if (!relicsBuffs.getJSONObject(i).getString("key").equals("dynamic_update")) continue;
            String band = relicsBuffs.getJSONObject(i).getJSONArray("blackboard").getJSONObject(0).getString("valueStr").substring("recruit_upgrade_".length()).toUpperCase(Locale.ROOT);
            dynamicUpdateList.add(band);
        }
        for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
            int charRarity;
            JSONObject originalChar = JSONObject.parseObject((String)entry.getValue().toString());
            JSONObject userChar = JSONObject.parseObject((String)entry.getValue().toString());
            String charId = userChar.getString("charId");
            String charProfession = ArknightsApplication.characterJson.getJSONObject(charId).getString("profession");
            if (!professionList.contains(charProfession) || !rarityList.contains((charRarity = ArknightsApplication.characterJson.getJSONObject(charId).getIntValue("rarity")))) continue;
            int charPopulation = 0;
            if (originalChar.getIntValue("evolvePhase") != 0 && originalChar.getIntValue("evolvePhase") == 2) {
                userChar.put("evolvePhase", 1);
            }
            if (userChar.getIntValue("evolvePhase") == 1) {
                if (userChar.getJSONArray("skills").size() == 1) {
                    userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", 0);
                }
                if (userChar.getJSONArray("skills").size() == 2) {
                    userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", 0);
                    userChar.getJSONArray("skills").getJSONObject(1).put("specializeLevel", 0);
                }
                if (userChar.getJSONArray("skills").size() == 3) {
                    userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", 0);
                    userChar.getJSONArray("skills").getJSONObject(1).put("specializeLevel", 0);
                    userChar.getJSONArray("skills").getJSONObject(2).put("specializeLevel", 0);
                    userChar.getJSONArray("skills").getJSONObject(2).put("unlock", 0);
                }
            }
            if (charRarity == 3) {
                charPopulation = 2;
                if (originalChar.getIntValue("level") > 60) {
                    userChar.put("level", 60);
                }
            }
            if (charRarity == 4) {
                charPopulation = 3;
                if (originalChar.getIntValue("level") > 70) {
                    userChar.put("level", 70);
                }
            }
            if (charRarity == 5) {
                charPopulation = 6;
                if (originalChar.getIntValue("level") > 80) {
                    userChar.put("level", 80);
                }
            }
            userChar.put("isUpgrade", false);
            userChar.put("upgradePhase", 0);
            userChar.put("upgradeLimited", true);
            if (charRarity >= 3) {
                userChar.put("upgradeLimited", false);
                if (dynamicUpdateList.contains(charProfession)) {
                    userChar.put("upgradeLimited", true);
                    if (originalChar.getIntValue("evolvePhase") != 0 && originalChar.getIntValue("evolvePhase") == 2) {
                        userChar.put("evolvePhase", 2);
                    }
                    userChar.put("skills", originalChar.getJSONArray("skills"));
                    if (userChar.getIntValue("evolvePhase") == 1) {
                        if (userChar.getJSONArray("skills").size() == 1) {
                            userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", 0);
                        }
                        if (userChar.getJSONArray("skills").size() == 2) {
                            userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", 0);
                            userChar.getJSONArray("skills").getJSONObject(1).put("specializeLevel", 0);
                        }
                        if (userChar.getJSONArray("skills").size() == 3) {
                            userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", 0);
                            userChar.getJSONArray("skills").getJSONObject(1).put("specializeLevel", 0);
                            userChar.getJSONArray("skills").getJSONObject(2).put("specializeLevel", 0);
                            userChar.getJSONArray("skills").getJSONObject(2).put("unlock", 0);
                        }
                    }
                    userChar.put("level", originalChar.getIntValue("level"));
                    userChar.put("upgradePhase", originalChar.getIntValue("evolvePhase"));
                }
            }
            userChar.put("rarity", charRarity);
            userChar.put("originalId", originalChar.getString("instId"));
            userChar.put("profession", charProfession);
            userChar.put("instId", (list.size() + 1));
            userChar.put("population", charPopulation);
            userChar.put("type", "NORMAL");
            Boolean upgrade = false;
            for (Map.Entry Entry2 : current.getJSONObject("troop").getJSONObject("chars").entrySet()) {
                JSONObject troopChar = JSONObject.parseObject((String)Entry2.getValue().toString());
                if (!troopChar.getString("charId").equals(charId)) continue;
                if (!troopChar.getBooleanValue("upgradeLimited")) {
                    if (charRarity == 3) {
                        charPopulation = 1;
                    }
                    if (charRarity == 4) {
                        charPopulation = 2;
                    }
                    if (charRarity == 5) {
                        charPopulation = 3;
                    }
                    if (originalChar.getIntValue("evolvePhase") != 0 && originalChar.getIntValue("evolvePhase") == 2) {
                        userChar.put("evolvePhase", 2);
                    }
                    userChar.put("isUpgrade", true);
                    userChar.put("upgradeLimited", true);
                    userChar.put("population", charPopulation);
                    userChar.put("skills", originalChar.getJSONArray("skills"));
                    userChar.put("level", originalChar.getIntValue("level"));
                    userChar.put("upgradePhase", originalChar.getIntValue("evolvePhase"));
                    continue;
                }
                upgrade = true;
            }
            if (upgrade.booleanValue()) continue;
            list.add(userChar);
        }
        for (int i = 0; i < list.size(); ++i) {
            if (!extraFreeRarity.contains(list.getJSONObject(i).getIntValue("rarity"))) continue;
            extraFreeList.add(i);
        }
        Collections.shuffle((List)extraFreeList);
        if (extraFreeList.size() != 0) {
            JSONObject userChar = list.getJSONObject(extraFreeList.getIntValue(0));
            int charRarity = userChar.getIntValue("rarity");
            userChar.put("population", 0);
            if (charRarity == 3) {
                userChar.put("level", 60);
            }
            if (charRarity == 4) {
                userChar.put("level", 70);
            }
            if (charRarity == 5) {
                userChar.put("level", 80);
            }
            userChar.put("potentialRank", 5);
            userChar.put("mainSkillLvl", 7);
            userChar.put("favorPoint", 25570);
            userChar.put("evolvePhase", 1);
            userChar.put("type", "FREE");
        }
        current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(id).put("list", list);
        JSONObject pending = new JSONObject();
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        JSONObject rlv22 = new JSONObject();
        rlv22.put("current", current);
        modified.put("rlv2", rlv22);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/recruitChar"}, produces={"application/json;charset=UTF-8"})
    public JSONObject recruitChar(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /rlv2/recruitChar");
        String ticketIndex = JsonBody.getString("ticketIndex");
        int optionId = JsonBody.getIntValue("optionId");
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
        JSONArray chars = new JSONArray();
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");
        String theme = current.getJSONObject("game").getString("theme");
        JSONObject ticket = current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(ticketIndex);
        JSONObject optChar = ticket.getJSONArray("list").getJSONObject(optionId - 1);
        if (optChar.getBooleanValue("isUpgrade")) {
            for (Map.Entry Entry2 : current.getJSONObject("troop").getJSONObject("chars").entrySet()) {
                JSONObject troopChar = JSONObject.parseObject((String)Entry2.getValue().toString());
                if (!troopChar.getString("charId").equals(optChar.getString("charId"))) continue;
                optChar.put("instId", troopChar.getIntValue("instId"));
                current.getJSONObject("troop").getJSONObject("chars").put(troopChar.getString("instId"), optChar);
                break;
            }
        } else {
            optChar.put("instId", (current.getJSONObject("troop").getJSONObject("chars").size() + 1));
            current.getJSONObject("troop").getJSONObject("chars").put(optChar.getString("instId"), optChar);
        }
        chars.add(optChar);
        ticket.put("list", new JSONArray());
        ticket.put("result", JSONObject.parseObject((String)JSONObject.toJSONString(optChar, (SerializerFeature[])new SerializerFeature[]{SerializerFeature.DisableCircularReferenceDetect})));
        ticket.put("state", 2);
        current.getJSONObject("player").getJSONObject("property").getJSONObject("population").put("cost", (current.getJSONObject("player").getJSONObject("property").getJSONObject("population").getIntValue("cost") + optChar.getIntValue("population")));
        JSONArray pending = current.getJSONObject("player").getJSONArray("pending");
        JSONArray newPending = new JSONArray();
        for (int i = 0; i < pending.size(); i++) {
            Object obj = pending.get(i);
               if (obj instanceof JSONObject) {
                    JSONObject event2 = (JSONObject) obj;
                    if (event2.getString("type").equals("RECRUIT")) {
                if (event2.getJSONObject("content").getJSONObject("recruit").getString("ticket").equals(ticketIndex)) {
                    JSONObject battleReward;
                    int pendingIndex = event2.getJSONObject("content").getJSONObject("recruit").getIntValue("pendingIndex");
                    int rewardsIndex = event2.getJSONObject("content").getJSONObject("recruit").getIntValue("rewardsIndex");
                    if (!pending.getJSONObject(pendingIndex).getString("type").equals("BATTLE_REWARD") || !(battleReward = pending.getJSONObject(pendingIndex).getJSONObject("content").getJSONObject("battleReward")).getJSONArray("rewards").getJSONObject(rewardsIndex).getBooleanValue("isRelic")) continue;
                    JSONObject relic = new JSONObject();
                    String relicIndex = "r_" + current.getJSONObject("inventory").getJSONObject("relic").size() + 1;
                    relic.put("count", 1);
                    relic.put("id", battleReward.getJSONArray("rewards").getJSONObject(rewardsIndex).getString("relicId"));
                    relic.put("index", relicIndex);
                    relic.put("ts", (new Date().getTime() / 1000L));
                    current.getJSONObject("inventory").getJSONObject("relic").put(relicIndex, relic);
                    continue;
                }
                current.getJSONObject("inventory").getJSONObject("recruit").remove(event2.getJSONObject("content").getJSONObject("recruit").getString("ticket"));
                continue;
            }
            newPending.add(event2);
               }
        }
        
        current.getJSONObject("player").put("pending", newPending);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        JSONObject rlv22 = new JSONObject();
        rlv22.put("current", current);
        modified.put("rlv2", rlv22);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("chars", chars);
        return result;
    }

    @PostMapping(value={"/closeRecruitTicket"}, produces={"application/json;charset=UTF-8"})
    public JSONObject closeRecruitTicket(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /rlv2/closeRecruitTicket");
        String ticketIndex = JsonBody.getString("id");
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
        JSONArray chars = new JSONArray();
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");
        String theme = current.getJSONObject("game").getString("theme");
        JSONObject ticket = current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(ticketIndex);
        ticket.put("state", 2);
        ticket.put("result", null);
        ticket.put("list", new JSONArray());
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        JSONObject rlv22 = new JSONObject();
        rlv22.put("current", current);
        modified.put("rlv2", rlv22);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("chars", chars);
        return result;
    }

    @PostMapping(value={"/finishEvent"}, produces={"application/json;charset=UTF-8"})
    public JSONObject finishEvent(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /rlv2/finishEvent");
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
        JSONArray chars = new JSONArray();
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");
        if (current.getJSONObject("player").getJSONArray("pending").getJSONObject(0).getString("type").equals("GAME_INIT_RECRUIT")) {
            current.getJSONObject("player").getJSONObject("cursor").put("zone", 1);
            current.getJSONObject("player").put("pending", new JSONArray());
            current.getJSONObject("player").put("state", "WAIT_MOVE");
            current.getJSONObject("map").put("zones", JSONObject.parseObject((String)"{\"1\":{\"id\":\"zone_1\",\"index\":1,\"nodes\":{\"0\":{\"index\":\"0\",\"pos\":{\"x\":0,\"y\":0},\"next\":[{\"x\":1,\"y\":0},{\"x\":1,\"y\":1}],\"type\":1,\"stage\":\"ro1_n_1_1\"},\"100\":{\"index\":\"100\",\"pos\":{\"x\":1,\"y\":0},\"next\":[{\"x\":2,\"y\":0}],\"type\":32},\"101\":{\"index\":\"101\",\"pos\":{\"x\":1,\"y\":1},\"next\":[{\"x\":2,\"y\":1}],\"type\":32},\"200\":{\"index\":\"200\",\"pos\":{\"x\":2,\"y\":0},\"next\":[{\"x\":3,\"y\":0}],\"type\":32},\"201\":{\"index\":\"201\",\"pos\":{\"x\":2,\"y\":1},\"next\":[{\"x\":3,\"y\":0}],\"type\":2},\"300\":{\"index\":\"300\",\"pos\":{\"x\":3,\"y\":0},\"next\":[],\"type\":8,\"zone_end\":true}},\"variation\":[]}}"));
        }
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        JSONObject rlv22 = new JSONObject();
        rlv22.put("current", current);
        modified.put("rlv2", rlv22);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("chars", chars);
        return result;
    }

    @PostMapping(value={"/normal/unlockBuff"}, produces={"application/json;charset=UTF-8"})
    public JSONObject unlockBuff(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /rlv2/normal/unlockBuff");
        String theme = JsonBody.getString("theme");
        String buff = JsonBody.getString("buff");
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
        JSONObject outer = UserSyncData.getJSONObject("rlv2").getJSONObject("outer").getJSONObject(theme);
        JSONObject outBuff = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme).getJSONObject("developments").getJSONObject(buff);
        outer.getJSONObject("buff").put("pointCost", (outer.getJSONObject("buff").getIntValue("pointCost") + outBuff.getIntValue("tokenCost")));
        if (outer.getJSONObject("buff").getIntValue("pointCost") > outer.getJSONObject("buff").getIntValue("pointOwned")) {
            JSONObject result = new JSONObject(true);
            result.put("msg", "error");
            return result;
        }
        outer.getJSONObject("buff").getJSONObject("unlocked").put(buff, 1);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        JSONObject rlv22 = new JSONObject();
        JSONObject tmp = new JSONObject();
        JSONObject tmp2 = new JSONObject();
        tmp2.put("buff", outer.getJSONObject("buff"));
        tmp.put(theme, tmp2);
        rlv22.put("outer", tmp);
        modified.put("rlv2", rlv22);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/moveAndBattleStart"}, produces={"application/json;charset=UTF-8"})
    public JSONObject moveAndBattleStart(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /rlv2/moveAndBattleStart");
        JSONObject moveTo = JsonBody.getJSONObject("to");
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
        JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");
        current.getJSONObject("player").put("pending", new JSONArray());
        current.getJSONObject("player").put("state", "PENDING");
        current.getJSONObject("player").getJSONObject("cursor").put("position", moveTo);
        current.getJSONObject("player").getJSONArray("trace").add(current.getJSONObject("player").getJSONObject("cursor"));
        JSONObject tmp = new JSONObject();
        JSONObject content = new JSONObject();
        JSONObject battle = new JSONObject();
        battle.put("chestCnt", 10);
        battle.put("goldTrapCnt", 10);
        battle.put("state", 1);
        battle.put("tmpChar", new JSONArray());
        JSONArray unKeepBuff = new JSONArray();
        JSONObject BattleBuff = new JSONObject();
        BattleBuff.put("key", "char_attribute_add");
        BattleBuff.put("blackboard", JSONArray.parseArray((String)"[{\"key\":\"attack_speed\",\"value\":1000.0},{\"key\":\"stack_by_res\",\"valueStr\":\"rogue_1_gold\"},{\"key\":\"stack_by_res_cnt\",\"value\":1.0}]"));
        unKeepBuff.add(BattleBuff);
        BattleBuff = new JSONObject();
        BattleBuff.put("key", "char_attribute_mul");
        BattleBuff.put("blackboard", JSONArray.parseArray((String)"[{\"key\":\"atk\",\"value\":1000.35}]"));
        unKeepBuff.add(BattleBuff);
        BattleBuff = new JSONObject();
        BattleBuff.put("key", "char_attribute_add");
        BattleBuff.put("blackboard", JSONArray.parseArray((String)"[{\"key\":\"respawn_time\",\"value\":-100.0}]"));
        unKeepBuff.add(BattleBuff);
        BattleBuff = new JSONObject();
        BattleBuff.put("key", "char_attribute_add");
        BattleBuff.put("blackboard", JSONArray.parseArray((String)"[{\"key\":\"block_cnt\",\"value\":10.0}]"));
        unKeepBuff.add(BattleBuff);
        BattleBuff = new JSONObject();
        BattleBuff.put("key", "char_attribute_add");
        BattleBuff.put("blackboard", JSONArray.parseArray((String)"[{\"key\":\"max_hp\",\"value\":1000000.0}]"));
        unKeepBuff.add(BattleBuff);
        BattleBuff = new JSONObject();
        BattleBuff.put("key", "global_buff_stack");
        BattleBuff.put("blackboard", JSONArray.parseArray((String)"[{\"key\":\"key\",\"valueStr\":\"modify_sp_recover[normal]\"},{\"key\":\"sp_recovery_per_sec\",\"value\":100.0}]"));
        unKeepBuff.add(BattleBuff);
        BattleBuff = new JSONObject();
        BattleBuff.put("key", "deck_card_buff");
        BattleBuff.put("blackboard", JSONArray.parseArray((String)"[{\"key\":\"selector.buildable\",\"valueStr\":\"ranged\"},{\"key\":\"buildable_type\",\"value\":3},{\"key\":\"selector.profession\",\"valueStr\":\"warrior|sniper|tank|medic|support|caster|special|pioneer\"}]"));
        battle.put("unKeepBuff", unKeepBuff);
        content.put("battle", battle);
        tmp.put("content", content);
        tmp.put("index", ("e_" + current.getJSONObject("player").getJSONArray("pending").size() + 1));
        tmp.put("type", "BATTLE");
        current.getJSONObject("player").getJSONArray("pending").add(tmp);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        modified.put("rlv2", UserSyncData.getJSONObject("rlv2"));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/battleFinish"}, produces={"application/json;charset=UTF-8"})
    public JSONObject battleFinish(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        JSONObject content;
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /rlv2/battleFinish");
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
        JSONObject BattleData = Utils.BattleData_decrypt((String)JsonBody.getString("data"), (String)UserSyncData.getJSONObject("pushFlags").getString("status"));
        int completeState = BattleData.getIntValue("completeState");
        if (ArknightsApplication.serverConfig.getJSONObject("battle").getBooleanValue("debug")) {
            completeState = 3;
        }
        JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");
        current.getJSONObject("player").put("pending", new JSONArray());
        if (completeState == 1) {
            content = new JSONObject();
            content.put("detailStr", "");
            content.put("popReport", false);
            content.put("success", 0);
            content.put("result", JSONObject.parseObject((String)"{\"brief\":{\"level\":4,\"over\":true,\"success\":0,\"ending\":\"\",\"theme\":\"rogue_1\",\"mode\":\"EASY\",\"predefined\":null,\"band\":\"rogue_1_band_3\",\"startTs\":1642943654,\"endTs\":1642945001,\"endZoneId\":\"zone_3\",\"endProperty\":{\"hp\":0,\"gold\":16,\"populationCost\":19,\"populationMax\":29}},\"record\":{\"cntZone\":3,\"cntBattleNormal\":5,\"cntBattleElite\":1,\"cntBattleBoss\":0,\"cntArrivedNode\":13,\"cntRecruitChar\":8,\"cntUpgradeChar\":2,\"cntKillEnemy\":217,\"cntShopBuy\":2,\"cntPerfectBattle\":6,\"cntProtectBox\":4,\"cntRecruitFree\":0,\"cntRecruitAssist\":2,\"cntRecruitNpc\":3,\"cntRecruitProfession\":{\"SNIPER\":1,\"CASTER\":1,\"PIONEER\":2,\"TANK\":1,\"WARRIOR\":2,\"MEDIC\":1},\"troopChars\":[{\"charId\":\"char_1013_chen2\",\"type\":\"ASSIST\",\"upgradePhase\":1,\"evolvePhase\":2,\"level\":90},{\"charId\":\"char_328_cammou\",\"type\":\"ASSIST\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":60},{\"charId\":\"char_504_rguard\",\"type\":\"THIRD_LOW\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":55},{\"charId\":\"char_201_moeshd\",\"type\":\"NORMAL\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":70},{\"charId\":\"char_504_rguard\",\"type\":\"THIRD_LOW\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":55},{\"charId\":\"char_143_ghost\",\"type\":\"NORMAL\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":70},{\"charId\":\"char_208_melan\",\"type\":\"NORMAL\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":55},{\"charId\":\"char_510_amedic\",\"type\":\"THIRD\",\"upgradePhase\":1,\"evolvePhase\":2,\"level\":80}],\"cntArrivedNodeType\":{\"BATTLE_NORMAL\":4,\"INCIDENT\":4,\"SHOP\":1,\"BATTLE_ELITE\":2,\"TREASURE\":1,\"REST\":1},\"relicList\":[\"rogue_1_relic_a01\",\"rogue_1_relic_r09\",\"rogue_1_relic_q02\",\"rogue_1_relic_a45\",\"rogue_1_relic_a11\"],\"capsuleList\":[\"rogue_1_capsule_3\",\"rogue_1_capsule_7\",\"rogue_1_capsule_8\"],\"activeToolList\":[],\"zones\":[{\"index\":1,\"zoneId\":\"zone_1\",\"variation\":[]},{\"index\":2,\"zoneId\":\"zone_2\",\"variation\":[]},{\"index\":3,\"zoneId\":\"zone_3\",\"variation\":[]}]}}"));
            JSONObject event2 = new JSONObject();
            event2.put("content", content);
            event2.put("index", ("e_" + current.getJSONObject("player").getJSONArray("pending").size() + 1));
            event2.put("type", "GAME_SETTLE");
            current.getJSONObject("player").getJSONArray("pending").add(event2);
        }
        content = new JSONObject();
        JSONObject battleReward = new JSONObject();
        JSONObject earn = new JSONObject();
        earn.put("damage", 0);
        earn.put("exp", 0);
        earn.put("hp", 0);
        earn.put("populationMax", 4);
        earn.put("squadCapacity", 1);
        current.getJSONObject("player").getJSONObject("property").put("capacity", (current.getJSONObject("player").getJSONObject("property").getIntValue("capacity") + 1));
        current.getJSONObject("player").getJSONObject("property").getJSONObject("population").put("max", (current.getJSONObject("player").getJSONObject("property").getJSONObject("population").getIntValue("max") + 4));
        JSONArray items = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("count", 1);
        item.put("id", "rogue_1_relic_r17");
        item.put("sub", 0);
        items.add(item);
        JSONArray rewards = new JSONArray();
        JSONObject reward = new JSONObject();
        reward.put("done", 0);
        reward.put("index", 0);
        reward.put("items", items);
        rewards.add(reward);
        battleReward.put("earn", earn);
        battleReward.put("rewards", rewards);
        battleReward.put("show", 1);
        content.put("battleReward", battleReward);
        JSONObject event3 = new JSONObject();
        event3.put("content", content);
        event3.put("index", ("e_" + current.getJSONObject("player").getJSONArray("pending").size() + 1));
        event3.put("type", "BATTLE_REWARD");
        current.getJSONObject("player").getJSONArray("pending").add(event3);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        modified.put("rlv2", UserSyncData.getJSONObject("rlv2"));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/chooseBattleReward"}, produces={"application/json;charset=UTF-8"})
    public JSONObject chooseBattleReward(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        int i;
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /rlv2/chooseBattleReward");
        int index = JsonBody.getIntValue("to");
        int sub = JsonBody.getIntValue("sub");
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
        JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");
        String theme = current.getJSONObject("game").getString("theme");
        JSONArray pending = current.getJSONObject("player").getJSONArray("pending");
        JSONObject property = current.getJSONObject("player").getJSONObject("property");
        JSONObject roguelikeItems = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme).getJSONObject("items");
        JSONObject BATTLE_REWARD = new JSONObject();
        JSONArray items = new JSONArray();
        int pendingIndex = 0;
        int rewardsIndex = 0;
        for (int i2 = 0; i2 < pending.size(); ++i2) {
            if (!pending.getJSONObject(i2).getString("type").equals("BATTLE_REWARD")) continue;
            BATTLE_REWARD = pending.getJSONObject(i2).getJSONObject("content").getJSONObject("battleReward");
            pendingIndex = i2;
        }
        JSONObject reward = new JSONObject();
        for (i = 0; i < BATTLE_REWARD.getJSONArray("rewards").size(); ++i) {
            int rewardDone = BATTLE_REWARD.getJSONArray("rewards").getJSONObject(i).getIntValue("done");
            int rewardIndex = BATTLE_REWARD.getJSONArray("rewards").getJSONObject(i).getIntValue("index");
            if (rewardIndex != index) continue;
            reward = BATTLE_REWARD.getJSONArray("rewards").getJSONObject(i);
            rewardsIndex = i;
            if (rewardDone != 0) continue;
            items = reward.getJSONArray("items");
        }
        for (i = 0; i < items.size(); ++i) {
            String ticketId;
            JSONObject relic;
            String id;
            JSONObject recruit;
            int itemCount = items.getJSONObject(i).getIntValue("count");
            int itemSub = items.getJSONObject(i).getIntValue("sub");
            String itemId = items.getJSONObject(i).getString("id");
            String type = roguelikeItems.getJSONObject(itemId).getString("type");
            reward.put("done", 1);
            if (type.equals("HP")) {
                property.put("hp", (property.getIntValue("hp") + itemCount));
            }
            if (type.equals("GOLD")) {
                property.put("gold", (property.getIntValue("gold") + itemCount));
            }
            if (type.equals("SQUAD_CAPACITY")) {
                property.put("capacity", (property.getIntValue("capacity") + itemCount));
            }
            if (type.equals("POPULATION")) {
                property.getJSONObject("population").put("max", (property.getJSONObject("population").getIntValue("max") + itemCount));
            }
            if (type.equals("EXP")) {
                property.put("exp", (property.getIntValue("exp") + itemCount));
            }
            if (type.equals("RELIC")) {
                Boolean UPGRADE = false;
                JSONObject selectRelics = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme).getJSONObject("relics").getJSONObject(itemId);
                JSONArray buffs = selectRelics.getJSONArray("buffs");
                for (int m = 0; m < buffs.size(); ++m) {
                    String key = buffs.getJSONObject(m).getString("key");
                    JSONArray blackboard = buffs.getJSONObject(m).getJSONArray("blackboard");
                    if (key.equals("level_life_point_add")) {
                        current.getJSONObject("buff").put("tmpHP", (current.getJSONObject("buff").getIntValue("tmpHP") + blackboard.getJSONObject(0).getIntValue("value")));
                    }
                    if (!key.equals("immediate_reward")) continue;
                    String valueStr = blackboard.getJSONObject(0).getString("valueStr");
                    String buffType = roguelikeItems.getJSONObject(valueStr).getString("type");
                    int count = blackboard.getJSONObject(1).getIntValue("value");
                    if (buffType.equals("HP")) {
                        property.put("hp", (property.getIntValue("hp") + count));
                    }
                    if (buffType.equals("GOLD")) {
                        property.put("gold", (property.getIntValue("gold") + count));
                    }
                    if (buffType.equals("SQUAD_CAPACITY")) {
                        property.put("capacity", (property.getIntValue("capacity") + count));
                    }
                    if (buffType.equals("POPULATION")) {
                        property.getJSONObject("population").put("max", (property.getJSONObject("population").getIntValue("max") + count));
                    }
                    if (buffType.equals("UPGRADE_TICKET")) {
                        UPGRADE = true;
                        int recruitIndex = current.getJSONObject("inventory").getJSONObject("recruit").size() + 1;
                        String id2 = "t_" + recruitIndex;
                        recruit = new JSONObject();
                        recruit.put("from", "buff");
                        recruit.put("id", valueStr);
                        recruit.put("index", ("t_" + recruitIndex));
                        recruit.put("mustExtra", 0);
                        recruit.put("needAssist", false);
                        recruit.put("state", 0);
                        recruit.put("result", null);
                        recruit.put("ts", (new Date().getTime() / 1000L));
                        current.getJSONObject("inventory").getJSONObject("recruit").put("t_" + recruitIndex, recruit);
                        JSONArray list = new JSONArray();
                        JSONObject upgradeTickets = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme).getJSONObject("upgradeTickets");
                        current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(id2).put("state", 1);
                        String ticketId2 = current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(id2).getString("id");
                        JSONObject ticket = upgradeTickets.getJSONObject(ticketId2);
                        JSONArray professionList = ticket.getJSONArray("professionList");
                        JSONArray rarityList = ticket.getJSONArray("rarityList");
                        for (Map.Entry Entry2 : current.getJSONObject("troop").getJSONObject("chars").entrySet()) {
                            JSONObject troopChar = JSONObject.parseObject((String)Entry2.getValue().toString());
                            String profession = troopChar.getString("profession");
                            int rarity = troopChar.getIntValue("rarity");
                            if (!professionList.contains(profession) || !rarityList.contains(rarity) || troopChar.getBooleanValue("upgradeLimited")) continue;
                            JSONObject originalChar = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(troopChar.getString("originalId"));
                            if (originalChar.getIntValue("evolvePhase") != 0 && originalChar.getIntValue("evolvePhase") == 2) {
                                troopChar.put("evolvePhase", 2);
                            }
                            troopChar.put("isUpgrade", true);
                            troopChar.put("upgradeLimited", true);
                            troopChar.put("population", 0);
                            troopChar.put("skills", originalChar.getJSONArray("skills"));
                            troopChar.put("level", originalChar.getIntValue("level"));
                            troopChar.put("upgradePhase", originalChar.getIntValue("evolvePhase"));
                            list.add(troopChar);
                        }
                        recruit.put("list", list);
                        JSONObject RECRUIT = new JSONObject();
                        JSONObject content = new JSONObject();
                        JSONObject recruitTicket = new JSONObject();
                        recruitTicket.put("ticket", id2);
                        recruitTicket.put("pendingIndex", pendingIndex);
                        recruitTicket.put("rewardsIndex", rewardsIndex);
                        recruitTicket.put("isRelic", true);
                        recruitTicket.put("relicId", itemId);
                        content.put("recruit", recruitTicket);
                        RECRUIT.put("content", content);
                        RECRUIT.put("index", ("e_" + current.getJSONObject("player").getJSONArray("pending").size() + 1));
                        RECRUIT.put("type", "RECRUIT");
                        current.getJSONObject("player").getJSONArray("pending").add(0, RECRUIT);
                    }
                    if (UPGRADE.booleanValue()) continue;
                    JSONObject relic2 = new JSONObject();
                    String relicIndex = "r_" + current.getJSONObject("inventory").getJSONObject("relic").size() + 1;
                    relic2.put("count", itemCount);
                    relic2.put("id", itemId);
                    relic2.put("index", relicIndex);
                    relic2.put("ts", (new Date().getTime() / 1000L));
                    current.getJSONObject("inventory").getJSONObject("relic").put(relicIndex, relic2);
                }
            }
            if (type.equals("UPGRADE_TICKET")) {
                int relicIndex = current.getJSONObject("inventory").getJSONObject("recruit").size() + 1;
                id = "t_" + relicIndex;
                relic = new JSONObject();
                relic.put("from", "buff");
                relic.put("id", itemId);
                relic.put("index", ("t_" + relicIndex));
                relic.put("mustExtra", 0);
                relic.put("needAssist", false);
                relic.put("state", 0);
                relic.put("result", null);
                relic.put("ts", (new Date().getTime() / 1000L));
                current.getJSONObject("inventory").getJSONObject("recruit").put("t_" + relicIndex, relic);
                JSONArray list = new JSONArray();
                JSONObject upgradeTickets = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme).getJSONObject("upgradeTickets");
                current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(id).put("state", 1);
                ticketId = current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(id).getString("id");
                JSONObject ticket = upgradeTickets.getJSONObject(ticketId);
                JSONArray professionList = ticket.getJSONArray("professionList");
                JSONArray rarityList = ticket.getJSONArray("rarityList");
                for (Map.Entry Entry3 : current.getJSONObject("troop").getJSONObject("chars").entrySet()) {
                    JSONObject troopChar = JSONObject.parseObject((String)Entry3.getValue().toString());
                    String profession = troopChar.getString("profession");
                    int rarity = troopChar.getIntValue("rarity");
                    if (!professionList.contains(profession) || !rarityList.contains(rarity) || troopChar.getBooleanValue("upgradeLimited")) continue;
                    JSONObject originalChar = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(troopChar.getString("originalId"));
                    if (originalChar.getIntValue("evolvePhase") != 0 && originalChar.getIntValue("evolvePhase") == 2) {
                        troopChar.put("evolvePhase", 2);
                    }
                    troopChar.put("isUpgrade", true);
                    troopChar.put("upgradeLimited", true);
                    troopChar.put("population", 0);
                    troopChar.put("skills", originalChar.getJSONArray("skills"));
                    if (troopChar.getIntValue("evolvePhase") == 1) {
                        if (troopChar.getJSONArray("skills").size() == 1) {
                            troopChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", 0);
                        }
                        if (troopChar.getJSONArray("skills").size() == 2) {
                            troopChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", 0);
                            troopChar.getJSONArray("skills").getJSONObject(1).put("specializeLevel", 0);
                        }
                        if (troopChar.getJSONArray("skills").size() == 3) {
                            troopChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", 0);
                            troopChar.getJSONArray("skills").getJSONObject(1).put("specializeLevel", 0);
                            troopChar.getJSONArray("skills").getJSONObject(2).put("specializeLevel", 0);
                            troopChar.getJSONArray("skills").getJSONObject(2).put("unlock", 0);
                        }
                    }
                    troopChar.put("level", originalChar.getIntValue("level"));
                    troopChar.put("upgradePhase", originalChar.getIntValue("evolvePhase"));
                    list.add(troopChar);
                }
                relic.put("list", list);
                JSONObject RECRUIT = new JSONObject();
                JSONObject content = new JSONObject();
                recruit = new JSONObject();
                recruit.put("ticket", id);
                recruit.put("pendingIndex", pendingIndex);
                recruit.put("rewardsIndex", rewardsIndex);
                recruit.put("isRelic", false);
                recruit.put("relicId", null);
                content.put("recruit", recruit);
                RECRUIT.put("content", content);
                RECRUIT.put("index", ("e_" + current.getJSONObject("player").getJSONArray("pending").size() + 1));
                RECRUIT.put("type", "RECRUIT");
                current.getJSONObject("player").getJSONArray("pending").add(0, RECRUIT);
            }
            if (!type.equals("RECRUIT_TICKET")) continue;
            int relicIndex = current.getJSONObject("inventory").getJSONObject("recruit").size() + 1;
            id = "t_" + relicIndex;
            relic = new JSONObject();
            relic.put("from", "battle");
            relic.put("id", itemId);
            relic.put("index", ("t_" + relicIndex));
            relic.put("mustExtra", 0);
            relic.put("needAssist", false);
            relic.put("state", 0);
            relic.put("result", null);
            relic.put("ts", (new Date().getTime() / 1000L));
            current.getJSONObject("inventory").getJSONObject("recruit").put("t_" + relicIndex, relic);
            JSONArray list = new JSONArray();
            JSONObject recruitTickets = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme).getJSONObject("recruitTickets");
            current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(id).put("state", 1);
            ticketId = current.getJSONObject("inventory").getJSONObject("recruit").getJSONObject(id).getString("id");
            JSONObject tempChar = JSONObject.parseObject((String)"{\"instId\":\"0\",\"charId\":\"temp\",\"type\":\"THIRD_LOW\",\"evolvePhase\":1,\"level\":55,\"exp\":0,\"favorPoint\":25570,\"potentialRank\":0,\"mainSkillLvl\":7,\"skills\":[],\"defaultSkillIndex\":0,\"skin\":\"temp\",\"upgradeLimited\":false,\"upgradePhase\":0,\"isUpgrade\":false,\"population\":0}");
            JSONObject ticket = recruitTickets.getJSONObject(ticketId);
            JSONArray professionList = ticket.getJSONArray("professionList");
            JSONArray rarityList = ticket.getJSONArray("rarityList");
            JSONArray extraFreeRarity = ticket.getJSONArray("extraFreeRarity");
            JSONArray extraCharIds = ticket.getJSONArray("extraCharIds");
            JSONArray extraFreeList = new JSONArray();
            for (int m = 0; m < extraCharIds.size(); ++m) {
                tempChar.put("instId", m);
                tempChar.put("charId", extraCharIds.getString(m));
                tempChar.put("skin", (extraCharIds.getString(m) + "#1"));
                list.add(JSONObject.parseObject((String)JSONObject.toJSONString(tempChar, (SerializerFeature[])new SerializerFeature[]{SerializerFeature.DisableCircularReferenceDetect})));
            }
            String relicsId = current.getJSONObject("inventory").getJSONObject("relic").getJSONObject("r_0").getString("id");
            JSONObject selectRelics = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme).getJSONObject("relics").getJSONObject(relicsId);
            JSONArray relicsBuffs = selectRelics.getJSONArray("buffs");
            JSONArray dynamicUpdateList = new JSONArray();
            for (int m = 0; m < relicsBuffs.size(); ++m) {
                if (!relicsBuffs.getJSONObject(m).getString("key").equals("dynamic_update")) continue;
                String band = relicsBuffs.getJSONObject(m).getJSONArray("blackboard").getJSONObject(0).getString("valueStr").substring("recruit_upgrade_".length()).toUpperCase(Locale.ROOT);
                dynamicUpdateList.add(band);
            }
            for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
                int charRarity;
                JSONObject originalChar = JSONObject.parseObject((String)entry.getValue().toString());
                JSONObject userChar = JSONObject.parseObject((String)entry.getValue().toString());
                String charId = userChar.getString("charId");
                String charProfession = ArknightsApplication.characterJson.getJSONObject(charId).getString("profession");
                if (!professionList.contains(charProfession) || !rarityList.contains((charRarity = ArknightsApplication.characterJson.getJSONObject(charId).getIntValue("rarity")))) continue;
                int charPopulation = 0;
                if (originalChar.getIntValue("evolvePhase") != 0 && originalChar.getIntValue("evolvePhase") == 2) {
                    userChar.put("evolvePhase", 1);
                }
                if (userChar.getIntValue("evolvePhase") == 1) {
                    if (userChar.getJSONArray("skills").size() == 1) {
                        userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", 0);
                    }
                    if (userChar.getJSONArray("skills").size() == 2) {
                        userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", 0);
                        userChar.getJSONArray("skills").getJSONObject(1).put("specializeLevel", 0);
                    }
                    if (userChar.getJSONArray("skills").size() == 3) {
                        userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", 0);
                        userChar.getJSONArray("skills").getJSONObject(1).put("specializeLevel", 0);
                        userChar.getJSONArray("skills").getJSONObject(2).put("specializeLevel", 0);
                        userChar.getJSONArray("skills").getJSONObject(2).put("unlock", 0);
                    }
                }
                if (charRarity == 3) {
                    charPopulation = 2;
                    if (originalChar.getIntValue("level") > 60) {
                        userChar.put("level", 60);
                    }
                }
                if (charRarity == 4) {
                    charPopulation = 3;
                    if (originalChar.getIntValue("level") > 70) {
                        userChar.put("level", 70);
                    }
                }
                if (charRarity == 5) {
                    charPopulation = 6;
                    if (originalChar.getIntValue("level") > 80) {
                        userChar.put("level", 80);
                    }
                }
                userChar.put("isUpgrade", false);
                userChar.put("upgradePhase", 0);
                userChar.put("upgradeLimited", true);
                if (charRarity >= 3) {
                    userChar.put("upgradeLimited", false);
                    if (dynamicUpdateList.contains(charProfession)) {
                        userChar.put("upgradeLimited", true);
                        if (originalChar.getIntValue("evolvePhase") != 0 && originalChar.getIntValue("evolvePhase") == 2) {
                            userChar.put("evolvePhase", 2);
                        }
                        userChar.put("skills", originalChar.getJSONArray("skills"));
                        if (userChar.getIntValue("evolvePhase") == 1) {
                            if (userChar.getJSONArray("skills").size() == 1) {
                                userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", 0);
                            }
                            if (userChar.getJSONArray("skills").size() == 2) {
                                userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", 0);
                                userChar.getJSONArray("skills").getJSONObject(1).put("specializeLevel", 0);
                            }
                            if (userChar.getJSONArray("skills").size() == 3) {
                                userChar.getJSONArray("skills").getJSONObject(0).put("specializeLevel", 0);
                                userChar.getJSONArray("skills").getJSONObject(1).put("specializeLevel", 0);
                                userChar.getJSONArray("skills").getJSONObject(2).put("specializeLevel", 0);
                                userChar.getJSONArray("skills").getJSONObject(2).put("unlock", 0);
                            }
                        }
                        userChar.put("level", originalChar.getIntValue("level"));
                        userChar.put("upgradePhase", originalChar.getIntValue("evolvePhase"));
                    }
                }
                userChar.put("rarity", charRarity);
                userChar.put("originalId", originalChar.getString("instId"));
                userChar.put("profession", charProfession);
                userChar.put("instId", list.size());
                userChar.put("population", charPopulation);
                userChar.put("type", "NORMAL");
                Boolean upgrade = false;
                for (Map.Entry Entry4 : current.getJSONObject("troop").getJSONObject("chars").entrySet()) {
                    JSONObject troopChar = JSONObject.parseObject((String)Entry4.getValue().toString());
                    if (!troopChar.getString("charId").equals(charId)) continue;
                    if (!troopChar.getBooleanValue("upgradeLimited")) {
                        if (charRarity == 3) {
                            charPopulation = 1;
                        }
                        if (charRarity == 4) {
                            charPopulation = 2;
                        }
                        if (charRarity == 5) {
                            charPopulation = 3;
                        }
                        if (originalChar.getIntValue("evolvePhase") != 0 && originalChar.getIntValue("evolvePhase") == 2) {
                            userChar.put("evolvePhase", 2);
                        }
                        userChar.put("isUpgrade", true);
                        userChar.put("upgradeLimited", true);
                        userChar.put("population", charPopulation);
                        userChar.put("skills", originalChar.getJSONArray("skills"));
                        userChar.put("level", originalChar.getIntValue("level"));
                        userChar.put("upgradePhase", originalChar.getIntValue("evolvePhase"));
                        continue;
                    }
                    upgrade = true;
                }
                if (upgrade.booleanValue()) continue;
                list.add(userChar);
            }
            for (int m = 0; m < list.size(); ++m) {
                if (!extraFreeRarity.contains(list.getJSONObject(m).getIntValue("rarity"))) continue;
                extraFreeList.add(m);
            }
            Collections.shuffle((List)extraFreeList);
            if (extraFreeList.size() != 0) {
                JSONObject userChar = list.getJSONObject(extraFreeList.getIntValue(0));
                int charRarity = userChar.getIntValue("rarity");
                userChar.put("population", 0);
                if (charRarity == 3) {
                    userChar.put("level", 60);
                }
                if (charRarity == 4) {
                    userChar.put("level", 70);
                }
                if (charRarity == 5) {
                    userChar.put("level", 80);
                }
                userChar.put("potentialRank", 5);
                userChar.put("mainSkillLvl", 7);
                userChar.put("favorPoint", 25570);
                userChar.put("evolvePhase", 1);
                userChar.put("type", "FREE");
            }
            relic.put("list", list);
            JSONObject RECRUIT = new JSONObject();
            JSONObject content = new JSONObject();
            JSONObject recruit2 = new JSONObject();
            recruit2.put("ticket", id);
            recruit2.put("pendingIndex", pendingIndex);
            recruit2.put("rewardsIndex", rewardsIndex);
            recruit2.put("isRelic", false);
            recruit2.put("relicId", null);
            content.put("recruit", recruit2);
            RECRUIT.put("content", content);
            RECRUIT.put("index", ("e_" + current.getJSONObject("player").getJSONArray("pending").size() + 1));
            RECRUIT.put("type", "RECRUIT");
            current.getJSONObject("player").getJSONArray("pending").add(0, RECRUIT);
        }
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        modified.put("rlv2", UserSyncData.getJSONObject("rlv2"));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/finishBattleReward"}, produces={"application/json;charset=UTF-8"})
    public JSONObject finishBattleReward(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /rlv2/finishBattleReward");
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
        JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");
        current.getJSONObject("player").put("pending", new JSONArray());
        current.getJSONObject("player").put("state", "WAIT_MOVE");
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        modified.put("rlv2", UserSyncData.getJSONObject("rlv2"));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/gameSettle"}, produces={"application/json;charset=UTF-8"})
    public JSONObject gameSettle(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /rlv2/gameSettle");
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
        JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");
        current.put("buff", null);
        current.put("game", null);
        current.put("inventory", null);
        current.put("map", null);
        current.put("player", null);
        current.put("record", null);
        current.put("troop", null);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        modified.put("rlv2", UserSyncData.getJSONObject("rlv2"));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("outer", JSONObject.parseObject((String)"{\"mission\":{\"before\":[{\"type\":\"A\",\"tmpl\":\"CerternItem\",\"id\":\"rogue_1_task_25\",\"state\":1,\"target\":6,\"value\":6},{\"type\":\"B\",\"tmpl\":\"PassNodeType\",\"id\":\"rogue_1_task_13\",\"state\":1,\"target\":24,\"value\":24},{\"type\":\"C\",\"tmpl\":\"UsePopulation\",\"id\":\"rogue_1_task_11\",\"state\":1,\"target\":75,\"value\":76},{\"type\":\"C\",\"tmpl\":\"KillEnemy\",\"id\":\"rogue_1_task_3\",\"state\":1,\"target\":500,\"value\":641}],\"after\":[{\"type\":\"A\",\"tmpl\":\"CerternItem\",\"id\":\"rogue_1_task_25\",\"state\":1,\"target\":6,\"value\":6},{\"type\":\"B\",\"tmpl\":\"PassNodeType\",\"id\":\"rogue_1_task_13\",\"state\":1,\"target\":24,\"value\":24},{\"type\":\"C\",\"tmpl\":\"UsePopulation\",\"id\":\"rogue_1_task_11\",\"state\":1,\"target\":75,\"value\":76},{\"type\":\"C\",\"tmpl\":\"KillEnemy\",\"id\":\"rogue_1_task_3\",\"state\":1,\"target\":500,\"value\":641}]},\"missionBp\":{\"cnt\":0,\"from\":7594,\"to\":7594},\"relicBp\":{\"cnt\":0,\"from\":7594,\"to\":7594},\"relicUnlock\":[],\"gp\":0}"));
        result.put("game", JSONObject.parseObject((String)"{\"brief\":{\"level\":4,\"over\":true,\"success\":0,\"ending\":\"\",\"theme\":\"rogue_1\",\"mode\":\"EASY\",\"predefined\":null,\"band\":\"rogue_1_band_3\",\"startTs\":1642943654,\"endTs\":1642945001,\"endZoneId\":\"zone_3\",\"endProperty\":{\"hp\":0,\"gold\":16,\"populationCost\":19,\"populationMax\":29}},\"record\":{\"cntZone\":3,\"cntBattleNormal\":5,\"cntBattleElite\":1,\"cntBattleBoss\":0,\"cntArrivedNode\":13,\"cntRecruitChar\":8,\"cntUpgradeChar\":2,\"cntKillEnemy\":217,\"cntShopBuy\":2,\"cntPerfectBattle\":6,\"cntProtectBox\":4,\"cntRecruitFree\":0,\"cntRecruitAssist\":2,\"cntRecruitNpc\":3,\"cntRecruitProfession\":{\"SNIPER\":1,\"CASTER\":1,\"PIONEER\":2,\"TANK\":1,\"WARRIOR\":2,\"MEDIC\":1},\"troopChars\":[{\"charId\":\"char_1013_chen2\",\"type\":\"ASSIST\",\"upgradePhase\":1,\"evolvePhase\":2,\"level\":90},{\"charId\":\"char_328_cammou\",\"type\":\"ASSIST\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":60},{\"charId\":\"char_504_rguard\",\"type\":\"THIRD_LOW\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":55},{\"charId\":\"char_201_moeshd\",\"type\":\"NORMAL\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":70},{\"charId\":\"char_504_rguard\",\"type\":\"THIRD_LOW\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":55},{\"charId\":\"char_143_ghost\",\"type\":\"NORMAL\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":70},{\"charId\":\"char_208_melan\",\"type\":\"NORMAL\",\"upgradePhase\":0,\"evolvePhase\":1,\"level\":55},{\"charId\":\"char_510_amedic\",\"type\":\"THIRD\",\"upgradePhase\":1,\"evolvePhase\":2,\"level\":80}],\"cntArrivedNodeType\":{\"BATTLE_NORMAL\":4,\"INCIDENT\":4,\"SHOP\":1,\"BATTLE_ELITE\":2,\"TREASURE\":1,\"REST\":1},\"relicList\":[\"rogue_1_relic_a01\",\"rogue_1_relic_r09\",\"rogue_1_relic_q02\",\"rogue_1_relic_a45\",\"rogue_1_relic_a11\"],\"capsuleList\":[\"rogue_1_capsule_3\",\"rogue_1_capsule_7\",\"rogue_1_capsule_8\"],\"activeToolList\":[],\"zones\":[{\"index\":1,\"zoneId\":\"zone_1\",\"variation\":[]},{\"index\":2,\"zoneId\":\"zone_2\",\"variation\":[]},{\"index\":3,\"zoneId\":\"zone_3\",\"variation\":[]}]},\"score\":{\"detail\":[[2,80],[13,13],[5,50],[1,20],[0,0],[7,35],[7,14]],\"scoreFactor\":0.5,\"score\":106,\"buff\":1.08,\"bp\":{\"cnt\":114,\"from\":7480,\"to\":7594},\"gp\":11}}"));
        return result;
    }

    @PostMapping(value={"/moveTo"}, produces={"application/json;charset=UTF-8"})
    public JSONObject moveTo(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /rlv2/gameSettle");
        JSONObject moveTo = JsonBody.getJSONObject("to");
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
        JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");
        current.getJSONObject("player").getJSONObject("cursor").put("position", moveTo);
        current.getJSONObject("player").getJSONArray("trace").add(current.getJSONObject("player").getJSONObject("cursor"));
        JSONObject pending = new JSONObject();
        JSONObject content = new JSONObject();
        JSONObject scene = new JSONObject();
        content.put("scene", scene);
        pending.put("content", content);
        pending.put("index", ("e_" + current.getJSONObject("player").getJSONArray("pending").size() + 1));
        pending.put("type", "SCENE");
        current.getJSONObject("player").getJSONArray("pending").add(pending);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        modified.put("rlv2", UserSyncData.getJSONObject("rlv2"));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/battlePass/getReward"}, produces={"application/json;charset=UTF-8"})
    public JSONObject getReward(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        LOGGER.info("[/" + clientIp + "] /rlv2/battlePass/getReward");
        String theme = JsonBody.getString("theme");
        JSONArray rewards = JsonBody.getJSONArray("rewards");
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
        JSONObject bp = UserSyncData.getJSONObject("rlv2").getJSONObject("outer").getJSONObject(theme).getJSONObject("bp");
        JSONArray milestones = ArknightsApplication.roguelikeTable.getJSONObject("details").getJSONObject(theme).getJSONArray("milestones");
        JSONArray items = new JSONArray();
        Boolean isChar = false;
        for (int i = 0; i < rewards.size(); ++i) {
            String bp_level = rewards.getString(i);
            int index = Integer.valueOf((String)bp_level.substring(9)) - 1;
            String itemID = milestones.getJSONObject(index).getString("itemID");
            String itemType = milestones.getJSONObject(index).getString("itemType");
            int itemCount = milestones.getJSONObject(index).getIntValue("itemCount");
            if (itemType.equals("CHAR")) {
                isChar = true;
            }
            Admin.GM_GiveItem((JSONObject)UserSyncData, (String)itemID, (String)itemType, (int)itemCount, (JSONArray)items);
            if (ArknightsApplication.serverConfig.getJSONObject("roguelike").getBooleanValue("unlimitedMilestones")) continue;
            bp.getJSONObject("reward").put(bp_level, 1);
        }
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        if (!ArknightsApplication.serverConfig.getJSONObject("roguelike").getBooleanValue("unlimitedMilestones")) {
            modified.put("rlv2", UserSyncData.getJSONObject("rlv2"));
        }
        modified.put("status", UserSyncData.getJSONObject("status"));
        modified.put("inventory", UserSyncData.getJSONObject("inventory"));
        if (isChar.booleanValue()) {
            modified.put("troop", UserSyncData.getJSONObject("troop"));
        }
        playerDataDelta.put("deleted", new JSONObject(true));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("items", items);
        result.put("result", 0);
        return result;
    }
}

