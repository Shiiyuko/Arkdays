/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypergryph.arknights.command.CommandBase
 *  com.hypergryph.arknights.command.ICommand
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collections
 *  java.util.List
 */
package com.hypergryph.arknights.command;

import com.hypergryph.arknights.command.ICommand;
import java.util.Collections;
import java.util.List;

public abstract class CommandBase
implements ICommand {
    public List<String> getCommandAliases() {
        return Collections.emptyList();
    }

    public int compareTo(ICommand p_compareTo_1_) {
        return this.getCommandName().compareTo(p_compareTo_1_.getCommandName());
    }
}

