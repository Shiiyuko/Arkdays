/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONObject
 *  com.hypergryph.arknights.ArknightsApplication
 *  com.hypergryph.arknights.network
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map$Entry
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.hypergryph.arknights;

import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class network {
    @RequestMapping(value={"/"})
    public JSONObject network_config(HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /config/prod/official/network_config");
        JSONObject server_network = ArknightsApplication.serverConfig.getJSONObject("network");
        JSONObject network2 = new JSONObject(true);
        network2.put("sign", server_network.getString("sign"));
        JSONObject content = new JSONObject(true);
        JSONObject configs = server_network.getJSONObject("configs");
        content.put("configVer", server_network.getString("configVer"));
        content.put("funcVer", server_network.getString("funcVer"));
        for (Map.Entry entry : configs.entrySet()) {
            JSONObject funcNetwork = configs.getJSONObject(entry.getKey().toString()).getJSONObject("network");
            for (Map.Entry funcNetworkEntry : funcNetwork.entrySet()) {
                String value = funcNetwork.getString(funcNetworkEntry.getKey().toString());
                funcNetwork.put(funcNetworkEntry.getKey().toString(), value.replace((CharSequence)"{server}", (CharSequence)ArknightsApplication.serverConfig.getJSONObject("server").getString("url")));
            }
        }
        content.put("configs", configs);
        network2.put("content", content.toJSONString());
        return network2;
    }
}

