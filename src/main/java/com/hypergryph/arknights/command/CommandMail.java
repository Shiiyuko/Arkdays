/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONArray
 *  com.alibaba.fastjson.JSONObject
 *  com.hypergryph.arknights.ArknightsApplication
 *  com.hypergryph.arknights.command.CommandBase
 *  com.hypergryph.arknights.command.CommandException
 *  com.hypergryph.arknights.command.CommandMail
 *  com.hypergryph.arknights.command.ICommandSender
 *  com.hypergryph.arknights.core.dao.mailDao
 *  com.hypergryph.arknights.core.dao.userDao
 *  com.hypergryph.arknights.core.pojo.Account
 *  com.hypergryph.arknights.core.pojo.Mail
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.List
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.hypergryph.arknights.command;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.command.CommandBase;
import com.hypergryph.arknights.command.CommandException;
import com.hypergryph.arknights.command.ICommandSender;
import com.hypergryph.arknights.core.dao.mailDao;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.pojo.Account;
import com.hypergryph.arknights.core.pojo.Mail;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Arrays;

/*
 * Exception performing whole class analysis ignored.
 */
public class CommandMail
extends CommandBase {
    private static final Logger LOGGER = LogManager.getLogger();

    public String getCommandName() {
        return "mail";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "[string]<邮件名|list> [string]<子命令>";
    }

    public String getCommandDescription() {
        return "处理邮件";
    }

    public String getCommandExample() {
        return "/mail 测试 create";
    }

    public String getCommandExampleUsage() {
        return "创造邮件，其名为\"测试\"";
    }

    
    public void processCommand(ICommandSender paramICommandSender, String[] paramArrayOfString) throws CommandException {
    if (paramArrayOfString.length == 1) {
      LOGGER.error("需要指定邮件名或使用 /mail list");
      return;
    } 
    if (paramArrayOfString.length == 2) {
      if (paramArrayOfString[1].equals("list")) {
        mailDao.queryMails();
        throw new VerifyError("d2j: can't get operand(s) for move-result-object, wrong position ?");
      } 
      LOGGER.error("可用的子命令: create<创建邮件> setFrom<设置来源> setSubject<设置主题> setContent<设置内容> items<物品管理> info<查看信息> send<发送邮件>");
      return;
    } 
    List<Mail> list = mailDao.queryMailByName(paramArrayOfString[1]);
    String[] arrayOfString = Arrays.<String, String>copyOfRange(paramArrayOfString, 3, paramArrayOfString.length, String[].class);
    String str = paramArrayOfString[2];
    int i = -1;
    switch (str.hashCode()) {
      default:
        switch (i) {
          default:
            LOGGER.error("可用的子命令: create<创建邮件> setFrom<设置来源> setSubject<设置主题> setContent<设置内容> items<物品管理> send<发送邮件>");
            return;
          case 0:
            processCreateCommand(paramICommandSender, paramArrayOfString[1], list, arrayOfString);
            return;
          case 1:
            processSetFromCommand(paramICommandSender, paramArrayOfString[1], list, arrayOfString);
            return;
          case 2:
            processSetSubjectCommand(paramICommandSender, paramArrayOfString[1], list, arrayOfString);
            return;
          case 3:
            processSetContentCommand(paramICommandSender, paramArrayOfString[1], list, arrayOfString);
            return;
          case 4:
            processItemsCommand(paramICommandSender, paramArrayOfString[1], list, arrayOfString);
            return;
          case 5:
            processSendCommand(paramICommandSender, paramArrayOfString[1], list, arrayOfString);
            return;
          case 6:
            break;
        } 
        break;
      case -1352294148:
        if (str.equals("create"))
          i = 0; 
      case 1984579372:
        if (str.equals("setFrom"))
          i = 1; 
      case 1105780330:
        if (str.equals("setSubject"))
          i = 2; 
      case -369771081:
        if (str.equals("setContent"))
          i = 3; 
      case 100526016:
        if (str.equals("items"))
          i = 4; 
      case 3526536:
        if (str.equals("send"))
          i = 5; 
      case 3237038:
        if (str.equals("info"))
          i = 6; 
    } 
    processInfoCommand(paramICommandSender, paramArrayOfString[1], list, arrayOfString);
    }
  
    private void processInfoCommand(ICommandSender sender, String arg, List<Mail> mailList, String[] subCommandArgs) {
        if (mailList.size() != 1) {
            LOGGER.error("指定的邮件名不存在");
            return;
        }
        Mail mail2 = (Mail)mailList.get(0);
        LOGGER.info("ID: " + mail2.getId() + ", 邮件名: " + mail2.getName());
        LOGGER.info("主题: " + mail2.getSubject() + ", 来自: " + ArknightsApplication.characterJson.getJSONObject(mail2.getFrom()).getString("name"));
        LOGGER.info("正文: ");
        LOGGER.info(mail2.getContent());
        LOGGER.info("物品: ");
        JSONArray.parseArray((String)mail2.getItems()).forEach(obj -> {
            if (!(obj instanceof JSONObject)) {
                return;
            }
            JSONObject item = (JSONObject)obj;
            if (item.getString("type").equals("CHAR")) {
                LOGGER.info(ArknightsApplication.characterJson.getJSONObject(item.getString("id")).getString("name") + " * " + item.getIntValue("count"));
            } else {
                LOGGER.info(ArknightsApplication.itemTable.getJSONObject(item.getString("id")).getString("name") + " * " + item.getIntValue("count"));
            }
        });
    }

    private void processCreateCommand(ICommandSender sender, String name, List<Mail> mailList, String[] args) {
        if (mailList.size() != 0) {
            LOGGER.error("指定的邮件名已存在");
            return;
        }
        int result = mailDao.createMail((String)name);
        if (result == 1) {
            LOGGER.info("创建成功");
        } else {
            LOGGER.error("出了点意外，为什么捏？");
        }
    }

    private void processSetFromCommand(ICommandSender sender, String name, List<Mail> mailList, String[] args) {
        int result;
        if (mailList.size() != 1) {
            LOGGER.error("指定的邮件名不存在");
            return;
        }
        if (args.length == 0) {
            LOGGER.error("用法: /mail " + name + " setFrom [string]<来源>");
            return;
        }
        String characterId = null;
        for (String id : ArknightsApplication.characterJson.keySet()) {
            JSONObject character = ArknightsApplication.characterJson.getJSONObject(id);
            if (!character.getString("name").equals(args[0]) && !id.equals(args[0])) continue;
            characterId = id;
        }
        if (characterId == null) {
            characterId = "none";
            LOGGER.info("未查找到此干员，默认企鹅物流");
        }
        if ((result = mailDao.setMailFrom((int)((Mail)mailList.get(0)).getId(), (String)characterId)) == 1) {
            LOGGER.info("修改成功");
        } else {
            LOGGER.error("出了点意外，为什么捏？");
        }
    }

    private void processSetSubjectCommand(ICommandSender sender, String name, List<Mail> mailList, String[] args) {
        if (mailList.size() != 1) {
            LOGGER.error("指定的邮件名不存在");
            return;
        }
        if (args.length == 0) {
            LOGGER.error("用法: /mail " + name + " setSubject [string]<主题>");
            return;
        }
        int result = mailDao.setMailSubject((int)((Mail)mailList.get(0)).getId(), (String)CommandMail.joinToString((String[])args, (String)" "));
        if (result == 1) {
            LOGGER.info("修改成功");
        } else {
            LOGGER.error("出了点意外，为什么捏？");
        }
    }

    private void processSetContentCommand(ICommandSender sender, String name, List<Mail> mailList, String[] args) {
        if (mailList.size() != 1) {
            LOGGER.error("指定的邮件名不存在");
            return;
        }
        if (args.length == 0) {
            LOGGER.error("用法: /mail " + name + " setConent [string]<内容>");
            return;
        }
        int result = mailDao.setMailContent((int)((Mail)mailList.get(0)).getId(), (String)CommandMail.joinToString((String[])args, (String)" "));
        if (result == 1) {
            LOGGER.info("修改成功");
        } else {
            LOGGER.error("出了点意外，为什么捏？");
        }
    }

    private void processItemsCommand(ICommandSender sender, String name, List<Mail> mailList, String[] args) {
        if (mailList.size() != 1) {
            LOGGER.error("指定的邮件名不存在");
            return;
        }
        Mail mail2 = (Mail)mailList.get(0);
        if (args.length == 0) {
            LOGGER.error("可用的二级子命令: list<查看物品列表> add<添加物品> del<删除物品>");
        } else if (args[0].equals("list")) {
            JSONArray.parseArray((String)mail2.getItems()).forEach(obj -> {
                if (!(obj instanceof JSONObject)) {
                    return;
                }
                JSONObject item = (JSONObject)obj;
                if (item.getString("type").equals("CHAR")) {
                    LOGGER.info(ArknightsApplication.characterJson.getJSONObject(item.getString("id")).getString("name") + " * " + item.getIntValue("count"));
                } else {
                    LOGGER.info(ArknightsApplication.itemTable.getJSONObject(item.getString("id")).getString("name") + " * " + item.getIntValue("count"));
                }
            });
        } else if (args[0].equals("add")) {
            if (args.length == 1) {
                LOGGER.error("用法: /mail " + name + " items add [String]<物品|干员> [int]<数量>");
                return;
            }
            int itemCount = 0;
            try {
                itemCount = Integer.parseInt((String)args[2]);
            }
            catch (Exception e) {
                LOGGER.error("解析数量时出错, 原文: " + args[2]);
                return;
            }
            if (itemCount <= 0 || itemCount > 9999999) {
                LOGGER.error("数量范围应在1-9999999");
                return;
            }
            String itemId = null;
            String itemType = null;
            for (String id : ArknightsApplication.itemTable.keySet()) {
                if (!ArknightsApplication.itemTable.getJSONObject(id).getString("name").equals(args[1]) && !id.equals(args[1])) continue;
                itemId = id;
                itemType = ArknightsApplication.itemTable.getJSONObject(id).getString("itemType");
            }
            for (String id : ArknightsApplication.characterJson.keySet()) {
                if (!ArknightsApplication.characterJson.getJSONObject(id).getString("name").equals(args[1]) && !id.equals(args[1])) continue;
                itemId = id;
                itemType = "CHAR";
            }
            if (itemId == null) {
                LOGGER.error("未查找到此物品或干员");
                return;
            }
            JSONArray items = JSONArray.parseArray((String)mail2.getItems());
            JSONObject item = new JSONObject(true);
            item.put("id", itemId);
            item.put("type", itemType);
            item.put("count", itemCount);
            items.add(item);
            int result = mailDao.setMailItems((int)mail2.getId(), (JSONArray)items);
            if (result == 1) {
                LOGGER.info("修改成功");
            } else {
                LOGGER.error("出了点意外，为什么捏？");
            }
        } else if (args[0].equals("del")) {
            if (args.length == 1) {
                LOGGER.error("用法: /mail " + name + " items del [int]<物品下标>");
                return;
            }
            int index = -1;
            try {
                index = Integer.parseInt((String)args[1]);
            }
            catch (Exception e) {
                LOGGER.error("解析下标时出错, 原文: " + args[2]);
                return;
            }
            JSONArray items = JSONArray.parseArray((String)mail2.getItems());
            if (index < 0 || index >= items.size()) {
                LOGGER.error("下标越界");
                return;
            }
            items.remove(index);
            int result = mailDao.setMailItems((int)mail2.getId(), (JSONArray)items);
            if (result == 1) {
                LOGGER.info("修改成功");
            } else {
                LOGGER.error("出了点意外，为什么捏？");
            }
        } else {
            LOGGER.error("可用的二级子命令: list<查看物品列表> add<添加物品> del<删除物品>");
        }
    }

    private void processSendCommand(ICommandSender sender, String name, List<Mail> mailList, String[] args) {
        if (mailList.size() != 1) {
            LOGGER.error("指定的邮件名不存在");
            return;
        }
        if (args.length < 2) {
            LOGGER.error("用法: /mail " + name + " send [string]<玩家UID|*> [int]<过期时长(天)>");
            return;
        }
        int expireTime = 0;
        try {
            expireTime = Integer.parseInt((String)args[1]);
        }
        catch (Exception e) {
            LOGGER.error("解析过期时长时出错, 原文: " + args[1]);
            return;
        }
        Long createAt = ArknightsApplication.getTimestamp();
        Long expireAt = createAt + 86400L * (long)expireTime;
        Mail mail2 = (Mail)mailList.get(0);
        JSONObject mailObject = new JSONObject(true);
        mailObject.put("mailId", mail2.getId());
        mailObject.put("createAt", createAt);
        mailObject.put("expireAt", expireAt);
        mailObject.put("state", 0);
        mailObject.put("type", 1);
        mailObject.put("hasItem", 1);
        if (!args[0].equals("*")) {
            long UID = 0L;
            try {
                UID = Long.parseLong((String)args[0]);
            }
            catch (Exception e) {
                LOGGER.error("解析 UID 时出错, 原文: " + args[0]);
                return;
            }
            List acounts = userDao.queryAccountByUid((long)UID);
            if (acounts.size() != 1) {
                LOGGER.error("无法找到该玩家");
                return;
            }
            Account account2 = (Account)acounts.get(0);
            JSONArray mailBox = JSONArray.parseArray((String)account2.getMails());
            if (!CommandMail.addMail((JSONArray)mailBox, (JSONObject)mailObject)) {
                LOGGER.error("玩家已拥有此邮件");
                return;
            }
            int result = userDao.setMailsData((Long)account2.getUid(), (JSONArray)mailBox);
            if (result == 1) {
                LOGGER.info("修改成功");
            } else {
                LOGGER.error("出了点意外，为什么捏？");
            }
        }
    }

    private static String joinToString(String[] args, String seperate) {
        if (args.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder(args[0]);
        for (int i = 1; i < args.length; ++i) {
            builder.append(seperate).append(args[i]);
        }
        return builder.toString();
    }

    private static boolean addMail(JSONArray mailList, JSONObject mail2) {
        for (Object object : mailList) {
            if (((JSONObject)object).getIntValue("mailId") != mail2.getIntValue("mailId")) continue;
            return false;
        }
        mailList.add(mail2);
        return true;
    }

    private static /* synthetic */ void lambda$processCommand$0(Mail mail2) {
        LOGGER.info("ID: " + mail2.getId() + ", 邮件名: " + mail2.getName() + ", 主题: " + mail2.getSubject());
    }
}

