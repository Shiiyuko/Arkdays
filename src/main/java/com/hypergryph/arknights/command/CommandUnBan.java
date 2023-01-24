/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypergryph.arknights.command.CommandBase
 *  com.hypergryph.arknights.command.CommandException
 *  com.hypergryph.arknights.command.CommandUnBan
 *  com.hypergryph.arknights.command.ICommandSender
 *  com.hypergryph.arknights.core.dao.userDao
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.hypergryph.arknights.command;

import com.hypergryph.arknights.command.CommandBase;
import com.hypergryph.arknights.command.CommandException;
import com.hypergryph.arknights.command.ICommandSender;
import com.hypergryph.arknights.core.dao.userDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandUnBan
extends CommandBase {
    private static final Logger LOGGER = LogManager.getLogger();

    public String getCommandName() {
        return "unban";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "[int]<玩家UID>";
    }

    public String getCommandDescription() {
        return "解封某位玩家";
    }

    public String getCommandExample() {
        return "/unban 1";
    }

    public String getCommandExampleUsage() {
        return "解封UID为1的玩家";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length >= 2) {
            int uid = 0;
            try {
                uid = Integer.parseInt((String)args[1]);
            }
            catch (Exception e) {
                LOGGER.error("使用方式: /" + this.getCommandName() + " " + this.getCommandUsage(sender));
                return;
            }
            if (userDao.setBanStatus((int)uid, (int)0) != 1) {
                LOGGER.error("解封失败");
                return;
            }
            LOGGER.info("已解封该玩家");
            return;
        }
        LOGGER.error("使用方式: /" + this.getCommandName() + " " + this.getCommandUsage(sender));
    }
}

