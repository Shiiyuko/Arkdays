/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONArray
 *  com.alibaba.fastjson.JSONObject
 *  com.hypergryph.arknights.ArknightsApplication
 *  com.hypergryph.arknights.core.dao.userDao
 *  com.hypergryph.arknights.core.pojo.Account
 *  com.hypergryph.arknights.core.pojo.SearchUidList
 *  com.hypergryph.arknights.core.pojo.UserInfo
 *  com.hypergryph.arknights.game.social
 *  java.lang.Boolean
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestHeader
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.hypergryph.arknights.game;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.pojo.Account;
import com.hypergryph.arknights.core.pojo.SearchUidList;
import com.hypergryph.arknights.core.pojo.UserInfo;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/social"})
public class social {
    public JSONObject teamV2 = JSONObject.parseObject((String)"{\"abyssal\":0,\"action4\":0,\"blacksteel\":0,\"bolivar\":0,\"chiave\":0,\"columbia\":0,\"egir\":0,\"followers\":0,\"glasgow\":0,\"higashi\":0,\"iberia\":0,\"karlan\":0,\"kazimierz\":0,\"kjerag\":0,\"laterano\":0,\"lee\":0,\"leithanien\":0,\"lgd\":0,\"lungmen\":0,\"minos\":0,\"penguin\":0,\"reserve1\":0,\"reserve4\":0,\"reserve6\":0,\"rhine\":0,\"rhodes\":0,\"rim\":0,\"sami\":0,\"sargon\":0,\"siesta\":0,\"siracusa\":0,\"student\":0,\"sui\":0,\"sweep\":0,\"ursus\":0,\"victoria\":0,\"yan\":0}\n");

