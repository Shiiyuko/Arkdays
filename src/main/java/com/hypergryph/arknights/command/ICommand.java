/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypergryph.arknights.command.CommandException
 *  com.hypergryph.arknights.command.ICommand
 *  com.hypergryph.arknights.command.ICommandSender
 *  java.lang.Comparable
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 */
package com.hypergryph.arknights.command;

import com.hypergryph.arknights.command.CommandException;
import com.hypergryph.arknights.command.ICommandSender;
import java.util.List;

public interface ICommand
extends Comparable<ICommand> {
    public String getCommandName();

    public String getCommandUsage(ICommandSender var1);

    public String getCommandDescription();

    public String getCommandExample();

    public String getCommandExampleUsage();

    public List<String> getCommandAliases();

    public void processCommand(ICommandSender var1, String[] var2) throws CommandException;
}

