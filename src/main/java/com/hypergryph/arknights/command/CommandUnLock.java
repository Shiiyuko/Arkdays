/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONObject
 *  com.hypergryph.arknights.ArknightsApplication
 *  com.hypergryph.arknights.command.CommandBase
 *  com.hypergryph.arknights.command.CommandException
 *  com.hypergryph.arknights.command.CommandUnLock
 *  com.hypergryph.arknights.command.ICommandSender
 *  com.hypergryph.arknights.core.dao.userDao
 *  com.hypergryph.arknights.core.pojo.Account
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.hypergryph.arknights.command;

import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.command.CommandBase;
import com.hypergryph.arknights.command.CommandException;
import com.hypergryph.arknights.command.ICommandSender;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.pojo.Account;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandUnLock
extends CommandBase {
    private static final Logger LOGGER = LogManager.getLogger();

    public String getCommandName() {
        return "unlock";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "[int]<玩家UID> [string]<关卡ID>";
    }

    public String getCommandDescription() {
        return "解锁某位玩家的关卡";
    }

    public String getCommandExample() {
        return "/unlock 10000001 main_03-08";
    }

    public String getCommandExampleUsage() {
        return "为UID为10000001的玩家解锁 3-8 关卡 详细信息请查看 data/excel/stage_table.json";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length >= 3) {
            int uid = 0;
            String stageId = "";
            try {
                uid = Integer.parseInt((String)args[1]);
                stageId = args[2];
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
            if (!ArknightsApplication.stageTable.containsKey(stageId)) {
                LOGGER.error("未知的关卡ID，请检查并修改后重试");
                return;
            }
            JSONObject UserSyncData = JSONObject.parseObject((String)((Account)user2.get(0)).getUser());
            JSONObject stageInfo = new JSONObject();
            stageInfo.put("stageId", stageId);
            stageInfo.put("completeTimes", 1);
            stageInfo.put("startTimes", 1);
            stageInfo.put("practiceTimes", 1);
            stageInfo.put("state", 3);
            stageInfo.put("hasBattleReplay", 0);
            stageInfo.put("noCostCnt", 0);
            UserSyncData.getJSONObject("dungeon").getJSONObject("stages").put(stageId, stageInfo);
            userDao.setUserData((Long)Long.valueOf((long)uid), (JSONObject)UserSyncData);
            LOGGER.info("已为该玩家解锁 " + stageId);
            return;
        }
        LOGGER.error("使用方式: /" + this.getCommandName() + " " + this.getCommandUsage(sender));
    }
}

