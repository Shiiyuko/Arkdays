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
 *  com.hypergryph.arknights.game.shop
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Date
 *  java.util.List
 *  java.util.Map$Entry
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
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.pojo.Account;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/shop"})
public class shop {
    @PostMapping(value={"/getSkinGoodList"}, produces={"application/json;charset=UTF-8"})
    public JSONObject getSkinGoodList(@RequestBody JSONObject JsonBody) {
        JSONArray charIdList = JsonBody.getJSONArray("charIdList");
        JSONArray goodList = new JSONArray();
        if (charIdList.size() == 0) {
            return ArknightsApplication.skinGoodList;
        }
        for (Map.Entry entry : ArknightsApplication.skinTable.entrySet()) {
            String skinId = (String)entry.getKey();
            if (skinId.indexOf(charIdList.getString(0)) == -1 || skinId.indexOf("@") == -1) continue;
            JSONObject SkinData = ArknightsApplication.skinTable.getJSONObject(skinId);
            JSONObject SkinGood = new JSONObject(true);
            SkinGood.put("charId", SkinData.getString("charId"));
            SkinGood.put("skinId", SkinData.getString("skinId"));
            SkinGood.put("goodId", ("SS_" + SkinData.getString("skinId")));
            SkinGood.put("slotId", SkinData.getJSONObject("displaySkin").getIntValue("sortId"));
            SkinGood.put("skinName", SkinData.getJSONObject("displaySkin").getString("skinName"));
            SkinGood.put("discount", 0);
            SkinGood.put("originPrice", 18);
            SkinGood.put("price", 18);
            SkinGood.put("startDateTime", -1);
            SkinGood.put("endDateTime", -1);
            SkinGood.put("desc1", null);
            SkinGood.put("desc2", null);
            SkinGood.put("currencyUnit", "DIAMOND");
            goodList.add(SkinGood);
        }
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("modified", new JSONObject(true));
        playerDataDelta.put("deleted", new JSONObject(true));
        result.put("playerDataDelta", playerDataDelta);
        result.put("goodList", goodList);
        return result;
    }

    @PostMapping(value={"/buySkinGood"}, produces={"application/json;charset=UTF-8"})
    public JSONObject buySkinGood(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /shop/buySkinGood");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        String goodId = JsonBody.getString("goodId");
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
        UserSyncData.getJSONObject("skin").getJSONObject("characterSkins").put(goodId.substring(3), 1);
        UserSyncData.getJSONObject("skin").getJSONObject("skinTs").put(goodId.substring(3), (new Date().getTime() / 1000L));
        UserSyncData.getJSONObject("status").put("androidDiamond", (UserSyncData.getJSONObject("status").getIntValue("androidDiamond") - 18));
        UserSyncData.getJSONObject("status").put("iosDiamond", (UserSyncData.getJSONObject("status").getIntValue("iosDiamond") - 18));
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        JSONObject status = new JSONObject(true);
        status.put("androidDiamond", UserSyncData.getJSONObject("status").getIntValue("androidDiamond"));
        status.put("iosDiamond", UserSyncData.getJSONObject("status").getIntValue("iosDiamond"));
        modified.put("skin", UserSyncData.getJSONObject("skin"));
        modified.put("status", status);
        playerDataDelta.put("deleted", new JSONObject(true));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("result", 0);
        return result;
    }

    @PostMapping(value={"/buyLowGood"}, produces={"application/json;charset=UTF-8"})
    public JSONObject buyLowGood(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /shop/buyLowGood");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        String goodId = JsonBody.getString("goodId");
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
        JSONArray items = new JSONArray();
        for (int i = 0; i < ArknightsApplication.LowGoodList.getJSONArray("goodList").size(); ++i) {
            JSONObject lowGood = ArknightsApplication.LowGoodList.getJSONArray("goodList").getJSONObject(i);
            if (!lowGood.getString("goodId").equals(goodId)) continue;
            UserSyncData.getJSONObject("status").put("lggShard", (UserSyncData.getJSONObject("status").getIntValue("lggShard") - lowGood.getIntValue("price") * count));
            String reward_id = lowGood.getJSONObject("item").getString("id");
            String reward_type = lowGood.getJSONObject("item").getString("type");
            int reward_count = lowGood.getJSONObject("item").getIntValue("count") * count;
            Admin.GM_GiveItem((JSONObject)UserSyncData, (String)reward_id, (String)reward_type, (int)reward_count, (JSONArray)items);
            break;
        }
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        modified.put("skin", UserSyncData.getJSONObject("skin"));
        modified.put("status", UserSyncData.getJSONObject("status"));
        modified.put("shop", UserSyncData.getJSONObject("shop"));
        modified.put("troop", UserSyncData.getJSONObject("troop"));
        modified.put("skin", UserSyncData.getJSONObject("skin"));
        modified.put("inventory", UserSyncData.getJSONObject("inventory"));
        playerDataDelta.put("deleted", new JSONObject(true));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("items", items);
        result.put("result", 0);
        return result;
    }

