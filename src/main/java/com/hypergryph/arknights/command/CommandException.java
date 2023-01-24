/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypergryph.arknights.command.CommandException
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 */
package com.hypergryph.arknights.command;

public class CommandException
extends Exception {
    private final Object[] errorObjects;

    public CommandException(String message, Object ... objects) {
        super(message);
        this.errorObjects = objects;
    }

    public Object[] getErrorObjects() {
        return this.errorObjects;
    }
}

