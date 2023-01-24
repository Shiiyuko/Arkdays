/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypergryph.arknights.command.CommandActivity
 *  com.hypergryph.arknights.command.CommandBan
 *  com.hypergryph.arknights.command.CommandGive
 *  com.hypergryph.arknights.command.CommandHandler
 *  com.hypergryph.arknights.command.CommandHelp
 *  com.hypergryph.arknights.command.CommandMail
 *  com.hypergryph.arknights.command.CommandManager
 *  com.hypergryph.arknights.command.CommandReload
 *  com.hypergryph.arknights.command.CommandStop
 *  com.hypergryph.arknights.command.CommandUnBan
 *  com.hypergryph.arknights.command.CommandUnLock
 *  com.hypergryph.arknights.command.CommandUpgrade
 *  com.hypergryph.arknights.command.ICommand
 *  java.lang.Object
 */
package com.hypergryph.arknights.command;

import com.hypergryph.arknights.command.CommandActivity;
import com.hypergryph.arknights.command.CommandBan;
import com.hypergryph.arknights.command.CommandGive;
import com.hypergryph.arknights.command.CommandHandler;
import com.hypergryph.arknights.command.CommandHelp;
import com.hypergryph.arknights.command.CommandMail;
import com.hypergryph.arknights.command.CommandReload;
import com.hypergryph.arknights.command.CommandStop;
import com.hypergryph.arknights.command.CommandUnBan;
import com.hypergryph.arknights.command.CommandUnLock;
import com.hypergryph.arknights.command.CommandUpgrade;
import com.hypergryph.arknights.command.ICommand;

public class CommandManager
extends CommandHandler {
    public CommandManager() {
        this.registerCommand((ICommand)new CommandHelp());
        this.registerCommand((ICommand)new CommandReload());
        this.registerCommand((ICommand)new CommandBan());
        this.registerCommand((ICommand)new CommandUnBan());
        this.registerCommand((ICommand)new CommandStop());
        this.registerCommand((ICommand)new CommandGive());
        this.registerCommand((ICommand)new CommandMail());
        this.registerCommand((ICommand)new CommandUpgrade());
        this.registerCommand((ICommand)new CommandUnLock());
        this.registerCommand((ICommand)new CommandActivity());
    }
}