    @PostMapping(value={"/buyHighGood"}, produces={"application/json;charset=UTF-8"})
    public JSONObject buyHighGood(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /shop/buyHighGood");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        String goodId = JsonBody.getString("goodId");
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
        JSONArray items = new JSONArray();
        for (int i = 0; i < ArknightsApplication.HighGoodList.getJSONArray("goodList").size(); ++i) {
            JSONObject HighGood = ArknightsApplication.HighGoodList.getJSONArray("goodList").getJSONObject(i);
            if (!HighGood.getString("goodId").equals(goodId)) continue;
            UserSyncData.getJSONObject("status").put("hggShard", (UserSyncData.getJSONObject("status").getIntValue("hggShard") - HighGood.getIntValue("price") * count));
            String reward_id = HighGood.getJSONObject("item").getString("id");
            String reward_type = HighGood.getJSONObject("item").getString("type");
            int reward_count = HighGood.getJSONObject("item").getIntValue("count") * count;
            Admin.GM_GiveItem((JSONObject)UserSyncData, (String)reward_id, (String)reward_type, (int)reward_count, (JSONArray)items);
            break;
        }
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        modified.put("skin", UserSyncData.getJSONObject("skin"));
        modified.put("status", UserSyncData.getJSONObject("status"));
        modified.put("shop", UserSyncData.getJSONObject("shop"));
        modified.put("troop", UserSyncData.getJSONObject("troop"));
        modified.put("skin", UserSyncData.getJSONObject("skin"));
        modified.put("inventory", UserSyncData.getJSONObject("inventory"));
        playerDataDelta.put("deleted", new JSONObject(true));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("items", items);
        result.put("result", 0);
        return result;
    }

    @PostMapping(value={"/buyExtraGood"}, produces={"application/json;charset=UTF-8"})
    public JSONObject buyExtraGood(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /shop/buyExtraGood");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        String goodId = JsonBody.getString("goodId");
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
        JSONArray items = new JSONArray();
        for (int i = 0; i < ArknightsApplication.ExtraGoodList.getJSONArray("goodList").size(); ++i) {
            JSONObject ExtraGood = ArknightsApplication.ExtraGoodList.getJSONArray("goodList").getJSONObject(i);
            if (!ExtraGood.getString("goodId").equals(goodId)) continue;
            UserSyncData.getJSONObject("inventory").put("4006", (UserSyncData.getJSONObject("inventory").getIntValue("4006") - ExtraGood.getIntValue("price") * count));
            String reward_id = ExtraGood.getJSONObject("item").getString("id");
            String reward_type = ExtraGood.getJSONObject("item").getString("type");
            int reward_count = ExtraGood.getJSONObject("item").getIntValue("count") * count;
            Admin.GM_GiveItem((JSONObject)UserSyncData, (String)reward_id, (String)reward_type, (int)reward_count, (JSONArray)items);
            break;
        }
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        modified.put("skin", UserSyncData.getJSONObject("skin"));
        modified.put("status", UserSyncData.getJSONObject("status"));
        modified.put("shop", UserSyncData.getJSONObject("shop"));
        modified.put("troop", UserSyncData.getJSONObject("troop"));
        modified.put("skin", UserSyncData.getJSONObject("skin"));
        modified.put("inventory", UserSyncData.getJSONObject("inventory"));
        playerDataDelta.put("deleted", new JSONObject(true));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("items", items);
        result.put("result", 0);
        return result;
    }

