/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypergryph.arknights.ArknightsApplication
 *  com.hypergryph.arknights.console
 *  java.lang.Object
 *  java.lang.String
 *  net.minecrell.terminalconsole.SimpleTerminalConsole
 */
package com.hypergryph.arknights;

import com.hypergryph.arknights.ArknightsApplication;
import net.minecrell.terminalconsole.SimpleTerminalConsole;

public class console
extends SimpleTerminalConsole {
    protected boolean isRunning() {
        return true;
    }

    protected void runCommand(String s) {
        ArknightsApplication.ConsoleCommandManager.executeCommand(ArknightsApplication.Sender, s);
    }

    protected void shutdown() {
    }
}

