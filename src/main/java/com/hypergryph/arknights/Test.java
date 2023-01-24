/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSON
 *  com.alibaba.fastjson.JSONArray
 *  com.alibaba.fastjson.JSONObject
 *  com.alibaba.fastjson.serializer.SerializerFeature
 *  com.hypergryph.arknights.ArknightsApplication
 *  com.hypergryph.arknights.Test
 *  com.hypergryph.arknights.core.dao.userDao
 *  com.hypergryph.arknights.core.file.IOTools
 *  com.hypergryph.arknights.core.pojo.Account
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
 *  java.util.List
 *  java.util.Map$Entry
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestParam
 *  org.springframework.web.bind.annotation.RestController
 */
package com.hypergryph.arknights;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.file.IOTools;
import com.hypergryph.arknights.core.pojo.Account;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/test"})
public class Test {
    @RequestMapping(value={"/set/map"})
    public String setMap() {
        long uid = 10000001L;
        List Accounts = userDao.queryAccountByUid((long)uid);
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        JSONObject current = UserSyncData.getJSONObject("rlv2").getJSONObject("current");
        current.getJSONObject("map").put("zones", IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/map.json")));
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        return "ok";
    }

    @RequestMapping(value={"/query"})
    public String query(@RequestParam String name) {
        ArknightsApplication.LOGGER.info(userDao.queryNickName((String)name).size());
        return "ok";
    }

    @RequestMapping(value={"/set"})
    public String set(@RequestParam String test) {
        return "ok";
    }

    @RequestMapping(value={"/itemtype"})
    public JSONArray itemtype() {
        JSONObject itemtype = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/excel/item_table.json")).getJSONObject("items");
        JSONArray type = new JSONArray();
        for (Map.Entry entry : itemtype.entrySet()) {
            String itemType = itemtype.getJSONObject((String)entry.getKey()).getString("itemType");
            if (type.contains(itemType)) continue;
            type.add(itemType);
        }
        return type;
    }

    @RequestMapping(value={"/sub"})
    public String sub() {
        String charId = "char_179_cgbird";
        String sub1 = charId.substring(charId.indexOf("_") + 1);
        String charName = sub1.substring(sub1.indexOf("_") + 1);
        return charName;
    }

    @RequestMapping(value={"/pwd"})
    public String pwd(@RequestParam String GMKey) {
        return GMKey;
    }

    @RequestMapping(value={"/roguelike"})
    public String roguelike() {
        JSONObject test = new JSONObject();
        for (Map.Entry entry : ArknightsApplication.stageTable.entrySet()) {
            JSONObject stageData = ArknightsApplication.stageTable.getJSONObject(entry.getKey().toString());
            JSONObject stage = new JSONObject();
            stage.put("id", stageData.getString("stageId"));
            stage.put("linkedStageId", "");
            stage.put("levelId", stageData.getString("levelId"));
            stage.put("code", ("ISW-" + stageData.getString("code")));
            stage.put("name", stageData.getString("name"));
            stage.put("loadingPicId", "loading_PCS");
            stage.put("isBoss", 0);
            stage.put("isElite", 0);
            stage.put("difficulty", stageData.getString("difficulty"));
            stage.put("enlargeId", null);
            stage.put("capsulePool", "pool_capsule_default");
            stage.put("capsuleProb", 1.0);
            stage.put("vutresProb", new JSONArray());
            stage.put("description", stageData.getString("description"));
            stage.put("eliteDesc", stageData.getString("description"));
            if (stageData.getString("difficulty").equals("NORMAL")) {
                stage.put("eliteDesc", null);
            }
            if (stageData.getString("difficulty").equals("FOUR_STAR")) {
                stage.put("isElite", 1);
            }
            if (stageData.getBooleanValue("bossMark")) {
                stage.put("isBoss", 1);
            }
            test.put(entry.getKey().toString(), stage);
        }
        return JSON.toJSONString(test, (SerializerFeature[])new SerializerFeature[]{SerializerFeature.WriteMapNullValue});
    }

    @RequestMapping(value={"/stage"})
    public JSONObject stage() {
        JSONObject data;
        JSONObject stageData;
        JSONArray test = new JSONArray();
        JSONObject a = new JSONObject(true);
        JSONObject b = new JSONObject(true);
        a.put("MainStage", new JSONObject(true));
        for (Map.Entry entry : ArknightsApplication.stageTable.entrySet()) {
            stageData = ArknightsApplication.stageTable.getJSONObject(entry.getKey().toString());
            if (stageData.getString("stageId").indexOf("act15side") == -1) continue;
            data = new JSONObject(true);
            data.put("next", null);
            data.put("star", null);
            data.put("sub", null);
            data.put("hard", null);
            a.getJSONObject("MainStage").put(stageData.getString("stageId"), data);
        }
        b.put("stage", new JSONObject(true));
        for (Map.Entry entry : ArknightsApplication.stageTable.entrySet()) {
            stageData = ArknightsApplication.stageTable.getJSONObject(entry.getKey().toString());
            if (stageData.getString("stageId").indexOf("act15side") == -1) continue;
            data = new JSONObject(true);
            data.put("stageId", stageData.getString("stageId"));
            data.put("completeTimes", 1);
            data.put("startTimes", 1);
            data.put("practiceTimes", 0);
            data.put("state", 3);
            data.put("hasBattleReplay", 0);
            data.put("noCostCnt", 0);
            b.getJSONObject("stage").put(stageData.getString("stageId"), data);
        }
        System.out.println(JSON.toJSONString(a, (SerializerFeature[])new SerializerFeature[]{SerializerFeature.WriteMapNullValue}));
        System.out.println(JSON.toJSONString(b, (SerializerFeature[])new SerializerFeature[]{SerializerFeature.WriteMapNullValue}));
        JSONObject stage = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/battle/stage.json"));
        return stage;
    }
}