    @PostMapping(value={"/decomposePotentialItem"}, produces={"application/json;charset=UTF-8"})
    public JSONObject decomposePotentialItem(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /shop/decomposePotentialItem");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        JSONArray charInstIdList = JsonBody.getJSONArray("charInstIdList");
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
        JSONArray itemGet = new JSONArray();
        for (int i = 0; i < charInstIdList.size(); ++i) {
            int lggShard = UserSyncData.getJSONObject("status").getIntValue("lggShard");
            int hggShard = UserSyncData.getJSONObject("status").getIntValue("hggShard");
            JSONObject chars = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf(charInstIdList.get(i)));
            String CharId = chars.getString("charId");
            int pcount = UserSyncData.getJSONObject("inventory").getIntValue("p_" + CharId);
            UserSyncData.getJSONObject("inventory").put("p_" + CharId, 0);
            int rarity = ArknightsApplication.characterJson.getJSONObject(CharId).getIntValue("rarity");
            JSONObject item = new JSONObject(true);
            if (rarity == 0) {
                item.put("type", "LGG_SHD");
                item.put("id", "4005");
                item.put("count", (pcount * 1));
                itemGet.add(item);
                UserSyncData.getJSONObject("status").put("lggShard", (lggShard + pcount * 1));
                continue;
            }
            if (rarity == 1) {
                item.put("type", "LGG_SHD");
                item.put("id", "4005");
                item.put("count", (pcount * 1));
                itemGet.add(item);
                UserSyncData.getJSONObject("status").put("lggShard", (lggShard + pcount * 1));
                continue;
            }
            if (rarity == 2) {
                item.put("type", "LGG_SHD");
                item.put("id", "4005");
                item.put("count", (pcount * 5));
                itemGet.add(item);
                UserSyncData.getJSONObject("status").put("lggShard", (lggShard + pcount * 5));
                continue;
            }
            if (rarity == 3) {
                item.put("type", "HGG_SHD");
                item.put("id", "4004");
                item.put("count", (pcount * 1));
                itemGet.add(item);
                UserSyncData.getJSONObject("status").put("hggShard", (hggShard + pcount * 1));
                continue;
            }
            if (rarity == 4) {
                item.put("type", "HGG_SHD");
                item.put("id", "4004");
                item.put("count", (pcount * 5));
                itemGet.add(item);
                UserSyncData.getJSONObject("status").put("hggShard", (hggShard + pcount * 5));
                continue;
            }
            if (rarity != 5) continue;
            item.put("type", "HGG_SHD");
            item.put("id", "4004");
            item.put("count", (pcount * 10));
            itemGet.add(item);
            UserSyncData.getJSONObject("status").put("hggShard", (hggShard + pcount * 10));
        }
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        JSONObject status = new JSONObject(true);
        status.put("lggShard", UserSyncData.getJSONObject("status").getIntValue("lggShard"));
        status.put("hggShard", UserSyncData.getJSONObject("status").getIntValue("hggShard"));
        modified.put("status", status);
        modified.put("inventory", UserSyncData.getJSONObject("inventory"));
        playerDataDelta.put("modified", modified);
        playerDataDelta.put("deleted", new JSONObject(true));
        result.put("items", itemGet);
        result.put("playerDataDelta", playerDataDelta);
        result.put("result", 0);
        return result;
    }

    @RequestMapping(value={"/getGoodPurchaseState"})
    public JSONObject getGoodPurchaseState() {
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("modified", new JSONObject(true));
        playerDataDelta.put("deleted", new JSONObject(true));
        result.put("playerDataDelta", playerDataDelta);
        result.put("result", new JSONObject(true));
        return result;
    }

    @RequestMapping(value={"/getCashGoodList"})
    public JSONObject getCashGoodList() {
        return ArknightsApplication.CashGoodList;
    }

    @RequestMapping(value={"/getGPGoodList"})
    public JSONObject getGPGoodList() {
        return ArknightsApplication.GPGoodList;
    }

    @RequestMapping(value={"/getLowGoodList"})
    public JSONObject getLowGoodList() {
        return ArknightsApplication.LowGoodList;
    }

    @RequestMapping(value={"/getHighGoodList"})
    public JSONObject getHighGoodList() {
        return ArknightsApplication.HighGoodList;
    }

    @RequestMapping(value={"/getExtraGoodList"})
    public JSONObject getExtraGoodList() {
        return ArknightsApplication.ExtraGoodList;
    }

    @RequestMapping(value={"/getLMTGSGoodList"})
    public JSONObject getLMTGSGoodList() {
        return ArknightsApplication.LMTGSGoodList;
    }

    @RequestMapping(value={"/getEPGSGoodList"})
    public JSONObject getEPGSGoodList() {
        return ArknightsApplication.EPGSGoodList;
    }

    @RequestMapping(value={"/getRepGoodList"})
    public JSONObject getRepGoodList() {
        return ArknightsApplication.RepGoodList;
    }

    @RequestMapping(value={"/getFurniGoodList"})
    public JSONObject getFurniGoodList() {
        return ArknightsApplication.FurniGoodList;
    }

    @RequestMapping(value={"/getSocialGoodList"})
    public JSONObject getSocialGoodList() {
        return ArknightsApplication.SocialGoodList;
    }
}

