/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.hypergryph.arknights.command.CommandException
 *  com.hypergryph.arknights.command.CommandHandler
 *  com.hypergryph.arknights.command.ICommand
 *  com.hypergryph.arknights.command.ICommandManager
 *  com.hypergryph.arknights.command.ICommandSender
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Map
 *  java.util.Set
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.hypergryph.arknights.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hypergryph.arknights.command.CommandException;
import com.hypergryph.arknights.command.ICommand;
import com.hypergryph.arknights.command.ICommandManager;
import com.hypergryph.arknights.command.ICommandSender;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandHandler
implements ICommandManager {
    private static final Logger logger = LogManager.getLogger();
    private final Map<String, ICommand> commandMap = Maps.newHashMap();
    private final Set<ICommand> commandSet = Sets.newHashSet();

    public List<ICommand> getPossibleCommands(ICommandSender sender) {
        ArrayList list = Lists.newArrayList();
        for (ICommand icommand : this.commandSet) {
            list.add(icommand);
        }
        return list;
    }

    public ICommand registerCommand(ICommand command) {
        this.commandMap.put(command.getCommandName(), command);
        this.commandSet.add(command);
        for (String s : command.getCommandAliases()) {
            ICommand icommand = (ICommand)this.commandMap.get(s);
            if (icommand != null && icommand.getCommandName().equals(s)) continue;
            this.commandMap.put(s, command);
        }
        return command;
    }

    public int executeCommand(ICommandSender sender, String rawCommand) {
        String[] astring = (rawCommand = rawCommand.trim()).split(" ");
        String s = astring[0];
        ICommand icommand = (ICommand)this.commandMap.get(s);
        if (icommand == null) {
            logger.error("未知或不完整的命令 '" + s + "'");
            return 0;
        }
        try {
            icommand.processCommand(sender, astring);
        }
        catch (CommandException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<String, ICommand> getCommands() {
        return this.commandMap;
    }
}

