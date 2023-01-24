/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONArray
 *  com.alibaba.fastjson.JSONObject
 *  com.hypergryph.arknights.Admin
 *  com.hypergryph.arknights.ArknightsApplication
 *  com.hypergryph.arknights.command.CommandBase
 *  com.hypergryph.arknights.command.CommandException
 *  com.hypergryph.arknights.command.CommandGive
 *  com.hypergryph.arknights.command.ICommandSender
 *  com.hypergryph.arknights.core.dao.userDao
 *  com.hypergryph.arknights.core.pojo.Account
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.Map$Entry
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.hypergryph.arknights.command;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.Admin;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.command.CommandBase;
import com.hypergryph.arknights.command.CommandException;
import com.hypergryph.arknights.command.ICommandSender;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.pojo.Account;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandGive
extends CommandBase {
    private static final Logger LOGGER = LogManager.getLogger();

    public String getCommandName() {
        return "give";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "[int]<玩家UID> [string]<物品ID> [int]<物品数量>";
    }

    public String getCommandDescription() {
        return "给予玩家物品或角色";
    }

    public String getCommandExample() {
        return "/give 10000001 4002 64 | /give 10000001 char_002_amiya 1 | /give 10000001 allchar 1";
    }

    public String getCommandExampleUsage() {
        return "给予UID为10000001的玩家64颗至纯源石 详细信息请查看 data/excel/item_table.json | 给予UID为10000001的玩家一个阿米娅 详细信息请查看 data/excel/character_table.json | 给予UID为10000001的玩家所有角色";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length >= 3) {
            int uid = 0;
            int itemCount = 0;
            String itemId = "";
            try {
                uid = Integer.parseInt((String)args[1]);
                itemId = args[2];
                itemCount = Integer.parseInt((String)args[3]);
            }
            catch (Exception e) {
                LOGGER.error("使用方式: /" + this.getCommandName() + " " + this.getCommandUsage(sender));
                return;
            }
            List user2 = userDao.queryAccountByUid((long)uid);
            if (user2.size() != 1) {
                LOGGER.error("无法找到该玩家");
                return;
            }
            JSONObject UserSyncData = JSONObject.parseObject((String)((Account)user2.get(0)).getUser());
            JSONArray excludeCharList = new JSONArray();
            excludeCharList.add("char_504_rguard");
            excludeCharList.add("char_505_rcast");
            excludeCharList.add("char_506_rmedic");
            excludeCharList.add("char_507_rsnipe");
            excludeCharList.add("char_508_aguard");
            excludeCharList.add("char_509_acast");
            excludeCharList.add("char_510_amedic");
            excludeCharList.add("char_511_asnipe");
            excludeCharList.add("char_512_aprot");
            if (itemId.indexOf("char") != -1) {
                if (itemId.equals("allchar")) {
                    JSONArray items = new JSONArray();
                    for (Map.Entry entry : ArknightsApplication.characterJson.entrySet()) {
                        String charId = entry.getKey().toString();
                        if (charId.indexOf("char_") == -1 || excludeCharList.contains(charId)) continue;
                        Admin.GM_GiveItem((JSONObject)UserSyncData, (String)charId, (String)"CHAR", (int)1, (JSONArray)items);
                    }
                    userDao.setUserData((Long)Long.valueOf((long)uid), (JSONObject)UserSyncData);
                    LOGGER.info("已发送所有干员至该玩家");
                    return;
                }
                if (!ArknightsApplication.characterJson.containsKey(itemId)) {
                    LOGGER.error("未查找到此干员");
                    return;
                }
                JSONArray items = new JSONArray();
                Admin.GM_GiveItem((JSONObject)UserSyncData, (String)itemId, (String)"CHAR", (int)1, (JSONArray)items);
                userDao.setUserData((Long)Long.valueOf((long)uid), (JSONObject)UserSyncData);
                LOGGER.info("已把干员" + ArknightsApplication.characterJson.getJSONObject(itemId).getString("name") + "给予该玩家，§c玩家需重新登录");
                return;
            }
            if (!ArknightsApplication.itemTable.containsKey(itemId)) {
                LOGGER.error("未查找到此物品");
                return;
            }
            if (itemCount <= 0 || itemCount > 9999999) {
                LOGGER.error("数量范围应在1-9999999");
                return;
            }
            String itemType = ArknightsApplication.itemTable.getJSONObject(itemId).getString("itemType");
            JSONArray items = new JSONArray();
            Admin.GM_GiveItem((JSONObject)UserSyncData, (String)itemId, (String)itemType, (int)itemCount, (JSONArray)items);
            userDao.setUserData((Long)Long.valueOf((long)uid), (JSONObject)UserSyncData);
            LOGGER.info("已给予该玩家 " + itemCount + " " + ArknightsApplication.itemTable.getJSONObject(itemId).getString("name"));
            return;
        }
        LOGGER.error("使用方式: /" + this.getCommandName() + " " + this.getCommandUsage(sender));
    }
}

