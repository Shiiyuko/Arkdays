/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONObject
 *  com.hypergryph.arknights.ArknightsApplication
 *  com.hypergryph.arknights.config.prod
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map$Entry
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.hypergryph.arknights.config;

import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/config/prod"})
public class prod {
    @RequestMapping(value={"/official/refresh_config"})
    public JSONObject RefreshConfig() {
        ArknightsApplication.reloadServerConfig();
        JSONObject result = new JSONObject(true);
        result.put("statusCode", 200);
        return result;
    }

    @RequestMapping(value={"/official/remote_config"})
    public JSONObject RemoteConfig(HttpServletRequest request) {
        return ArknightsApplication.serverConfig.getJSONObject("remote");
    }

    @RequestMapping(value={"/official/network_config"})
    public JSONObject NetworkConfig(HttpServletRequest request) {
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

    @RequestMapping(value={"/official/Android/version"})
    public JSONObject AndroidVersion(HttpServletRequest request) {
        return ArknightsApplication.serverConfig.getJSONObject("version").getJSONObject("android");
    }

    @RequestMapping(value={"/official/IOS/version"})
    public JSONObject IosVersion(HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /config/prod/official/IOS/version");
        return ArknightsApplication.serverConfig.getJSONObject("version").getJSONObject("ios");
    }

    @RequestMapping(value={"/b/remote_config"})
    public JSONObject BRemoteConfig(HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /config/prod/b/remote_config");
        return ArknightsApplication.serverConfig.getJSONObject("remote");
    }

    @RequestMapping(value={"/b/network_config"})
    public JSONObject BNetworkConfig(HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /config/prod/b/network_config");
        return ArknightsApplication.serverConfig.getJSONObject("network");
    }

    @RequestMapping(value={"/b/Android/version"})
    public JSONObject BAndroidVersion(HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /config/prod/b/Android/version");
        return ArknightsApplication.serverConfig.getJSONObject("version").getJSONObject("android");
    }

    @RequestMapping(value={"/announce_meta/Android/preannouncement.meta.json"})
    public JSONObject PreAnnouncement(HttpServletRequest request) {
        return ArknightsApplication.serverConfig.getJSONObject("announce").getJSONObject("preannouncement");
    }

    @RequestMapping(value={"/announce_meta/Android/announcement.meta.json"})
    public JSONObject announcement(HttpServletRequest request) {
        return ArknightsApplication.serverConfig.getJSONObject("announce").getJSONObject("announcement");
    }

    @RequestMapping(value={"/announce_meta/IOS/preannouncement.meta.json"})
    public JSONObject IOSPreAnnouncement(HttpServletRequest request) {
        return ArknightsApplication.serverConfig.getJSONObject("announce").getJSONObject("preannouncement");
    }

    @RequestMapping(value={"/announce_meta/IOS/announcement.meta.json"})
    public JSONObject IOSannouncement(HttpServletRequest request) {
        return ArknightsApplication.serverConfig.getJSONObject("announce").getJSONObject("announcement");
    }
}

