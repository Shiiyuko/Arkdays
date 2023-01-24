/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.date.DateUtil
 *  com.alibaba.fastjson.JSONObject
 *  com.hypergryph.arknights.ArknightsApplication
 *  com.hypergryph.arknights.command.CommandManager
 *  com.hypergryph.arknights.command.ICommandSender
 *  com.hypergryph.arknights.console
 *  com.hypergryph.arknights.core.dao.mailDao
 *  com.hypergryph.arknights.core.dao.userDao
 *  com.hypergryph.arknights.core.file.IOTools
 *  com.hypergryph.arknights.core.function.randomPwd
 *  java.lang.CharSequence
 *  java.lang.Class
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
 *  java.net.InetAddress
 *  java.net.UnknownHostException
 *  java.util.Date
 *  javax.servlet.http.HttpServletRequest
 *  javax.sql.DataSource
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.springframework.boot.Banner$Mode
 *  org.springframework.boot.SpringApplication
 *  org.springframework.boot.autoconfigure.SpringBootApplication
 *  org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.jdbc.datasource.DriverManagerDataSource
 *  org.springframework.util.StringUtils
 */
package com.hypergryph.arknights;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.command.CommandManager;
import com.hypergryph.arknights.command.ICommandSender;
import com.hypergryph.arknights.console;
import com.hypergryph.arknights.core.dao.mailDao;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.file.IOTools;
import com.hypergryph.arknights.core.function.randomPwd;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.StringUtils;

