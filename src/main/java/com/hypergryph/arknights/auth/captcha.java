/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONObject
 *  com.hypergryph.arknights.auth.captcha
 *  java.lang.Object
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.hypergryph.arknights.auth;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/captcha"})
public class captcha {
    @RequestMapping(value={"/v1/register"})
    public JSONObject RegisterCaptcha() {
        JSONObject result = new JSONObject(true);
        result.put("result", 1);
        return result;
    }
}

