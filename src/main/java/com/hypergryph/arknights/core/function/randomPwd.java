/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypergryph.arknights.core.function.randomPwd
 *  java.lang.Character
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.security.SecureRandom
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.List
 */
package com.hypergryph.arknights.core.function;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Exception performing whole class analysis ignored.
 */
public class randomPwd {
    private static final String lowStr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String specialStr = "~!@#$%/";
    private static final String numStr = "0123456789";

    private static char getRandomChar(String str) {
        SecureRandom random = new SecureRandom();
        return str.charAt(random.nextInt(str.length()));
    }

    private static char getLowChar() {
        return randomPwd.getRandomChar((String)"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    private static char getUpperChar() {
        return Character.toUpperCase((char)randomPwd.getLowChar());
    }

    private static char getNumChar() {
        return randomPwd.getRandomChar((String)"0123456789");
    }

    private static char getSpecialChar() {
        return randomPwd.getRandomChar((String)"~!@#$%/");
    }

    private static char getRandomChar(int funNum) {
        switch (funNum) {
            case 0: {
                return randomPwd.getLowChar();
            }
            case 1: {
                return randomPwd.getUpperChar();
            }
            case 2: {
                return randomPwd.getNumChar();
            }
        }
        return randomPwd.getSpecialChar();
    }

    public static String getRandomPwd(int num) {
        ArrayList<Character> list = new ArrayList(num);
        list.add(Character.valueOf((char)randomPwd.getLowChar()));
        list.add(Character.valueOf((char)randomPwd.getUpperChar()));
        list.add(Character.valueOf((char)randomPwd.getNumChar()));
        list.add(Character.valueOf((char)randomPwd.getSpecialChar()));
        for (int i = 4; i < num; ++i) {
            SecureRandom random = new SecureRandom();
            int funNum = random.nextInt(4);
            list.add(Character.valueOf((char)randomPwd.getRandomChar((int)funNum)));
        }
        Collections.shuffle((List)list);
        StringBuilder stringBuilder = new StringBuilder(list.size());
        for (Character c : list) {
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    public static String randomKey(int num) {
        ArrayList<Character> list = new ArrayList(num);
        list.add(Character.valueOf((char)randomPwd.getLowChar()));
        list.add(Character.valueOf((char)randomPwd.getUpperChar()));
        list.add(Character.valueOf((char)randomPwd.getNumChar()));
        for (int i = 4; i < num; ++i) {
            SecureRandom random = new SecureRandom();
            int funNum = random.nextInt(4);
            list.add(Character.valueOf((char)randomPwd.getRandomChar((int)funNum)));
        }
        Collections.shuffle((List)list);
        StringBuilder stringBuilder = new StringBuilder(list.size());
        for (Character c : list) {
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }
}

