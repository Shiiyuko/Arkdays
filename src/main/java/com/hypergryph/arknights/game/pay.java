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
 *  com.hypergryph.arknights.game.pay
 *  java.lang.Boolean
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
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
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/pay"})
public class pay {
    @PostMapping(value={"/getUnconfirmedOrderIdList"}, produces={"application/json;charset=UTF-8"})
    public JSONObject getUnconfirmedOrderIdList() {
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("modified", new JSONObject(true));
        playerDataDelta.put("deleted", new JSONObject(true));
        result.put("playerDataDelta", playerDataDelta);
        result.put("orderIdList", new JSONArray());
        return result;
    }

    @PostMapping(value={"/createOrder"}, produces={"application/json;charset=UTF-8"})
    public JSONObject createOrder(@RequestBody JSONObject json) {
        if (!ArknightsApplication.serverConfig.getJSONObject("shop").getBooleanValue("enableDiamondShop")) {
            JSONObject result = new JSONObject(true);
            result.put("result", 114514);
            return result;
        }
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("modified", new JSONObject(true));
        playerDataDelta.put("deleted", new JSONObject(true));
        result.put("playerDataDelta", playerDataDelta);
        result.put("result", 0);
        result.put("orderId", json.getString("goodId"));
        result.put("orderIdList", new JSONArray());
        return result;
    }

    @PostMapping(value={"/createOrderAlipay2"}, produces={"application/json;charset=UTF-8"})
    public JSONObject createOrderAlipay(@RequestBody JSONObject json, HttpServletResponse response) {
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("modified", new JSONObject(true));
        playerDataDelta.put("deleted", new JSONObject(true));
        result.put("playerDataDelta", playerDataDelta);
        result.put("result", 0);
        result.put("orderId", json.getString("goodId"));
        result.put("price", 600);
        result.put("qs", "");
        result.put("pagePay", true);
        result.put("returnUrl", "");
        return result;
    }

    @PostMapping(value={"/confirmOrderAlipay"}, produces={"application/json;charset=UTF-8"})
    public JSONObject confirmOrderAlipay() {
        JSONObject result = new JSONObject(true);
        result.put("status", 0);
        return result;
    }

    @PostMapping(value={"/confirmOrder"}, produces={"application/json;charset=UTF-8"})
    public JSONObject confirmOrder(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response) {
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        String goodId = JsonBody.getString("orderId");
        JSONArray items = new JSONArray();
        JSONObject receiveItems = new JSONObject(true);
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
        if (goodId.indexOf("CS") != -1) {
            JSONObject CashGood = new JSONObject();
            for (int i = 0; i < ArknightsApplication.CashGoodList.getJSONArray("goodList").size(); ++i) {
                if (!ArknightsApplication.CashGoodList.getJSONArray("goodList").getJSONObject(i).getString("goodId").equals(goodId)) continue;
                CashGood = ArknightsApplication.CashGoodList.getJSONArray("goodList").getJSONObject(i);
                break;
            }
            Boolean doubleCash = true;
            JSONArray info = UserSyncData.getJSONObject("shop").getJSONObject("CASH").getJSONArray("info");
            int diamondNum = 0;
            for (int i = 0; i < info.size(); ++i) {
                String id = info.getJSONObject(i).getString("id");
                if (!id.equals(goodId)) continue;
                doubleCash = false;
                break;
            }
            if (doubleCash.booleanValue()) {
                JSONObject CS = new JSONObject(true);
                CS.put("id", goodId);
                CS.put("count", 1);
                info.add(CS);
                diamondNum = CashGood.getIntValue("doubleCount");
            } else {
                diamondNum = CashGood.getIntValue("diamondNum") + CashGood.getIntValue("plusNum");
            }
            UserSyncData.getJSONObject("status").put("androidDiamond", (UserSyncData.getJSONObject("status").getIntValue("androidDiamond") + diamondNum));
            UserSyncData.getJSONObject("status").put("iosDiamond", (UserSyncData.getJSONObject("status").getIntValue("iosDiamond") + diamondNum));
            JSONObject item = new JSONObject(true);
            item.put("id", "4002");
            item.put("type", "DIAMOND");
            item.put("count", diamondNum);
            items.add(item);
        }
        if (goodId.indexOf("GP_") != -1) {
            JSONArray GPItems = new JSONArray();
            if (goodId.indexOf("GP_gW") != -1) {
                GPItems = ArknightsApplication.GPGoodList.getJSONObject("weeklyGroup").getJSONObject("packages").getJSONObject(goodId).getJSONArray("items");
            }
            if (goodId.indexOf("GP_gM") != -1) {
                GPItems = ArknightsApplication.GPGoodList.getJSONObject("monthlyGroup").getJSONObject("packages").getJSONObject(goodId).getJSONArray("items");
            }
            if (goodId.indexOf("GP_Once") != -1) {
                for (int i = 0; i < ArknightsApplication.GPGoodList.getJSONArray("oneTimeGP").size(); ++i) {
                    if (!ArknightsApplication.GPGoodList.getJSONArray("oneTimeGP").getJSONObject(i).getString("goodId").equals(goodId)) continue;
                    GPItems = ArknightsApplication.GPGoodList.getJSONArray("oneTimeGP").getJSONObject(i).getJSONArray("items");
                    break;
                }
            }
            for (int i = 0; i < GPItems.size(); ++i) {
                String reward_id = GPItems.getJSONObject(i).getString("id");
                String reward_type = GPItems.getJSONObject(i).getString("type");
                int reward_count = GPItems.getJSONObject(i).getIntValue("count");
                Admin.GM_GiveItem((JSONObject)UserSyncData, (String)reward_id, (String)reward_type, (int)reward_count, (JSONArray)items);
            }
        }
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        receiveItems.put("items", items);
        modified.put("status", UserSyncData.getJSONObject("status"));
        modified.put("shop", UserSyncData.getJSONObject("shop"));
        modified.put("troop", UserSyncData.getJSONObject("troop"));
        modified.put("skin", UserSyncData.getJSONObject("skin"));
        modified.put("inventory", UserSyncData.getJSONObject("inventory"));
        playerDataDelta.put("modified", modified);
        playerDataDelta.put("deleted", new JSONObject(true));
        result.put("playerDataDelta", playerDataDelta);
        result.put("result", 0);
        result.put("receiveItems", receiveItems);
        return result;
    }
}