/*
 * Exception performing whole class analysis ignored.
 */
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class ArknightsApplication {
    public static final Logger LOGGER = LogManager.getLogger();
    public static JdbcTemplate jdbcTemplate = null;
    public static JSONObject serverConfig = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/config.json"));
    public static boolean enableServer = serverConfig.getJSONObject("server").getBooleanValue("enableServer");
    public static JSONObject DefaultSyncData = new JSONObject();
    public static JSONObject characterJson = new JSONObject();
    public static JSONObject roguelikeTable = new JSONObject();
    public static JSONObject stageTable = new JSONObject();
    public static JSONObject itemTable = new JSONObject();
    public static JSONObject mainStage = new JSONObject();
    public static JSONObject normalGachaData = new JSONObject();
    public static JSONObject uniequipTable = new JSONObject();
    public static JSONObject skinGoodList = new JSONObject();
    public static JSONObject skinTable = new JSONObject();
    public static JSONObject charwordTable = new JSONObject();
    public static JSONObject CrisisData = new JSONObject();
    public static JSONObject CashGoodList = new JSONObject();
    public static JSONObject GPGoodList = new JSONObject();
    public static JSONObject LowGoodList = new JSONObject();
    public static JSONObject HighGoodList = new JSONObject();
    public static JSONObject ExtraGoodList = new JSONObject();
    public static JSONObject LMTGSGoodList = new JSONObject();
    public static JSONObject EPGSGoodList = new JSONObject();
    public static JSONObject RepGoodList = new JSONObject();
    public static JSONObject FurniGoodList = new JSONObject();
    public static JSONObject SocialGoodList = new JSONObject();
    public static JSONObject AllProductList = new JSONObject();
    public static JSONObject unlockActivity = new JSONObject();
    public static JSONObject buildingData = new JSONObject();
    public static CommandManager ConsoleCommandManager = new CommandManager();
    public static ICommandSender Sender = () -> "Console";

    public static void main(String[] args) throws Exception {
        String host = serverConfig.getJSONObject("database").getString("host");
        String port = serverConfig.getJSONObject("database").getString("port");
        String dbname = serverConfig.getJSONObject("database").getString("dbname");
        String username = serverConfig.getJSONObject("database").getString("username");
        String password = serverConfig.getJSONObject("database").getString("password");
        String extra = serverConfig.getJSONObject("database").getString("extra");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://" + host + ":" + port + "/" + dbname + "?" + extra);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        jdbcTemplate = new JdbcTemplate((DataSource)dataSource);
        SpringApplication springApplication = new SpringApplication(new Class[]{ArknightsApplication.class});
        springApplication.setBannerMode(Banner.Mode.OFF);
        String[] disabledCommands = new String[]{"--server.port=" + serverConfig.getJSONObject("server").getString("https"), "--spring.profiles.active=default"};
        String[] fullArgs = StringUtils.concatenateStringArrays((String[])args, (String[])disabledCommands);
        springApplication.run(fullArgs);
        ArknightsApplication.reloadServerConfig();
        String MysqlVersion = null;
        LOGGER.info("检测数据库版本中...");
        try {
            MysqlVersion = userDao.queryMysqlVersion();
        }
        catch (Exception e) {
            LOGGER.error("无法连接至Mysql数据库");
            System.exit((int)0);
        }
        if (Integer.valueOf((String)MysqlVersion.substring(0, 1)) < 8) {
            LOGGER.error("Mysql版本需要 >= 8.0");
            LOGGER.error("请升级后重试");
            System.exit((int)0);
        }
        LOGGER.info("数据库版本 " + MysqlVersion);
        LOGGER.info("服务端版本 1.9.3");
        LOGGER.info("客户端版本 1.7.21");
        LOGGER.info("构建时间 2022年02月15日22时33分");
        if (serverConfig.getJSONObject("server").getString("GMKey") == null) {
            serverConfig.getJSONObject("server").put("GMKey", randomPwd.getRandomPwd((int)64));
            IOTools.SaveJsonFile((String)(System.getProperty((String)"user.dir") + "/config.json"), (JSONObject)serverConfig);
            LOGGER.info("已随机生成新的管理员密钥");
        }
        LOGGER.info("管理员密钥 " + serverConfig.getJSONObject("server").getString("GMKey"));
        if (userDao.tableExists((String)"account").size() == 0) {
            userDao.insertTable();
            LOGGER.info("检测到玩家数据库不存在，已自动生成");
        }
        if (userDao.tableExists((String)"mail").size() == 0) {
            mailDao.insertTable();
            LOGGER.info("检测到邮件数据库不存在，已自动生成");
        }
        ArknightsApplication.getTimestamp();
        LOGGER.info("服务端更新日志:");
        LOGGER.info("[+] 新增控制台指令 /activity");
        LOGGER.info("[+] 新增控制台指令 /unlock");
        LOGGER.info("[+] 新增控制台指令 /upgrade");
        LOGGER.info("[+] 新增控制台指令 /mail 感谢@rainy");
        LOGGER.info("[*] 邮件储存方式更改为Mysql");
        LOGGER.info("[*] 肉鸽持续开发中...");
        LOGGER.info("启动完成! 如果需要获取帮助,请输入 \"help\"");
        new console().start();
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if ((ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) && (ipAddress = request.getRemoteAddr()).equals("127.0.0.1")) {
                try {
                    ipAddress = InetAddress.getLocalHost().getHostAddress();
                }
                catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
            if (ipAddress != null) {
                if (ipAddress.contains((CharSequence)",")) {
                    return ipAddress.split(",")[0];
                }
                return ipAddress;
            }
            return "";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static long getTimestamp() {
        long ts = serverConfig.getJSONObject("timestamp").getLongValue(DateUtil.dayOfWeekEnum((Date)DateUtil.date()).toString().toLowerCase());
        if (ts == -1L) {
            ts = new Date().getTime() / 1000L;
        }
        return ts;
    }

    public static void reloadServerConfig() {
        long startTime = System.currentTimeMillis();
        LOGGER.info("载入服务器配置...");
        serverConfig = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/config.json"));
        enableServer = serverConfig.getJSONObject("server").getBooleanValue("enableServer");
        LOGGER.info("载入游戏数据...");
        DefaultSyncData = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/defaultSyncData.json"));
        characterJson = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/excel/character_table.json"));
        roguelikeTable = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/excel/roguelike_topic_table.json"));
        stageTable = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/excel/stage_table.json")).getJSONObject("stages");
        itemTable = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/excel/item_table.json")).getJSONObject("items");
        mainStage = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/battle/stage.json")).getJSONObject("MainStage");
        normalGachaData = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/normalGacha.json"));
        uniequipTable = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/excel/uniequip_table.json")).getJSONObject("equipDict");
        skinGoodList = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/shop/SkinGoodList.json"));
        skinTable = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/excel/skin_table.json")).getJSONObject("charSkins");
        charwordTable = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/excel/charword_table.json"));
        CashGoodList = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/shop/CashGoodList.json"));
        GPGoodList = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/shop/GPGoodList.json"));
        LowGoodList = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/shop/LowGoodList.json"));
        HighGoodList = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/shop/HighGoodList.json"));
        ExtraGoodList = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/shop/ExtraGoodList.json"));
        LMTGSGoodList = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/shop/LMTGSGoodList.json"));
        EPGSGoodList = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/shop/EPGSGoodList.json"));
        RepGoodList = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/shop/RepGoodList.json"));
        FurniGoodList = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/shop/FurniGoodList.json"));
        SocialGoodList = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/shop/SocialGoodList.json"));
        AllProductList = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/shop/AllProductList.json"));
        unlockActivity = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/unlockActivity.json"));
        CrisisData = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/battle/crisis.json"));
        buildingData = IOTools.ReadJsonFile((String)(System.getProperty((String)"user.dir") + "/data/excel/building_data.json")).getJSONObject("workshopFormulas");
        long endTime = System.currentTimeMillis();
        LOGGER.info("载入完成，耗时：" + (endTime - startTime) + "ms");
    }
}

