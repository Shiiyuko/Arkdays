/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONObject
 *  com.hypergryph.arknights.track.beat
 *  java.lang.Object
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.hypergryph.arknights.track;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class beat {
    @RequestMapping(value={"/beat"})
    public JSONObject Beat() {
        JSONObject result = new JSONObject(true);
        result.put("code", 200);
        result.put("msg", "ok");
        return result;
    }
}

