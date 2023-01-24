/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONArray
 *  com.alibaba.fastjson.JSONObject
 *  com.hypergryph.arknights.Admin
 *  com.hypergryph.arknights.ArknightsApplication
 *  com.hypergryph.arknights.core.dao.userDao
 *  com.hypergryph.arknights.core.pojo.Account
 *  java.lang.CharSequence
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Date
 *  java.util.List
 *  java.util.Map$Entry
 *  org.springframework.web.bind.annotation.RequestHeader
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestParam
 *  org.springframework.web.bind.annotation.RestController
 */
package com.hypergryph.arknights;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.pojo.Account;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * Exception performing whole class analysis ignored.
 */
@RestController
@RequestMapping(value={"/admin"})
public class Admin {
    public static void GM_GiveItem(JSONObject UserSyncData, String reward_id, String reward_type, int reward_count, JSONArray items) {
        JSONObject item;
        JSONObject chars = UserSyncData.getJSONObject("troop").getJSONObject("chars");
        JSONObject troop = new JSONObject(true);
        if (reward_type.equals("CHAR")) {
            JSONObject charGet;
            item = new JSONObject(true);
            String randomCharId = reward_id;
            int repeatCharId = 0;
            for (int j = 0; j < UserSyncData.getJSONObject("troop").getJSONObject("chars").size(); ++j) {
                if (!UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)(j + 1))).getString("charId").equals(randomCharId)) continue;
                repeatCharId = j + 1;
                break;
            }
            if (repeatCharId == 0) {
                JSONObject get_char = new JSONObject(true);
                JSONObject char_data = new JSONObject(true);
                JSONArray skilsArray = ArknightsApplication.characterJson.getJSONObject(randomCharId).getJSONArray("skills");
                JSONArray skils = new JSONArray();
                for (int m = 0; m < skilsArray.size(); ++m) {
                    JSONObject new_skils = new JSONObject(true);
                    new_skils.put("skillId", skilsArray.getJSONObject(m).getString("skillId"));
                    new_skils.put("state", 0);
                    new_skils.put("specializeLevel", 0);
                    new_skils.put("completeUpgradeTime", -1);
                    if (skilsArray.getJSONObject(m).getJSONObject("unlockCond").getIntValue("phase") == 0) {
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
                UserSyncData.getJSONObject("troop").put("curCharInstId", (instId + 1));
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
                charGet = get_char;
                JSONObject charinstId = new JSONObject(true);
                charinstId.put(String.valueOf((int)instId), char_data);
                chars.put(String.valueOf((int)instId), char_data);
                troop.put("chars", charinstId);
                item.put("id", randomCharId);
                item.put("type", reward_type);
                item.put("charGet", charGet);
                items.add(item);
            } else {
                JSONObject get_char = new JSONObject(true);
                get_char.put("charInstId", repeatCharId);
                get_char.put("charId", randomCharId);
                get_char.put("isNew", 0);
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
                JSONArray itemGet = new JSONArray();
                JSONObject new_itemGet_1 = new JSONObject(true);
                new_itemGet_1.put("type", itemType);
                new_itemGet_1.put("id", itemId);
                new_itemGet_1.put("count", itemCount);
                itemGet.add(new_itemGet_1);
                UserSyncData.getJSONObject("status").put(itemName, (UserSyncData.getJSONObject("status").getIntValue(itemName) + itemCount));
                JSONObject new_itemGet_3 = new JSONObject(true);
                new_itemGet_3.put("type", "MATERIAL");
                new_itemGet_3.put("id", ("p_" + randomCharId));
                new_itemGet_3.put("count", 1);
                itemGet.add(new_itemGet_3);
                get_char.put("itemGet", itemGet);
                UserSyncData.getJSONObject("inventory").put("p_" + randomCharId, (UserSyncData.getJSONObject("inventory").getIntValue("p_" + randomCharId) + 1));
                charGet = get_char;
                JSONObject charinstId = new JSONObject(true);
                charinstId.put(String.valueOf((int)repeatCharId), UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)repeatCharId)));
                chars.put(String.valueOf((int)repeatCharId), UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)repeatCharId)));
                troop.put("chars", charinstId);
                item.put("type", reward_type);
                item.put("id", randomCharId);
                item.put("charGet", charGet);
                items.add(item);
            }
        }
        if (reward_type.equals("HGG_SHD")) {
            UserSyncData.getJSONObject("status").put("practiceTicket", (UserSyncData.getJSONObject("status").getIntValue("hggShard") + reward_count));
        }
        if (reward_type.equals("LGG_SHD")) {
            UserSyncData.getJSONObject("status").put("practiceTicket", (UserSyncData.getJSONObject("status").getIntValue("lggShard") + reward_count));
        }
        if (reward_type.equals("MATERIAL")) {
            UserSyncData.getJSONObject("inventory").put(reward_id, (UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
        }
        if (reward_type.equals("CARD_EXP")) {
            UserSyncData.getJSONObject("inventory").put(reward_id, (UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
        }
        if (reward_type.equals("SOCIAL_PT")) {
            UserSyncData.getJSONObject("status").put("socialPoint", (UserSyncData.getJSONObject("status").getIntValue("socialPoint") + reward_count));
        }
        if (reward_type.equals("AP_GAMEPLAY")) {
            UserSyncData.getJSONObject("status").put("ap", (UserSyncData.getJSONObject("status").getIntValue("ap") + reward_count));
        }
        if (reward_type.equals("AP_ITEM")) {
            if (reward_id.contains((CharSequence)"60")) {
                UserSyncData.getJSONObject("status").put("ap", (UserSyncData.getJSONObject("status").getIntValue("ap") + 60));
            } else if (reward_id.contains((CharSequence)"200")) {
                UserSyncData.getJSONObject("status").put("ap", (UserSyncData.getJSONObject("status").getIntValue("ap") + 200));
            } else {
                UserSyncData.getJSONObject("status").put("ap", (UserSyncData.getJSONObject("status").getIntValue("ap") + 100));
            }
        }
        if (reward_type.equals("TKT_TRY")) {
            UserSyncData.getJSONObject("status").put("practiceTicket", (UserSyncData.getJSONObject("status").getIntValue("practiceTicket") + reward_count));
        }
        if (reward_type.equals("DIAMOND")) {
            UserSyncData.getJSONObject("status").put("androidDiamond", (UserSyncData.getJSONObject("status").getIntValue("androidDiamond") + reward_count));
            UserSyncData.getJSONObject("status").put("iosDiamond", (UserSyncData.getJSONObject("status").getIntValue("iosDiamond") + reward_count));
        }
        if (reward_type.equals("DIAMOND_SHD")) {
            UserSyncData.getJSONObject("status").put("diamondShard", (UserSyncData.getJSONObject("status").getIntValue("diamondShard") + reward_count));
        }
        if (reward_type.equals("GOLD")) {
            UserSyncData.getJSONObject("status").put("gold", (UserSyncData.getJSONObject("status").getIntValue("gold") + reward_count));
        }
        if (reward_type.equals("TKT_RECRUIT")) {
            UserSyncData.getJSONObject("status").put("recruitLicense", (UserSyncData.getJSONObject("status").getIntValue("recruitLicense") + reward_count));
        }
        if (reward_type.equals("TKT_INST_FIN")) {
            UserSyncData.getJSONObject("status").put("instantFinishTicket", (UserSyncData.getJSONObject("status").getIntValue("instantFinishTicket") + reward_count));
        }
        if (reward_type.equals("TKT_GACHA_PRSV")) {
            UserSyncData.getJSONObject("inventory").put(reward_id, (UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
        }
        if (reward_type.equals("RENAMING_CARD")) {
            UserSyncData.getJSONObject("inventory").put(reward_id, (UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
        }
        if (reward_type.equals("RETRO_COIN")) {
            UserSyncData.getJSONObject("inventory").put(reward_id, (UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
        }
        if (reward_type.equals("AP_SUPPLY")) {
            UserSyncData.getJSONObject("inventory").put(reward_id, (UserSyncData.getJSONObject("inventory").getIntValue(reward_id) + reward_count));
        }
        if (reward_type.equals("TKT_GACHA_10")) {
            UserSyncData.getJSONObject("status").put("tenGachaTicket", (UserSyncData.getJSONObject("status").getIntValue("tenGachaTicket") + reward_count));
        }
        if (reward_type.equals("TKT_GACHA")) {
            UserSyncData.getJSONObject("status").put("gachaTicket", (UserSyncData.getJSONObject("status").getIntValue("gachaTicket") + reward_count));
        }
        if (reward_type.indexOf("VOUCHER") != -1) {
            if (!UserSyncData.getJSONObject("consumable").containsKey(reward_id)) {
                JSONObject consumables = new JSONObject(true);
                JSONObject consumable = new JSONObject(true);
                consumable.put("ts", -1);
                consumable.put("count", 0);
                consumables.put("0", consumable);
                UserSyncData.getJSONObject("consumable").put(reward_id, consumables);
            }
            UserSyncData.getJSONObject("consumable").getJSONObject(reward_id).getJSONObject("0").put("count", (UserSyncData.getJSONObject("consumable").getJSONObject(reward_id).getJSONObject("0").getIntValue("count") + reward_count));
        }
        if (reward_type.equals("CHAR_SKIN")) {
            UserSyncData.getJSONObject("skin").getJSONObject("characterSkins").put(reward_id, 1);
            UserSyncData.getJSONObject("skin").getJSONObject("skinTs").put(reward_id, (new Date().getTime() / 1000L));
        }
        if (!reward_type.equals("CHAR")) {
            item = new JSONObject(true);
            item.put("id", reward_id);
            item.put("type", reward_type);
            item.put("count", reward_count);
            items.add(item);
        }
    }

    @RequestMapping(value={"/send/character"})
    public JSONObject character(@RequestHeader(value="GMKey") String GMKey, @RequestParam Long uid, @RequestParam String charId) {
        if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
            JSONObject result = new JSONObject(true);
            result.put("code", 401);
            result.put("msg", "Unauthorized");
            return result;
        }
        List user2 = userDao.queryAccountByUid((long)uid);
        if (user2.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("code", 404);
            result.put("msg", "无法找到该玩家的存档");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)user2.get(0)).getUser());
        JSONArray items = new JSONArray();
        Admin.GM_GiveItem((JSONObject)UserSyncData, (String)charId, (String)"CHAR", (int)1, (JSONArray)items);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        result.put("code", 200);
        result.put("items", items);
        return result;
    }

    @RequestMapping(value={"/send/item"})
    public JSONObject item(@RequestHeader(value="GMKey") String GMKey, @RequestParam Long uid, @RequestParam String itemType, @RequestParam String itemId, @RequestParam int count) {
        if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
            JSONObject result = new JSONObject(true);
            result.put("code", 401);
            result.put("msg", "Unauthorized");
            return result;
        }
        List user2 = userDao.queryAccountByUid((long)uid);
        if (user2.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("code", 404);
            result.put("msg", "无法找到该玩家的存档");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)user2.get(0)).getUser());
        JSONArray items = new JSONArray();
        Admin.GM_GiveItem((JSONObject)UserSyncData, (String)itemId, (String)itemType, (int)count, (JSONArray)items);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        result.put("code", 200);
        result.put("items", items);
        return result;
    }

    @RequestMapping(value={"/charBuild/changeLevel"})
    public JSONObject level(@RequestHeader(value="GMKey") String GMKey, @RequestParam Long uid, @RequestParam String charId, @RequestParam int value) {
        if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
            JSONObject result = new JSONObject(true);
            result.put("code", 401);
            result.put("msg", "Unauthorized");
            return result;
        }
        List Accounts = userDao.queryAccountByUid((long)uid);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("code", 404);
            result.put("msg", "无法找到该玩家的存档");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
            JSONObject charData = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(entry.getKey().toString());
            if (!charData.getString("charId").equals(charId)) continue;
            charData.put("level", value);
            UserSyncData.getJSONObject("troop").getJSONObject("chars").put(entry.getKey().toString(), charData);
            userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
            JSONObject result = new JSONObject(true);
            result.put("code", 200);
            result.put("msg", ("已改变 " + charId + " 的等级为" + value));
            return result;
        }
        JSONObject result = new JSONObject(true);
        result.put("code", 404);
        result.put("msg", ("该玩家尚未拥有 " + charId));
        return result;
    }

    @RequestMapping(value={"/charBuild/changeFavorPoint"})
    public JSONObject favorPoint(@RequestHeader(value="GMKey") String GMKey, @RequestParam Long uid, @RequestParam String charId, @RequestParam int value) {
        if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
            JSONObject result = new JSONObject(true);
            result.put("code", 401);
            result.put("msg", "Unauthorized");
            return result;
        }
        List Accounts = userDao.queryAccountByUid((long)uid);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("code", 404);
            result.put("msg", "无法找到该玩家的存档");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
            JSONObject charData = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(entry.getKey().toString());
            if (!charData.getString("charId").equals(charId)) continue;
            charData.put("favorPoint", value);
            UserSyncData.getJSONObject("troop").getJSONObject("chars").put(entry.getKey().toString(), charData);
            userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
            JSONObject result = new JSONObject(true);
            result.put("code", 200);
            result.put("msg", ("已改变 " + charId + " 的信赖为" + value));
            return result;
        }
        JSONObject result = new JSONObject(true);
        result.put("code", 404);
        result.put("msg", ("该玩家尚未拥有 " + charId));
        return result;
    }

    @RequestMapping(value={"/charBuild/changePotentialRank"})
    public JSONObject PotentialRank(@RequestHeader(value="GMKey") String GMKey, @RequestParam Long uid, @RequestParam String charId, @RequestParam int value) {
        if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
            JSONObject result = new JSONObject(true);
            result.put("code", 401);
            result.put("msg", "Unauthorized");
            return result;
        }
        List Accounts = userDao.queryAccountByUid((long)uid);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("code", 404);
            result.put("msg", "无法找到该玩家的存档");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
            JSONObject charData = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(entry.getKey().toString());
            if (!charData.getString("charId").equals(charId)) continue;
            charData.put("potentialRank", value);
            UserSyncData.getJSONObject("troop").getJSONObject("chars").put(entry.getKey().toString(), charData);
            userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
            JSONObject result = new JSONObject(true);
            result.put("code", 200);
            result.put("msg", ("已改变 " + charId + " 的潜能为 " + value));
            return result;
        }
        JSONObject result = new JSONObject(true);
        result.put("code", 404);
        result.put("msg", ("该玩家尚未拥有 " + charId));
        return result;
    }

    @RequestMapping(value={"/charBuild/changeMainSkillLvl"})
    public JSONObject mainSkillLvl(@RequestHeader(value="GMKey") String GMKey, @RequestParam Long uid, @RequestParam String charId, @RequestParam int value) {
        if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
            JSONObject result = new JSONObject(true);
            result.put("code", 401);
            result.put("msg", "Unauthorized");
            return result;
        }
        List Accounts = userDao.queryAccountByUid((long)uid);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("code", 404);
            result.put("msg", "无法找到该玩家的存档");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
            JSONObject charData = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(entry.getKey().toString());
            if (!charData.getString("charId").equals(charId)) continue;
            charData.put("mainSkillLvl", value);
            UserSyncData.getJSONObject("troop").getJSONObject("chars").put(entry.getKey().toString(), charData);
            userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
            JSONObject result = new JSONObject(true);
            result.put("code", 200);
            result.put("msg", ("已改变 " + charId + " 的技能等级为 " + value));
            return result;
        }
        JSONObject result = new JSONObject(true);
        result.put("code", 404);
        result.put("msg", ("该玩家尚未拥有 " + charId));
        return result;
    }

    @RequestMapping(value={"/charBuild/changeExp"})
    public JSONObject Exp(@RequestHeader(value="GMKey") String GMKey, @RequestParam Long uid, @RequestParam String charId, @RequestParam int value) {
        if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
            JSONObject result = new JSONObject(true);
            result.put("code", 401);
            result.put("msg", "Unauthorized");
            return result;
        }
        List Accounts = userDao.queryAccountByUid((long)uid);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("code", 404);
            result.put("msg", "无法找到该玩家的存档");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
            JSONObject charData = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(entry.getKey().toString());
            if (!charData.getString("charId").equals(charId)) continue;
            charData.put("exp", value);
            UserSyncData.getJSONObject("troop").getJSONObject("chars").put(entry.getKey().toString(), charData);
            userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
            JSONObject result = new JSONObject(true);
            result.put("code", 200);
            result.put("msg", ("已改变 " + charId + " 的经验为 " + value));
            return result;
        }
        JSONObject result = new JSONObject(true);
        result.put("code", 404);
        result.put("msg", ("该玩家尚未拥有 " + charId));
        return result;
    }

    @RequestMapping(value={"/charBuild/changeEvolvePhase"})
    public JSONObject evolvePhase(@RequestHeader(value="GMKey") String GMKey, @RequestParam Long uid, @RequestParam String charId, @RequestParam int value) {
        if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
            JSONObject result = new JSONObject(true);
            result.put("code", 401);
            result.put("msg", "Unauthorized");
            return result;
        }
        List Accounts = userDao.queryAccountByUid((long)uid);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("code", 404);
            result.put("msg", "无法找到该玩家的存档");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
            JSONObject charData = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(entry.getKey().toString());
            if (!charData.getString("charId").equals(charId)) continue;
            charData.put("evolvePhase", value);
            UserSyncData.getJSONObject("troop").getJSONObject("chars").put(entry.getKey().toString(), charData);
            userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
            JSONObject result = new JSONObject(true);
            result.put("code", 200);
            result.put("msg", ("已改变 " + charId + " 的精英化等级为 " + value));
            return result;
        }
        JSONObject result = new JSONObject(true);
        result.put("code", 404);
        result.put("msg", ("该玩家尚未拥有 " + charId));
        return result;
    }

    @RequestMapping(value={"/charBuild/changeDefaultSkillIndex"})
    public JSONObject defaultSkillIndex(@RequestHeader(value="GMKey") String GMKey, @RequestParam Long uid, @RequestParam String charId, @RequestParam int value) {
        if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
            JSONObject result = new JSONObject(true);
            result.put("code", 401);
            result.put("msg", "Unauthorized");
            return result;
        }
        List Accounts = userDao.queryAccountByUid((long)uid);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("code", 404);
            result.put("msg", "无法找到该玩家的存档");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
            JSONObject charData = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(entry.getKey().toString());
            if (!charData.getString("charId").equals(charId)) continue;
            charData.put("defaultSkillIndex", value);
            UserSyncData.getJSONObject("troop").getJSONObject("chars").put(entry.getKey().toString(), charData);
            userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
            JSONObject result = new JSONObject(true);
            result.put("code", 200);
            result.put("msg", ("已改变 " + charId + " 的默认技能为 " + value));
            return result;
        }
        JSONObject result = new JSONObject(true);
        result.put("code", 404);
        result.put("msg", ("该玩家尚未拥有 " + charId));
        return result;
    }

    @RequestMapping(value={"/charBuild/changeSkin"})
    public JSONObject skin(@RequestHeader(value="GMKey") String GMKey, @RequestParam Long uid, @RequestParam String charId, @RequestParam String value) {
        if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
            JSONObject result = new JSONObject(true);
            result.put("code", 401);
            result.put("msg", "Unauthorized");
            return result;
        }
        List Accounts = userDao.queryAccountByUid((long)uid);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("code", 404);
            result.put("msg", "无法找到该玩家的存档");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
            JSONObject charData = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(entry.getKey().toString());
            if (!charData.getString("charId").equals(charId)) continue;
            charData.put("skin", value);
            UserSyncData.getJSONObject("troop").getJSONObject("chars").put(entry.getKey().toString(), charData);
            userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
            JSONObject result = new JSONObject(true);
            result.put("code", 200);
            result.put("msg", ("已改变 " + charId + " 的默认皮肤为 " + value));
            return result;
        }
        JSONObject result = new JSONObject(true);
        result.put("code", 404);
        result.put("msg", ("该玩家尚未拥有 " + charId));
        return result;
    }

    @RequestMapping(value={"/charBuild/unlockAllSkills"})
    public JSONObject unlockAllSkills(@RequestHeader(value="GMKey") String GMKey, @RequestParam Long uid, @RequestParam String charId) {
        if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
            JSONObject result = new JSONObject(true);
            result.put("code", 401);
            result.put("msg", "Unauthorized");
            return result;
        }
        List Accounts = userDao.queryAccountByUid((long)uid);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("code", 404);
            result.put("msg", "无法找到该玩家的存档");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
            JSONObject charData = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(entry.getKey().toString());
            if (!charData.getString("charId").equals(charId)) continue;
            for (int i = 0; i < charData.getJSONArray("skills").size(); ++i) {
                charData.getJSONArray("skills").getJSONObject(i).put("unlock", 1);
            }
            UserSyncData.getJSONObject("troop").getJSONObject("chars").put(entry.getKey().toString(), charData);
            userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
            JSONObject result = new JSONObject(true);
            result.put("code", 200);
            result.put("msg", ("已解锁 " + charId + " 的所有技能"));
            return result;
        }
        JSONObject result = new JSONObject(true);
        result.put("code", 404);
        result.put("msg", ("该玩家尚未拥有 " + charId));
        return result;
    }

    @RequestMapping(value={"/charBuild/changeSpecializeLevel"})
    public JSONObject UpSpecializeLevel(@RequestHeader(value="GMKey") String GMKey, @RequestParam Long uid, @RequestParam String charId, @RequestParam int value) {
        if (!ArknightsApplication.serverConfig.getJSONObject("server").getString("GMKey").equals(GMKey)) {
            JSONObject result = new JSONObject(true);
            result.put("code", 401);
            result.put("msg", "Unauthorized");
            return result;
        }
        List Accounts = userDao.queryAccountByUid((long)uid);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("code", 404);
            result.put("msg", "无法找到该玩家的存档");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        for (Map.Entry entry : UserSyncData.getJSONObject("troop").getJSONObject("chars").entrySet()) {
            JSONObject charData = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(entry.getKey().toString());
            if (!charData.getString("charId").equals(charId)) continue;
            for (int i = 0; i < charData.getJSONArray("skills").size(); ++i) {
                charData.getJSONArray("skills").getJSONObject(i).put("specializeLevel", value);
            }
            UserSyncData.getJSONObject("troop").getJSONObject("chars").put(entry.getKey().toString(), charData);
            userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
            JSONObject result = new JSONObject(true);
            result.put("code", 200);
            result.put("msg", ("已把 " + charId + " 的所有技能专精提升至 " + value));
            return result;
        }
        JSONObject result = new JSONObject(true);
        result.put("code", 404);
        result.put("msg", ("该玩家尚未拥有 " + charId));
        return result;
    }
}

