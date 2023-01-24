/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONArray
 *  com.alibaba.fastjson.JSONObject
 *  com.hypergryph.arknights.ArknightsApplication
 *  com.hypergryph.arknights.core.dao.userDao
 *  com.hypergryph.arknights.core.file.IOTools
 *  com.hypergryph.arknights.core.pojo.Account
 *  com.hypergryph.arknights.game.gacha
 *  java.io.File
 *  java.lang.Boolean
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
 *  java.util.Collections
 *  java.util.Date
 *  java.util.List
 *  java.util.Random
 *  java.util.stream.IntStream
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
import com.hypergryph.arknights.core.file.IOTools;
import com.hypergryph.arknights.core.pojo.Account;
import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/gacha"})
public class gacha {
    @PostMapping(value={"/syncNormalGacha"})
    public JSONObject SyncNormalGacha(@RequestHeader(value="secret") String secret, HttpServletResponse response, HttpServletRequest request) {
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
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        modified.put("recruit", UserSyncData.getJSONObject("recruit"));
        playerDataDelta.put("modified", modified);
        playerDataDelta.put("deleted", new JSONObject(true));
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/normalGacha"})
    public JSONObject normalGacha(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /gacha/normalGacha");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        String slotId = JsonBody.getString("slotId");
        JSONArray tagList = JsonBody.getJSONArray("tagList");
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
        UserSyncData.getJSONObject("recruit").getJSONObject("normal").getJSONObject("slots").getJSONObject(String.valueOf(slotId)).put("state", 2);
        JSONArray selectTags = new JSONArray();
        for (int i = 0; i < tagList.size(); ++i) {
            JSONObject selectTag = new JSONObject(true);
            selectTag.put("pick", 1);
            selectTag.put("tagId", tagList.getIntValue(i));
            selectTags.add(selectTag);
        }
        UserSyncData.getJSONObject("recruit").getJSONObject("normal").getJSONObject("slots").getJSONObject(String.valueOf(slotId)).put("selectTags", selectTags);
        UserSyncData.getJSONObject("status").put("recruitLicense", (UserSyncData.getJSONObject("status").getIntValue("recruitLicense") - 1));
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        modified.put("recruit", UserSyncData.getJSONObject("recruit"));
        modified.put("status", UserSyncData.getJSONObject("status"));
        playerDataDelta.put("modified", modified);
        playerDataDelta.put("deleted", new JSONObject(true));
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/finishNormalGacha"})
    public JSONObject finishNormalGacha(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /gacha/finishNormalGacha");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        String slotId = JsonBody.getString("slotId");
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
        JSONObject chars = UserSyncData.getJSONObject("troop").getJSONObject("chars");
        JSONObject buildingChars = UserSyncData.getJSONObject("building").getJSONObject("chars");
        JSONArray availCharInfo = ArknightsApplication.normalGachaData.getJSONObject("detailInfo").getJSONObject("availCharInfo").getJSONArray("perAvailList");
        JSONArray randomRankArray = new JSONArray();
        for (int i = 0; i < availCharInfo.size(); ++i) {
            int totalPercent = (int)(availCharInfo.getJSONObject(i).getFloat("totalPercent").floatValue() * 100.0f);
            int rarityRank = availCharInfo.getJSONObject(i).getIntValue("rarityRank");
            JSONObject randomRankObject = new JSONObject(true);
            randomRankObject.put("rarityRank", rarityRank);
            randomRankObject.put("index", i);
            IntStream.range((int)0, (int)totalPercent).forEach(n -> randomRankArray.add(randomRankObject));
        }
        Collections.shuffle((List)randomRankArray);
        JSONObject randomRank = randomRankArray.getJSONObject(new Random().nextInt(randomRankArray.size()));
        JSONArray randomCharArray = availCharInfo.getJSONObject(randomRank.getIntValue("index")).getJSONArray("charIdList");
        Collections.shuffle((List)randomCharArray);
        String randomCharId = randomCharArray.getString(new Random().nextInt(randomCharArray.size()));
        int repeatCharId = 0;
        for (int i = 0; i < UserSyncData.getJSONObject("troop").getJSONObject("chars").size(); ++i) {
            if (!UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)(i + 1))).getString("charId").equals(randomCharId)) continue;
            repeatCharId = i + 1;
            break;
        }
        JSONArray itemGet = new JSONArray();
        int isNew = 0;
        int charinstId = repeatCharId;
        if (repeatCharId == 0) {
            int instId;
            JSONObject char_data = new JSONObject(true);
            JSONArray skilsArray = ArknightsApplication.characterJson.getJSONObject(randomCharId).getJSONArray("skills");
            JSONArray skils = new JSONArray();
            for (int i = 0; i < skilsArray.size(); ++i) {
                JSONObject new_skils = new JSONObject(true);
                new_skils.put("skillId", skilsArray.getJSONObject(i).getString("skillId"));
                new_skils.put("state", 0);
                new_skils.put("specializeLevel", 0);
                new_skils.put("completeUpgradeTime", -1);
                if (skilsArray.getJSONObject(i).getJSONObject("unlockCond").getIntValue("phase") == 0) {
                    new_skils.put("unlock", 1);
                } else {
                    new_skils.put("unlock", 0);
                }
                skils.add(new_skils);
            }
            charinstId = instId = UserSyncData.getJSONObject("troop").getJSONObject("chars").size() + 1;
            char_data.put("instId", instId);
            char_data.put("charId", randomCharId);
            char_data.put("favorPoint", 0);
            char_data.put("potentialRank", 0);
            char_data.put("mainSkillLvl", 1);
            char_data.put("skin", (randomCharId + "#1"));
            char_data.put("level", 1);
            char_data.put("exp", 0);
            char_data.put("evolvePhase", 0);
            char_data.put("gainTime", (new Date().getTime() / 1000L));
            char_data.put("skills", skils);
            char_data.put("equip", new JSONObject(true));
            char_data.put("voiceLan", ArknightsApplication.charwordTable.getJSONObject("charDefaultTypeDict").getString(randomCharId));
            if (skils == new JSONArray()) {
                char_data.put("defaultSkillIndex", -1);
            } else {
                char_data.put("defaultSkillIndex", 0);
            }
            String sub1 = randomCharId.substring(randomCharId.indexOf("_") + 1);
            String charName = sub1.substring(sub1.indexOf("_") + 1);
            if (ArknightsApplication.uniequipTable.containsKey(("uniequip_001_" + charName))) {
                JSONObject equip = new JSONObject(true);
                JSONObject uniequip_001 = new JSONObject(true);
                uniequip_001.put("hide", 0);
                uniequip_001.put("locked", 0);
                uniequip_001.put("level", 1);
                JSONObject uniequip_002 = new JSONObject(true);
                uniequip_002.put("hide", 0);
                uniequip_002.put("locked", 0);
                uniequip_002.put("level", 1);
                equip.put("uniequip_001_" + charName, uniequip_001);
                equip.put("uniequip_002_" + charName, uniequip_002);
                char_data.put("equip", equip);
                char_data.put("currentEquip", ("uniequip_001_" + charName));
            } else {
                char_data.put("currentEquip", null);
            }
            UserSyncData.getJSONObject("troop").getJSONObject("chars").put(String.valueOf((int)instId), char_data);
            JSONObject charGroup = new JSONObject(true);
            charGroup.put("favorPoint", 0);
            UserSyncData.getJSONObject("troop").getJSONObject("charGroup").put(randomCharId, charGroup);
            JSONObject buildingChar = new JSONObject(true);
            buildingChar.put("charId", randomCharId);
            buildingChar.put("lastApAddTime", (new Date().getTime() / 1000L));
            buildingChar.put("ap", 8640000);
            buildingChar.put("roomSlotId", "");
            buildingChar.put("index", -1);
            buildingChar.put("changeScale", 0);
            JSONObject bubble = new JSONObject(true);
            JSONObject normal = new JSONObject(true);
            normal.put("add", -1);
            normal.put("ts", 0);
            bubble.put("normal", normal);
            JSONObject assist = new JSONObject(true);
            assist.put("add", -1);
            assist.put("ts", -1);
            bubble.put("assist", assist);
            buildingChar.put("bubble", bubble);
            buildingChar.put("workTime", 0);
            buildingChars.put(String.valueOf((int)instId), buildingChar);
            chars.put(String.valueOf((int)instId), char_data);
            JSONObject SHD = new JSONObject(true);
            SHD.put("type", "HGG_SHD");
            SHD.put("id", "4004");
            SHD.put("count", 1);
            itemGet.add(SHD);
            isNew = 1;
            UserSyncData.getJSONObject("status").put("hggShard", (UserSyncData.getJSONObject("status").getIntValue("hggShard") + 1));
        } else {
            JSONObject repatChar = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)repeatCharId));
            int potentialRank = repatChar.getIntValue("potentialRank");
            int rarity = ArknightsApplication.characterJson.getJSONObject(randomCharId).getIntValue("rarity");
            String itemName = null;
            String itemType = null;
            String itemId = null;
            int itemCount = 0;
            if (rarity == 0) {
                itemName = "lggShard";
                itemType = "LGG_SHD";
                itemId = "4005";
                itemCount = 1;
            }
            if (rarity == 1) {
                itemName = "lggShard";
                itemType = "LGG_SHD";
                itemId = "4005";
                itemCount = 1;
            }
            if (rarity == 2) {
                itemName = "lggShard";
                itemType = "LGG_SHD";
                itemId = "4005";
                itemCount = 5;
            }
            if (rarity == 3) {
                itemName = "lggShard";
                itemType = "LGG_SHD";
                itemId = "4005";
                itemCount = 30;
            }
            if (rarity == 4) {
                itemName = "hggShard";
                itemType = "HGG_SHD";
                itemId = "4004";
                itemCount = potentialRank != 5 ? 5 : 8;
            }
            if (rarity == 5) {
                itemName = "hggShard";
                itemType = "HGG_SHD";
                itemId = "4004";
                itemCount = potentialRank != 5 ? 10 : 15;
            }
            JSONObject SHD = new JSONObject(true);
            SHD.put("type", itemType);
            SHD.put("id", itemId);
            SHD.put("count", itemCount);
            itemGet.add(SHD);
            JSONObject potential = new JSONObject(true);
            potential.put("type", "MATERIAL");
            potential.put("id", ("p_" + randomCharId));
            potential.put("count", 1);
            itemGet.add(potential);
            UserSyncData.getJSONObject("status").put(itemName, (UserSyncData.getJSONObject("status").getIntValue(itemName) + itemCount));
            UserSyncData.getJSONObject("inventory").put("p_" + randomCharId, (UserSyncData.getJSONObject("inventory").getIntValue("p_" + randomCharId) + 1));
            chars.put(String.valueOf((int)repeatCharId), UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)repeatCharId)));
        }
        UserSyncData.getJSONObject("troop").put("chars", chars);
        UserSyncData.getJSONObject("recruit").getJSONObject("normal").getJSONObject("slots").getJSONObject(String.valueOf(slotId)).put("state", 1);
        UserSyncData.getJSONObject("recruit").getJSONObject("normal").getJSONObject("slots").getJSONObject(String.valueOf(slotId)).put("selectTags", new JSONArray());
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject charGet = new JSONObject(true);
        charGet.put("itemGet", itemGet);
        charGet.put("charId", randomCharId);
        charGet.put("charInstId", charinstId);
        charGet.put("isNew", isNew);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        modified.put("recruit", UserSyncData.getJSONObject("recruit"));
        modified.put("status", UserSyncData.getJSONObject("status"));
        modified.put("troop", UserSyncData.getJSONObject("troop"));
        playerDataDelta.put("modified", modified);
        playerDataDelta.put("deleted", new JSONObject(true));
        result.put("playerDataDelta", playerDataDelta);
        result.put("charGet", charGet);
        return result;
    }

    @PostMapping(value={"/getPoolDetail"})
    public JSONObject GetPoolDetail(@RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /gacha/getPoolDetail");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        String poolId = JsonBody.getString("poolId");
        String PoolPath = System.getProperty((String)"user.dir") + "/data/gacha/" + poolId + ".json";
        if (!new File(PoolPath).exists()) {
            JSONObject result = new JSONObject(true);
            JSONObject detailInfo = new JSONObject();
            JSONObject availCharInfo = new JSONObject();
            availCharInfo.put("perAvailList", new JSONArray());
            detailInfo.put("availCharInfo", availCharInfo);
            detailInfo.put("limitedChar", null);
            detailInfo.put("weightUpCharInfo", null);
            JSONArray gachaObjList = new JSONArray();
            JSONObject Text0 = new JSONObject();
            JSONObject Text7 = new JSONObject();
            Text7.put("gachaObject", "TEXT");
            Text7.put("type", 7);
            Text7.put("param", poolId);
            JSONObject Text8 = new JSONObject();
            Text8.put("gachaObject", "TEXT");
            Text8.put("type", 5);
            Text8.put("param", "该卡池尚未实装，无法获取详细信息");
            gachaObjList.add(Text7);
            gachaObjList.add(Text8);
            detailInfo.put("gachaObjList", gachaObjList);
            result.put("detailInfo", detailInfo);
            return result;
        }
        return IOTools.ReadJsonFile((String)PoolPath);
    }

    @PostMapping(value={"/advancedGacha"})
    public JSONObject advancedGacha(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /gacha/advancedGacha");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        if (JsonBody.getString("poolId").equals("BOOT_0_1_1")) {
            return this.Gacha("gachaTicket", 380, secret, JsonBody);
        }
        return this.Gacha("gachaTicket", 600, secret, JsonBody);
    }

    @PostMapping(value={"/tenAdvancedGacha"})
    public JSONObject tenAdvancedGacha(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /gacha/tenAdvancedGacha");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        if (JsonBody.getString("poolId").equals("BOOT_0_1_1")) {
            return this.Gacha("tenGachaTicket", 3800, secret, JsonBody);
        }
        return this.Gacha("tenGachaTicket", 6000, secret, JsonBody);
    }

    public JSONObject Gacha(String type, int useDiamondShard, String secret, JSONObject JsonBody) {
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        String poolId = JsonBody.getString("poolId");
        String poolPath = System.getProperty((String)"user.dir") + "/data/gacha/" + poolId + ".json";
        int useTkt = JsonBody.getIntValue("useTkt");
        if (!new File(poolPath).exists()) {
            JSONObject result = new JSONObject(true);
            result.put("result", 1);
            result.put("errMsg", "该当前干员寻访无法使用，详情请关注官方公告");
            return result;
        }
        JSONObject poolJson = IOTools.ReadJsonFile((String)poolPath);
        JSONArray gachaResultList = new JSONArray();
        JSONArray newChars = new JSONArray();
        JSONObject charGet = new JSONObject(true);
        JSONObject troop = new JSONObject(true);
        JSONObject chars = UserSyncData.getJSONObject("troop").getJSONObject("chars");
        int usedimmond = 0;
        usedimmond = JsonBody.getString("poolId").equals("BOOT_0_1_1") ? useDiamondShard / 380 : useDiamondShard / 600;
        for (int count = 0; count < usedimmond; ++count) {
            JSONObject get_char;
            int cnt;
            JSONObject result;
            if (useTkt == 1 || useTkt == 2) {
                if (UserSyncData.getJSONObject("status").getIntValue(type) <= 0) {
                    result = new JSONObject(true);
                    result.put("result", 2);
                    result.put("errMsg", "剩余寻访凭证不足");
                    return result;
                }
            } else if (UserSyncData.getJSONObject("status").getIntValue("diamondShard") < useDiamondShard) {
                result = new JSONObject(true);
                result.put("result", 3);
                result.put("errMsg", "剩余合成玉不足");
                return result;
            }
            Boolean Minimum = false;
            String poolObjecName = null;
            JSONObject Pool = new JSONObject(true);
            if (JsonBody.getString("poolId").equals("BOOT_0_1_1")) {
                poolObjecName = "newbee";
                Pool = UserSyncData.getJSONObject("gacha").getJSONObject(poolObjecName);
                cnt = Pool.getIntValue("cnt") - 1;
                UserSyncData.getJSONObject("gacha").getJSONObject(poolObjecName).put("cnt", cnt);
                UserSyncData.getJSONObject("status").put("gachaCount", (UserSyncData.getJSONObject("status").getIntValue("gachaCount") + 1));
                if (cnt == 0) {
                    UserSyncData.getJSONObject("gacha").getJSONObject(poolObjecName).put("openFlag", 0);
                }
            } else {
                poolObjecName = "normal";
                if (!UserSyncData.getJSONObject("gacha").getJSONObject(poolObjecName).containsKey(poolId)) {
                    JSONObject PoolJson = new JSONObject(true);
                    PoolJson.put("cnt", 0);
                    PoolJson.put("maxCnt", 10);
                    PoolJson.put("rarity", 4);
                    PoolJson.put("avail", true);
                    UserSyncData.getJSONObject("gacha").getJSONObject(poolObjecName).put(poolId, PoolJson);
                }
                Pool = UserSyncData.getJSONObject("gacha").getJSONObject(poolObjecName).getJSONObject(poolId);
                cnt = Pool.getIntValue("cnt") + 1;
                UserSyncData.getJSONObject("gacha").getJSONObject(poolObjecName).getJSONObject(poolId).put("cnt", cnt);
                UserSyncData.getJSONObject("status").put("gachaCount", (UserSyncData.getJSONObject("status").getIntValue("gachaCount") + 1));
                if (cnt == 10 && Pool.getBoolean("avail").booleanValue()) {
                    UserSyncData.getJSONObject("gacha").getJSONObject(poolObjecName).getJSONObject(poolId).put("avail", false);
                    Minimum = true;
                }
            }
            JSONArray availCharInfo = poolJson.getJSONObject("detailInfo").getJSONObject("availCharInfo").getJSONArray("perAvailList");
            JSONArray upCharInfo = poolJson.getJSONObject("detailInfo").getJSONObject("upCharInfo").getJSONArray("perCharList");
            JSONArray randomRankArray = new JSONArray();
            for (int i = 0; i < availCharInfo.size(); ++i) {
                int totalPercent = (int)(availCharInfo.getJSONObject(i).getFloat("totalPercent").floatValue() * 200.0f);
                int rarityRank = availCharInfo.getJSONObject(i).getIntValue("rarityRank");
                if (rarityRank == 5) {
                    totalPercent += (UserSyncData.getJSONObject("status").getIntValue("gachaCount") + 50) / 50 * 2;
                }
                if (Minimum.booleanValue() && rarityRank < Pool.getIntValue("rarity")) continue;
                JSONObject randomRankObject = new JSONObject(true);
                randomRankObject.put("rarityRank", rarityRank);
                randomRankObject.put("index", i);
                IntStream.range((int)0, (int)totalPercent).forEach(n -> randomRankArray.add(randomRankObject));
            }
            Collections.shuffle((List)randomRankArray);
            JSONObject randomRank = randomRankArray.getJSONObject(new Random().nextInt(randomRankArray.size()));
            if (!JsonBody.getString("poolId").equals("BOOT_0_1_1") && randomRank.getIntValue("rarityRank") >= Pool.getIntValue("rarity")) {
                UserSyncData.getJSONObject("gacha").getJSONObject(poolObjecName).getJSONObject(poolId).put("avail", false);
            }
            if (randomRank.getIntValue("rarityRank") == 5) {
                UserSyncData.getJSONObject("status").put("gachaCount", 0);
            }
            JSONArray randomCharArray = availCharInfo.getJSONObject(randomRank.getIntValue("index")).getJSONArray("charIdList");
            for (int i = 0; i < upCharInfo.size(); ++i) {
                if (upCharInfo.getJSONObject(i).getIntValue("rarityRank") != randomRank.getIntValue("rarityRank")) continue;
                int percent = (int)(upCharInfo.getJSONObject(i).getFloat("percent").floatValue() * 100.0f) - 15;
                JSONArray upCharIdList = upCharInfo.getJSONObject(i).getJSONArray("charIdList");
                for (int n2 = 0; n2 < upCharIdList.size(); ++n2) {
                    String charId = upCharIdList.getString(n2);
                    IntStream.range((int)0, (int)percent).forEach(p -> randomCharArray.add(charId));
                }
            }
            Collections.shuffle((List)randomCharArray);
            String randomCharId = randomCharArray.getString(new Random().nextInt(randomCharArray.size()));
            int repeatCharId = 0;
            for (int i = 0; i < UserSyncData.getJSONObject("troop").getJSONObject("chars").size(); ++i) {
                if (!UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)(i + 1))).getString("charId").equals(randomCharId)) continue;
                repeatCharId = i + 1;
                break;
            }
            if (repeatCharId == 0) {
                get_char = new JSONObject(true);
                JSONObject char_data = new JSONObject(true);
                JSONArray skilsArray = ArknightsApplication.characterJson.getJSONObject(randomCharId).getJSONArray("skills");
                JSONArray skils = new JSONArray();
                for (int i = 0; i < skilsArray.size(); ++i) {
                    JSONObject new_skils = new JSONObject(true);
                    new_skils.put("skillId", skilsArray.getJSONObject(i).getString("skillId"));
                    new_skils.put("state", 0);
                    new_skils.put("specializeLevel", 0);
                    new_skils.put("completeUpgradeTime", -1);
                    if (skilsArray.getJSONObject(i).getJSONObject("unlockCond").getIntValue("phase") == 0) {
                        new_skils.put("unlock", 1);
                    } else {
                        new_skils.put("unlock", 0);
                    }
                    skils.add(new_skils);
                }
                int instId = UserSyncData.getJSONObject("troop").getJSONObject("chars").size() + 1;
                char_data.put("instId", instId);
                char_data.put("charId", randomCharId);
                char_data.put("favorPoint", 0);
                char_data.put("potentialRank", 0);
                char_data.put("mainSkillLvl", 1);
                char_data.put("skin", (randomCharId + "#1"));
                char_data.put("level", 1);
                char_data.put("exp", 0);
                char_data.put("evolvePhase", 0);
                char_data.put("gainTime", (new Date().getTime() / 1000L));
                char_data.put("skills", skils);
                char_data.put("voiceLan", ArknightsApplication.charwordTable.getJSONObject("charDefaultTypeDict").getString(randomCharId));
                if (skils == new JSONArray()) {
                    char_data.put("defaultSkillIndex", -1);
                } else {
                    char_data.put("defaultSkillIndex", 0);
                }
                String sub1 = randomCharId.substring(randomCharId.indexOf("_") + 1);
                String charName = sub1.substring(sub1.indexOf("_") + 1);
                if (ArknightsApplication.uniequipTable.containsKey(("uniequip_001_" + charName))) {
                    JSONObject equip = new JSONObject(true);
                    JSONObject uniequip_001 = new JSONObject(true);
                    uniequip_001.put("hide", 0);
                    uniequip_001.put("locked", 0);
                    uniequip_001.put("level", 1);
                    JSONObject uniequip_002 = new JSONObject(true);
                    uniequip_002.put("hide", 0);
                    uniequip_002.put("locked", 0);
                    uniequip_002.put("level", 1);
                    equip.put("uniequip_001_" + charName, uniequip_001);
                    equip.put("uniequip_002_" + charName, uniequip_002);
                    char_data.put("equip", equip);
                    char_data.put("currentEquip", ("uniequip_001_" + charName));
                } else {
                    char_data.put("currentEquip", null);
                }
                UserSyncData.getJSONObject("troop").getJSONObject("chars").put(String.valueOf((int)instId), char_data);
                JSONObject charGroup = new JSONObject(true);
                charGroup.put("favorPoint", 0);
                UserSyncData.getJSONObject("troop").getJSONObject("charGroup").put(randomCharId, charGroup);
                JSONObject buildingChar = new JSONObject(true);
                buildingChar.put("charId", randomCharId);
                buildingChar.put("lastApAddTime", (new Date().getTime() / 1000L));
                buildingChar.put("ap", 8640000);
                buildingChar.put("roomSlotId", "");
                buildingChar.put("index", -1);
                buildingChar.put("changeScale", 0);
                JSONObject bubble = new JSONObject(true);
                JSONObject normal = new JSONObject(true);
                normal.put("add", -1);
                normal.put("ts", 0);
                bubble.put("normal", normal);
                JSONObject assist = new JSONObject(true);
                assist.put("add", -1);
                assist.put("ts", -1);
                bubble.put("assist", assist);
                buildingChar.put("bubble", bubble);
                buildingChar.put("workTime", 0);
                UserSyncData.getJSONObject("building").getJSONObject("chars").put(String.valueOf((int)instId), buildingChar);
                get_char.put("charInstId", instId);
                get_char.put("charId", randomCharId);
                get_char.put("isNew", 1);
                JSONArray itemGet = new JSONArray();
                JSONObject new_itemGet_1 = new JSONObject(true);
                new_itemGet_1.put("type", "HGG_SHD");
                new_itemGet_1.put("id", "4004");
                new_itemGet_1.put("count", 1);
                itemGet.add(new_itemGet_1);
                UserSyncData.getJSONObject("status").put("hggShard", (UserSyncData.getJSONObject("status").getIntValue("hggShard") + 1));
                get_char.put("itemGet", itemGet);
                UserSyncData.getJSONObject("inventory").put("p_" + randomCharId, 0);
                gachaResultList.add(get_char);
                newChars.add(get_char);
                charGet = get_char;
                JSONObject charinstId = new JSONObject(true);
                charinstId.put(String.valueOf((int)instId), char_data);
                chars.put(String.valueOf((int)instId), char_data);
                troop.put("chars", charinstId);
                continue;
            }
            get_char = new JSONObject(true);
            get_char.put("charInstId", repeatCharId);
            get_char.put("charId", randomCharId);
            get_char.put("isNew", 0);
            JSONObject repatChar = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)repeatCharId));
            int potentialRank = repatChar.getIntValue("potentialRank");
            int rarity = randomRank.getIntValue("rarityRank");
            String itemName = null;
            String itemType = null;
            String itemId = null;
            int itemCount = 0;
            if (rarity == 0) {
                itemName = "lggShard";
                itemType = "LGG_SHD";
                itemId = "4005";
                itemCount = 1;
            }
            if (rarity == 1) {
                itemName = "lggShard";
                itemType = "LGG_SHD";
                itemId = "4005";
                itemCount = 1;
            }
            if (rarity == 2) {
                itemName = "lggShard";
                itemType = "LGG_SHD";
                itemId = "4005";
                itemCount = 5;
            }
            if (rarity == 3) {
                itemName = "lggShard";
                itemType = "LGG_SHD";
                itemId = "4005";
                itemCount = 30;
            }
            if (rarity == 4) {
                itemName = "hggShard";
                itemType = "HGG_SHD";
                itemId = "4004";
                itemCount = potentialRank != 5 ? 5 : 8;
            }
            if (rarity == 5) {
                itemName = "hggShard";
                itemType = "HGG_SHD";
                itemId = "4004";
                itemCount = potentialRank != 5 ? 10 : 15;
            }
            JSONArray itemGet = new JSONArray();
            JSONObject new_itemGet_1 = new JSONObject(true);
            new_itemGet_1.put("type", itemType);
            new_itemGet_1.put("id", itemId);
            new_itemGet_1.put("count", itemCount);
            itemGet.add(new_itemGet_1);
            UserSyncData.getJSONObject("status").put(itemName, (UserSyncData.getJSONObject("status").getIntValue(itemName) + count));
            JSONObject new_itemGet_3 = new JSONObject(true);
            new_itemGet_3.put("type", "MATERIAL");
            new_itemGet_3.put("id", ("p_" + randomCharId));
            new_itemGet_3.put("count", 1);
            itemGet.add(new_itemGet_3);
            get_char.put("itemGet", itemGet);
            UserSyncData.getJSONObject("inventory").put("p_" + randomCharId, (UserSyncData.getJSONObject("inventory").getIntValue("p_" + randomCharId) + 1));
            gachaResultList.add(get_char);
            charGet = get_char;
            JSONObject charinstId = new JSONObject(true);
            charinstId.put(String.valueOf((int)repeatCharId), UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)repeatCharId)));
            chars.put(String.valueOf((int)repeatCharId), UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)repeatCharId)));
            troop.put("chars", charinstId);
        }
        if (useTkt == 1 || useTkt == 2) {
            UserSyncData.getJSONObject("status").put(type, (UserSyncData.getJSONObject("status").getIntValue(type) - 1));
        } else {
            UserSyncData.getJSONObject("status").put("diamondShard", (UserSyncData.getJSONObject("status").getIntValue("diamondShard") - useDiamondShard));
        }
        UserSyncData.getJSONObject("troop").put("chars", chars);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        modified.put("troop", UserSyncData.getJSONObject("troop"));
        modified.put("consumable", UserSyncData.getJSONObject("consumable"));
        modified.put("status", UserSyncData.getJSONObject("status"));
        modified.put("inventory", UserSyncData.getJSONObject("inventory"));
        modified.put("gacha", UserSyncData.getJSONObject("gacha"));
        playerDataDelta.put("deleted", new JSONObject(true));
        playerDataDelta.put("modified", modified);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        result.put("result", 0);
        result.put("charGet", charGet);
        result.put("gachaResultList", gachaResultList);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }
}