    @PostMapping(value={"/setAssistCharList"}, produces={"application/json;charset=UTF-8"})
    public JSONObject setAssistCharList(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response) {
        JSONArray assistCharList = JsonBody.getJSONArray("assistCharList");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        JSONObject assistChar = new JSONObject();
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        UserSyncData.getJSONObject("social").put("assistCharList", assistCharList);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        for (int i = 0; i < assistCharList.size(); ++i) {
            if (assistCharList.getJSONObject(i) == null) continue;
            JSONObject charInfo = assistCharList.getJSONObject(i);
            String charInstId = charInfo.getString("charInstId");
            String charId = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(charInstId).getString("charId");
            charInfo.put("charId", charId);
            String profession = ArknightsApplication.characterJson.getJSONObject(charId).getString("profession");
            if (!assistChar.containsKey(profession)) {
                assistChar.put(profession, new JSONArray());
            }
            assistChar.getJSONArray(profession).add(charInfo);
        }
        userDao.setAssistCharListData((Long)uid, (JSONObject)assistChar);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        modified.put("social", UserSyncData.getJSONObject("social"));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/getSortListInfo"}, produces={"application/json;charset=UTF-8"})
    public JSONObject getSortListInfo(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response) {
        int type = JsonBody.getIntValue("type");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        JSONArray resultList = new JSONArray();
        if (type == 0) {
            String nickNumber = JsonBody.getJSONObject("param").getString("nickNumber");
            String nickName = JsonBody.getJSONObject("param").getString("nickName");
            List search = userDao.searchPlayer((String)("%" + nickName + "%"), (String)("%" + nickNumber + "%"));
            for (int i = 0; i < search.size(); ++i) {
                if (((SearchUidList)search.get(i)).getUid() == uid.longValue()) continue;
                JSONObject FriendInfo = new JSONObject(true);
                FriendInfo.put("level", ((SearchUidList)search.get(i)).getLevel());
                FriendInfo.put("uid", ((SearchUidList)search.get(i)).getUid());
                resultList.add(FriendInfo);
            }
        }
        if (type == 1) {
            JSONArray FriendList = JSONObject.parseObject((String)((Account)Accounts.get(0)).getFriend()).getJSONArray("list");
            for (int i = 0; i < FriendList.size(); ++i) {
                int FriendUid = FriendList.getJSONObject(i).getIntValue("uid");
                List userInfo = userDao.queryUserInfo((long)FriendUid);
                JSONObject userStatus = JSONObject.parseObject((String)((UserInfo)userInfo.get(0)).getStatus());
                JSONObject FriendInfo = new JSONObject(true);
                FriendInfo.put("level", userStatus.getIntValue("level"));
                FriendInfo.put("infoShare", 0);
                FriendInfo.put("uid", FriendUid);
                resultList.add(FriendInfo);
            }
        }
        if (type == 2) {
            JSONArray FriendRequest;
            resultList = FriendRequest = JSONObject.parseObject((String)((Account)Accounts.get(0)).getFriend()).getJSONArray("request");
        }
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("result", resultList);
        return result;
    }

    @PostMapping(value={"/getFriendList"}, produces={"application/json;charset=UTF-8"})
    public JSONObject getFriendList(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response) {
        JSONArray idList = JsonBody.getJSONArray("idList");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        JSONArray friends = new JSONArray();
        JSONArray board = new JSONArray();
        JSONObject medalBoard = new JSONObject(true);
        medalBoard.put("type", "EMPTY");
        medalBoard.put("template", null);
        medalBoard.put("custom", null);
        JSONArray friendAlias = new JSONArray();
        for (int i = 0; i < idList.size(); ++i) {
            Long FriendUid = idList.getLongValue(i);
            List userInfo = userDao.queryUserInfo((long)FriendUid);
            JSONArray userAssistCharList = JSONArray.parseArray((String)((UserInfo)userInfo.get(0)).getSocialAssistCharList());
            JSONObject userStatus = JSONObject.parseObject((String)((UserInfo)userInfo.get(0)).getStatus());
            JSONObject chars = JSONObject.parseObject((String)((UserInfo)userInfo.get(0)).getChars());
            JSONObject UserFriend = JSONObject.parseObject((String)((Account)Accounts.get(0)).getFriend());
            JSONObject FriendInfo = new JSONObject(true);
            JSONArray assistCharList = new JSONArray();
            for (int n = 0; n < userAssistCharList.size(); ++n) {
                if (userAssistCharList.getJSONObject(n) != null) {
                    String charInstId = String.valueOf((int)userAssistCharList.getJSONObject(n).getIntValue("charInstId"));
                    JSONObject chardata = chars.getJSONObject(charInstId);
                    chardata.put("skillIndex", userAssistCharList.getJSONObject(n).getIntValue("skillIndex"));
                    assistCharList.add(chardata);
                    continue;
                }
                assistCharList.add(null);
            }
            FriendInfo.put("assistCharList", assistCharList);
            FriendInfo.put("avatarId", userStatus.getIntValue("avatarId"));
            FriendInfo.put("uid", FriendUid);
            FriendInfo.put("board", board);
            FriendInfo.put("medalBoard", medalBoard);
            FriendInfo.put("charCnt", chars.size());
            FriendInfo.put("friendNumLimit", 50);
            FriendInfo.put("furnCnt", 0);
            FriendInfo.put("infoShare", 0);
            FriendInfo.put("lastOnlineTime", userStatus.getIntValue("lastOnlineTs"));
            FriendInfo.put("level", userStatus.getIntValue("level"));
            FriendInfo.put("mainStageProgress", userStatus.getString("mainStageProgress"));
            FriendInfo.put("nickName", userStatus.getString("nickName"));
            FriendInfo.put("nickNumber", userStatus.getString("nickNumber"));
            FriendInfo.put("avatar", userStatus.getJSONObject("avatar"));
            FriendInfo.put("resume", userStatus.getString("resume"));
            FriendInfo.put("recentVisited", 0);
            FriendInfo.put("registerTs", userStatus.getIntValue("registerTs"));
            FriendInfo.put("secretary", userStatus.getString("secretary"));
            FriendInfo.put("secretarySkinId", userStatus.getString("secretarySkinId"));
            FriendInfo.put("serverName", "泰拉");
            FriendInfo.put("teamV2", this.teamV2);
            friends.add(FriendInfo);
            JSONArray FriendList = UserFriend.getJSONArray("list");
            for (int m = 0; m < FriendList.size(); ++m) {
                if ((long)FriendList.getJSONObject(m).getIntValue("uid") != FriendUid) continue;
                friendAlias.add(FriendList.getJSONObject(m).getString("alias"));
            }
        }
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("friends", friends);
        result.put("resultIdList", idList);
        result.put("friendAlias", friendAlias);
        return result;
    }

    @PostMapping(value={"/searchPlayer"}, produces={"application/json;charset=UTF-8"})
    public JSONObject searchPlayer(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response) {
        JSONArray idList = JsonBody.getJSONArray("idList");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        JSONArray friends = new JSONArray();
        JSONObject medalBoard = new JSONObject(true);
        medalBoard.put("type", "EMPTY");
        medalBoard.put("template", null);
        medalBoard.put("custom", null);
        JSONArray friendStatusList = new JSONArray();
        for (int i = 0; i < idList.size(); ++i) {
            int n;
            long FriendUid = idList.getLongValue(i);
            List userInfo = userDao.queryUserInfo((long)FriendUid);
            JSONArray userAssistCharList = JSONArray.parseArray((String)((UserInfo)userInfo.get(0)).getSocialAssistCharList());
            JSONObject userStatus = JSONObject.parseObject((String)((UserInfo)userInfo.get(0)).getStatus());
            JSONObject chars = JSONObject.parseObject((String)((UserInfo)userInfo.get(0)).getChars());
            JSONObject UserFriend = JSONObject.parseObject((String)((UserInfo)userInfo.get(0)).getFriend());
            JSONObject FriendInfo = new JSONObject(true);
            JSONArray assistCharList = new JSONArray();
            for (int n2 = 0; n2 < userAssistCharList.size(); ++n2) {
                if (userAssistCharList.getJSONObject(n2) != null) {
                    String charInstId = String.valueOf((int)userAssistCharList.getJSONObject(n2).getIntValue("charInstId"));
                    JSONObject chardata = chars.getJSONObject(charInstId);
                    chardata.put("skillIndex", userAssistCharList.getJSONObject(n2).getIntValue("skillIndex"));
                    assistCharList.add(chardata);
                    continue;
                }
                assistCharList.add(null);
            }
            FriendInfo.put("assistCharList", assistCharList);
            FriendInfo.put("avatarId", userStatus.getIntValue("avatarId"));
            FriendInfo.put("uid", FriendUid);
            FriendInfo.put("friendNumLimit", 999);
            FriendInfo.put("medalBoard", medalBoard);
            FriendInfo.put("lastOnlineTime", userStatus.getIntValue("lastOnlineTs"));
            FriendInfo.put("level", userStatus.getIntValue("level"));
            FriendInfo.put("nickName", userStatus.getString("nickName"));
            FriendInfo.put("nickNumber", userStatus.getString("nickNumber"));
            FriendInfo.put("avatar", userStatus.getJSONObject("avatar"));
            FriendInfo.put("resume", userStatus.getString("resume"));
            FriendInfo.put("serverName", "泰拉");
            friends.add(FriendInfo);
            JSONArray FriendRequest = UserFriend.getJSONArray("request");
            JSONArray FriendList = UserFriend.getJSONArray("list");
            Boolean isSet = false;
            for (n = 0; n < FriendList.size(); ++n) {
                if ((long)FriendList.getJSONObject(n).getIntValue("uid") != uid) continue;
                friendStatusList.add(2);
                isSet = true;
            }
            for (n = 0; n < FriendRequest.size(); ++n) {
                if ((long)FriendRequest.getJSONObject(n).getIntValue("uid") != uid) continue;
                friendStatusList.add(1);
                isSet = true;
            }
            if (isSet.booleanValue()) continue;
            friendStatusList.add(0);
        }
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("players", friends);
        result.put("resultIdList", idList);
        result.put("friendStatusList", friendStatusList);
        return result;
    }

    @PostMapping(value={"/getFriendRequestList"}, produces={"application/json;charset=UTF-8"})
    public JSONObject getFriendRequestList(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response) {
        JSONArray idList = JsonBody.getJSONArray("idList");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        JSONArray friends = new JSONArray();
        JSONArray board = new JSONArray();
        JSONObject medalBoard = new JSONObject(true);
        medalBoard.put("type", "EMPTY");
        medalBoard.put("template", null);
        medalBoard.put("custom", null);
        for (int i = 0; i < idList.size(); ++i) {
            long FriendUid = idList.getIntValue(i);
            List userInfo = userDao.queryUserInfo((long)FriendUid);
            JSONArray userAssistCharList = JSONArray.parseArray((String)((UserInfo)userInfo.get(0)).getSocialAssistCharList());
            JSONObject userStatus = JSONObject.parseObject((String)((UserInfo)userInfo.get(0)).getStatus());
            JSONObject chars = JSONObject.parseObject((String)((UserInfo)userInfo.get(0)).getChars());
            JSONObject UserFriend = JSONObject.parseObject((String)((UserInfo)userInfo.get(0)).getFriend());
            JSONObject FriendInfo = new JSONObject(true);
            JSONArray assistCharList = new JSONArray();
            for (int n = 0; n < userAssistCharList.size(); ++n) {
                if (userAssistCharList.getJSONObject(n) != null) {
                    String charInstId = userAssistCharList.getJSONObject(n).getString("charInstId");
                    JSONObject chardata = chars.getJSONObject(charInstId);
                    chardata.put("skillIndex", userAssistCharList.getJSONObject(n).getIntValue("skillIndex"));
                    assistCharList.add(chardata);
                    continue;
                }
                assistCharList.add(null);
            }
            FriendInfo.put("assistCharList", assistCharList);
            FriendInfo.put("avatarId", userStatus.getIntValue("avatarId"));
            FriendInfo.put("uid", FriendUid);
            FriendInfo.put("board", board);
            FriendInfo.put("medalBoard", medalBoard);
            FriendInfo.put("charCnt", chars.size());
            FriendInfo.put("friendNumLimit", 50);
            FriendInfo.put("furnCnt", 0);
            FriendInfo.put("infoShare", 0);
            FriendInfo.put("lastOnlineTime", userStatus.getIntValue("lastOnlineTs"));
            FriendInfo.put("level", userStatus.getIntValue("level"));
            FriendInfo.put("mainStageProgress", userStatus.getString("mainStageProgress"));
            FriendInfo.put("nickName", userStatus.getString("nickName"));
            FriendInfo.put("nickNumber", userStatus.getString("nickNumber"));
            FriendInfo.put("avatar", userStatus.getJSONObject("avatar"));
            FriendInfo.put("resume", userStatus.getString("resume"));
            FriendInfo.put("recentVisited", 0);
            FriendInfo.put("registerTs", userStatus.getIntValue("registerTs"));
            FriendInfo.put("secretary", userStatus.getString("secretary"));
            FriendInfo.put("secretarySkinId", userStatus.getString("secretarySkinId"));
            FriendInfo.put("serverName", "泰拉");
            FriendInfo.put("teamV2", this.teamV2);
            friends.add(FriendInfo);
        }
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("requestList", friends);
        result.put("resultIdList", idList);
        return result;
    }

    @PostMapping(value={"/processFriendRequest"}, produces={"application/json;charset=UTF-8"})
    public JSONObject processFriendRequest(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response) {
        int action = JsonBody.getIntValue("action");
        long friendId = JsonBody.getIntValue("friendId");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        JSONObject FriendJson = JSONObject.parseObject((String)((Account)Accounts.get(0)).getFriend());
        JSONArray FriendRequest = FriendJson.getJSONArray("request");
        JSONArray FriendList = FriendJson.getJSONArray("list");
        for (int i = 0; i < FriendRequest.size(); ++i) {
            if ((long)FriendRequest.getJSONObject(i).getIntValue("uid") != friendId) continue;
            FriendRequest.remove(i);
            FriendJson.put("request", FriendRequest);
            userDao.setFriendData((Long)uid, (JSONObject)FriendJson);
            if (action != 1) continue;
            JSONObject Friend = new JSONObject(true);
            Friend.put("uid", friendId);
            Friend.put("alias", null);
            FriendList.add(Friend);
            FriendJson.put("list", FriendList);
            userDao.setFriendData((Long)uid, (JSONObject)FriendJson);
        }
        if (action == 1) {
            List userInfo = userDao.queryUserInfo((long)friendId);
            JSONObject FJson = JSONObject.parseObject((String)((UserInfo)userInfo.get(0)).getFriend());
            JSONArray FList = FJson.getJSONArray("list");
            JSONObject Friend = new JSONObject(true);
            Friend.put("uid", uid);
            Friend.put("alias", null);
            FList.add(Friend);
            FJson.put("list", FList);
            userDao.setFriendData((Long)friendId, (JSONObject)FJson);
        }
        if (FriendRequest.size() == 0) {
            UserSyncData.getJSONObject("pushFlags").put("hasFriendRequest", 0);
        }
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        modified.put("pushFlags", UserSyncData.getJSONObject("pushFlags"));
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("friendNum", FriendList.size());
        result.put("result", 0);
        return result;
    }

    @PostMapping(value={"/sendFriendRequest"}, produces={"application/json;charset=UTF-8"})
    public JSONObject sendFriendRequest(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response) {
        int i;
        int afterBattle = JsonBody.getIntValue("afterBattle");
        long friendId = JsonBody.getIntValue("friendId");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        List userInfo = userDao.queryUserInfo((long)friendId);
        JSONObject FriendJson = JSONObject.parseObject((String)((UserInfo)userInfo.get(0)).getFriend());
        JSONArray FriendRequest = FriendJson.getJSONArray("request");
        JSONArray FriendList = FriendJson.getJSONArray("list");
        for (i = 0; i < FriendList.size(); ++i) {
            if ((long)FriendList.getJSONObject(i).getIntValue("uid") != uid) continue;
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "已添加该好友");
            return result;
        }
        for (i = 0; i < FriendRequest.size(); ++i) {
            if ((long)FriendRequest.getJSONObject(i).getIntValue("uid") != uid) continue;
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "已对该博士进行过好友申请");
            return result;
        }
        JSONObject Request = new JSONObject(true);
        Request.put("uid", uid);
        FriendRequest.add(Request);
        FriendJson.put("request", FriendRequest);
        userDao.setFriendData((Long)friendId, (JSONObject)FriendJson);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("result", 0);
        return result;
    }

    @PostMapping(value={"/setFriendAlias"}, produces={"application/json;charset=UTF-8"})
    public JSONObject setFriendAlias(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response) {
        String alias = JsonBody.getString("alias");
        int friendId = JsonBody.getIntValue("friendId");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        List userInfo = userDao.queryUserInfo((long)uid);
        JSONObject FriendJson = JSONObject.parseObject((String)((UserInfo)userInfo.get(0)).getFriend());
        JSONArray FriendList = FriendJson.getJSONArray("list");
        for (int i = 0; i < FriendList.size(); ++i) {
            if (FriendList.getJSONObject(i).getIntValue("uid") != friendId) continue;
            FriendList.getJSONObject(i).put("alias", alias);
        }
        FriendJson.put("list", FriendList);
        userDao.setFriendData((Long)uid, (JSONObject)FriendJson);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("result", 0);
        return result;
    }

    @PostMapping(value={"/deleteFriend"}, produces={"application/json;charset=UTF-8"})
    public JSONObject deleteFriend(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response) {
        long friendId = JsonBody.getIntValue("friendId");
        List Accounts = userDao.queryAccountBySecret((String)secret);
        if (Accounts.size() != 1) {
            JSONObject result = new JSONObject(true);
            result.put("result", 2);
            result.put("error", "无法查询到此账户");
            return result;
        }
        Long uid = ((Account)Accounts.get(0)).getUid();
        if (((Account)Accounts.get(0)).getBan() == 1L) {
            response.setStatus(500);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 403);
            result.put("error", "Bad Request");
            result.put("message", "error");
            return result;
        }
        JSONObject UserSyncData = JSONObject.parseObject((String)((Account)Accounts.get(0)).getUser());
        List userInfo = userDao.queryUserInfo((long)uid);
        JSONObject FriendJson = JSONObject.parseObject((String)((UserInfo)userInfo.get(0)).getFriend());
        JSONArray FriendList = FriendJson.getJSONArray("list");
        for (int i = 0; i < FriendList.size(); ++i) {
            if ((long)FriendList.getJSONObject(i).getIntValue("uid") != friendId) continue;
            FriendList.remove(i);
        }
        FriendJson.put("list", FriendList);
        userDao.setFriendData((Long)uid, (JSONObject)FriendJson);
        List UserFriend = userDao.queryUserInfo((long)friendId);
        FriendJson = JSONObject.parseObject((String)((UserInfo)UserFriend.get(0)).getFriend());
        FriendList = FriendJson.getJSONArray("list");
        for (int i = 0; i < FriendList.size(); ++i) {
            if ((long)FriendList.getJSONObject(i).getIntValue("uid") != uid) continue;
            FriendList.remove(i);
        }
        FriendJson.put("list", FriendList);
        userDao.setFriendData((Long)friendId, (JSONObject)FriendJson);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject result = new JSONObject(true);
        JSONObject playerDataDelta = new JSONObject(true);
        playerDataDelta.put("deleted", new JSONObject(true));
        JSONObject modified = new JSONObject(true);
        playerDataDelta.put("modified", modified);
        result.put("playerDataDelta", playerDataDelta);
        result.put("result", 0);
        return result;
    }
}

