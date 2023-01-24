/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypergryph.arknights.command.ICommand
 *  com.hypergryph.arknights.command.ICommandManager
 *  com.hypergryph.arknights.command.ICommandSender
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.Map
 */
package com.hypergryph.arknights.command;

import com.hypergryph.arknights.command.ICommand;
import com.hypergryph.arknights.command.ICommandSender;
import java.util.List;
import java.util.Map;

public interface ICommandManager {
    public static int executeCommand(ICommandSender sender, String rawCommand) {
        return 0;
    }

    public List<ICommand> getPossibleCommands(ICommandSender var1);

    public Map<String, ICommand> getCommands();
}

