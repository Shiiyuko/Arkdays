/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONObject
 *  com.hypergryph.arknights.auth.online
 *  java.lang.Object
 *  java.lang.String
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestHeader
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.hypergryph.arknights.auth;

import com.alibaba.fastjson.JSONObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/online"})
public class online {
    private static final Logger LOGGER = LogManager.getLogger();

    @PostMapping(value={"/v1/ping"}, produces={"application/json;charset=UTF-8"})
    public JSONObject Ping(HttpServletRequest request) {
        JSONObject result = new JSONObject(true);
        result.put("result", 0);
        result.put("message", "OK");
        result.put("interval", 2242);
        result.put("timeLeft", -1);
        result.put("alertTime", 600);
        return result;
    }

    @PostMapping(value={"/v1/loginout"}, produces={"application/json;charset=UTF-8"})
    public JSONObject LoginOut(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject(true);
        jsonObject.put("result", 0);
        return jsonObject;
    }
}

