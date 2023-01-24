/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSONArray
 *  com.alibaba.fastjson.JSONObject
 *  com.hypergryph.arknights.ArknightsApplication
 *  com.hypergryph.arknights.core.dao.userDao
 *  com.hypergryph.arknights.core.pojo.Account
 *  com.hypergryph.arknights.game.building
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Date
 *  java.util.List
 *  java.util.Map$Entry
 *  javax.servlet.http.HttpServletRequest
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/building"})
public class building {
    @PostMapping(value={"/sync"}, produces={"application/json;charset=UTF-8"})
    public JSONObject Sync(@RequestHeader(value="secret") String secret, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /building/sync");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
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
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        modified.put("building", UserSyncData.getJSONObject("building"));
        modified.put("event", UserSyncData.getJSONObject("event"));
        JSONObject result = new JSONObject(true);
        result.put("ts", ArknightsApplication.getTimestamp());
        playerDataDelta.put("modified", modified);
        playerDataDelta.put("deleted", new JSONObject(true));
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/getInfoShareVisitorsNum"}, produces={"application/json;charset=UTF-8"})
    public JSONObject getInfoShareVisitorsNum() {
        JSONObject result = new JSONObject(true);
        result.put("num", 0);
        return result;
    }

    @PostMapping(value={"/getRecentVisitors"}, produces={"application/json;charset=UTF-8"})
    public JSONObject getRecentVisitors() {
        JSONObject result = new JSONObject(true);
        result.put("getRecentVisitors", new JSONArray());
        return result;
    }

    @PostMapping(value={"/assignChar"}, produces={"application/json;charset=UTF-8"})
    public JSONObject assignChar(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /building/assignChar");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        String roomSlotId = JsonBody.getString("roomSlotId");
        JSONArray charInstIdList = JsonBody.getJSONArray("charInstIdList");
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
        JSONObject roomSlots = UserSyncData.getJSONObject("building").getJSONObject("roomSlots");
        for (Map.Entry entry : roomSlots.entrySet()) {
            JSONArray roomCharInstIds = roomSlots.getJSONObject(entry.getKey().toString()).getJSONArray("charInstIds");
            for (int i = 0; i < roomCharInstIds.size(); ++i) {
                for (int n = 0; n < charInstIdList.size(); ++n) {
                    if (charInstIdList.get(n) != roomCharInstIds.get(i)) continue;
                    roomCharInstIds.set(i, -1);
                }
            }
        }
        UserSyncData.getJSONObject("building").getJSONObject("roomSlots").getJSONObject(roomSlotId).put("charInstIds", charInstIdList);
        if (roomSlotId.equals("slot_13")) {
            int trainer = charInstIdList.getIntValue(0);
            int trainee = charInstIdList.getIntValue(1);
            UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject(roomSlotId).getJSONObject("trainee").put("charInstId", trainee);
            UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject(roomSlotId).getJSONObject("trainee").put("targetSkill", -1);
            UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject(roomSlotId).getJSONObject("trainee").put("speed", 1000);
            UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject(roomSlotId).getJSONObject("trainer").put("charInstId", trainer);
            if (trainee == -1) {
                UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject(roomSlotId).getJSONObject("trainee").put("state", 0);
            } else {
                UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject(roomSlotId).getJSONObject("trainee").put("state", 3);
            }
            if (trainer == -1) {
                UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject(roomSlotId).getJSONObject("trainer").put("state", 0);
            } else {
                UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject(roomSlotId).getJSONObject("trainer").put("state", 3);
            }
        }
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        modified.put("building", UserSyncData.getJSONObject("building"));
        modified.put("event", UserSyncData.getJSONObject("event"));
        JSONObject result = new JSONObject(true);
        playerDataDelta.put("modified", modified);
        playerDataDelta.put("deleted", new JSONObject(true));
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/changeDiySolution"}, produces={"application/json;charset=UTF-8"})
    public JSONObject changeDiySolution(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /building/changeDiySolution");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        String roomSlotId = JsonBody.getString("roomSlotId");
        JSONObject solution = JsonBody.getJSONObject("solution");
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
        UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("DORMITORY").getJSONObject(roomSlotId).put("diySolution", solution);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        modified.put("building", UserSyncData.getJSONObject("building"));
        modified.put("event", UserSyncData.getJSONObject("event"));
        JSONObject result = new JSONObject(true);
        playerDataDelta.put("modified", modified);
        playerDataDelta.put("deleted", new JSONObject(true));
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    public String getFormulaId(String formulaId) {
        if (formulaId.equals("1")) {
            return "2001";
        }
        if (formulaId.equals("2")) {
            return "2002";
        }
        if (formulaId.equals("3")) {
            return "2003";
        }
        if (formulaId.equals("4")) {
            return "3003";
        }
        if (formulaId.equals("5")) {
            return "3213";
        }
        if (formulaId.equals("6")) {
            return "3223";
        }
        if (formulaId.equals("7")) {
            return "3233";
        }
        if (formulaId.equals("8")) {
            return "3243";
        }
        if (formulaId.equals("9")) {
            return "3253";
        }
        if (formulaId.equals("10")) {
            return "3263";
        }
        if (formulaId.equals("11")) {
            return "3273";
        }
        if (formulaId.equals("12")) {
            return "3283";
        }
        if (formulaId.equals("13")) {
            return "3141";
        }
        if (formulaId.equals("14")) {
            return "3141";
        }
        return null;
    }

    @PostMapping(value={"/changeManufactureSolution"}, produces={"application/json;charset=UTF-8"})
    public JSONObject changeManufactureSolution(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /building/changeManufactureSolution");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        String roomSlotId = JsonBody.getString("roomSlotId");
        String targetFormulaId = JsonBody.getString("targetFormulaId");
        int solutionCount = JsonBody.getIntValue("solutionCount");
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
        int outputSolutionCnt = UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).getIntValue("outputSolutionCnt");
        String FormulaId = UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).getString("formulaId");
        if (outputSolutionCnt != 0) {
            String itemId;
            if (Integer.valueOf((String)FormulaId) >= 5 && Integer.valueOf((String)FormulaId) <= 12) {
                itemId = null;
                if (Integer.valueOf((String)FormulaId) == 5) {
                    itemId = "3212";
                }
                if (Integer.valueOf((String)FormulaId) == 6) {
                    itemId = "3222";
                }
                if (Integer.valueOf((String)FormulaId) == 7) {
                    itemId = "3232";
                }
                if (Integer.valueOf((String)FormulaId) == 8) {
                    itemId = "3242";
                }
                if (Integer.valueOf((String)FormulaId) == 9) {
                    itemId = "3252";
                }
                if (Integer.valueOf((String)FormulaId) == 10) {
                    itemId = "3262";
                }
                if (Integer.valueOf((String)FormulaId) == 11) {
                    itemId = "3272";
                }
                if (Integer.valueOf((String)FormulaId) == 12) {
                    itemId = "3282";
                }
                UserSyncData.getJSONObject("inventory").put(this.getFormulaId(FormulaId), (UserSyncData.getJSONObject("inventory").getIntValue(this.getFormulaId(FormulaId)) + outputSolutionCnt));
                UserSyncData.getJSONObject("inventory").put(itemId, (UserSyncData.getJSONObject("inventory").getIntValue(itemId) - 2 * outputSolutionCnt));
                UserSyncData.getJSONObject("inventory").put("32001", (UserSyncData.getJSONObject("inventory").getIntValue("32001") - 1 * outputSolutionCnt));
            } else if (Integer.valueOf((String)FormulaId) > 12) {
                itemId = null;
                if (Integer.valueOf((String)FormulaId) == 13) {
                    itemId = "30012";
                    UserSyncData.getJSONObject("status").put("gold", (UserSyncData.getJSONObject("status").getIntValue("gold") - 1600 * outputSolutionCnt));
                }
                if (Integer.valueOf((String)FormulaId) == 14) {
                    itemId = "30062";
                    UserSyncData.getJSONObject("status").put("gold", (UserSyncData.getJSONObject("status").getIntValue("gold") - 1000 * outputSolutionCnt));
                }
                UserSyncData.getJSONObject("inventory").put(this.getFormulaId(FormulaId), (UserSyncData.getJSONObject("inventory").getIntValue(this.getFormulaId(FormulaId)) + outputSolutionCnt));
                UserSyncData.getJSONObject("inventory").put(itemId, (UserSyncData.getJSONObject("inventory").getIntValue(itemId) - 2 * outputSolutionCnt));
            } else {
                UserSyncData.getJSONObject("inventory").put(this.getFormulaId(FormulaId), (UserSyncData.getJSONObject("inventory").getIntValue(this.getFormulaId(FormulaId)) + outputSolutionCnt));
            }
        }
        UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("state", 1);
        UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("formulaId", targetFormulaId);
        UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("lastUpdateTime", (new Date().getTime() / 1000L));
        UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("completeWorkTime", -1);
        UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("remainSolutionCnt", 0);
        UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("outputSolutionCnt", solutionCount);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        modified.put("building", UserSyncData.getJSONObject("building"));
        modified.put("event", UserSyncData.getJSONObject("event"));
        modified.put("inventory", UserSyncData.getJSONObject("inventory"));
        modified.put("status", UserSyncData.getJSONObject("status"));
        JSONObject result = new JSONObject(true);
        playerDataDelta.put("modified", modified);
        playerDataDelta.put("deleted", new JSONObject(true));
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/settleManufacture"}, produces={"application/json;charset=UTF-8"})
    public JSONObject settleManufacture(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /building/settleManufacture");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        JSONArray roomSlotIdList = JsonBody.getJSONArray("roomSlotIdList");
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
        for (int i = 0; i < roomSlotIdList.size(); ++i) {
            String roomSlotId = roomSlotIdList.getString(i);
            int outputSolutionCnt = UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).getIntValue("outputSolutionCnt");
            String FormulaId = UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).getString("formulaId");
            if (outputSolutionCnt != 0) {
                String itemId;
                if (Integer.valueOf((String)FormulaId) >= 5 && Integer.valueOf((String)FormulaId) <= 12) {
                    itemId = null;
                    if (Integer.valueOf((String)FormulaId) == 5) {
                        itemId = "3212";
                    }
                    if (Integer.valueOf((String)FormulaId) == 6) {
                        itemId = "3222";
                    }
                    if (Integer.valueOf((String)FormulaId) == 7) {
                        itemId = "3232";
                    }
                    if (Integer.valueOf((String)FormulaId) == 8) {
                        itemId = "3242";
                    }
                    if (Integer.valueOf((String)FormulaId) == 9) {
                        itemId = "3252";
                    }
                    if (Integer.valueOf((String)FormulaId) == 10) {
                        itemId = "3262";
                    }
                    if (Integer.valueOf((String)FormulaId) == 11) {
                        itemId = "3272";
                    }
                    if (Integer.valueOf((String)FormulaId) == 12) {
                        itemId = "3282";
                    }
                    UserSyncData.getJSONObject("inventory").put(this.getFormulaId(FormulaId), (UserSyncData.getJSONObject("inventory").getIntValue(this.getFormulaId(FormulaId)) + outputSolutionCnt));
                    UserSyncData.getJSONObject("inventory").put(itemId, (UserSyncData.getJSONObject("inventory").getIntValue(itemId) - 2 * outputSolutionCnt));
                    UserSyncData.getJSONObject("inventory").put("32001", (UserSyncData.getJSONObject("inventory").getIntValue("32001") - 1 * outputSolutionCnt));
                } else if (Integer.valueOf((String)FormulaId) > 12) {
                    itemId = null;
                    if (Integer.valueOf((String)FormulaId) == 13) {
                        itemId = "30012";
                        UserSyncData.getJSONObject("status").put("gold", (UserSyncData.getJSONObject("status").getIntValue("gold") - 1600 * outputSolutionCnt));
                    }
                    if (Integer.valueOf((String)FormulaId) == 14) {
                        itemId = "30062";
                        UserSyncData.getJSONObject("status").put("gold", (UserSyncData.getJSONObject("status").getIntValue("gold") - 1000 * outputSolutionCnt));
                    }
                    UserSyncData.getJSONObject("inventory").put(this.getFormulaId(FormulaId), (UserSyncData.getJSONObject("inventory").getIntValue(this.getFormulaId(FormulaId)) + outputSolutionCnt));
                    UserSyncData.getJSONObject("inventory").put(itemId, (UserSyncData.getJSONObject("inventory").getIntValue(itemId) - 2 * outputSolutionCnt));
                } else {
                    UserSyncData.getJSONObject("inventory").put(this.getFormulaId(FormulaId), (UserSyncData.getJSONObject("inventory").getIntValue(this.getFormulaId(FormulaId)) + outputSolutionCnt));
                }
            }
            UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("state", 0);
            UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("formulaId", "");
            UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("lastUpdateTime", (new Date().getTime() / 1000L));
            UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("completeWorkTime", -1);
            UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("remainSolutionCnt", 0);
            UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("MANUFACTURE").getJSONObject(roomSlotId).put("outputSolutionCnt", 0);
        }
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        modified.put("building", UserSyncData.getJSONObject("building"));
        modified.put("event", UserSyncData.getJSONObject("event"));
        modified.put("inventory", UserSyncData.getJSONObject("inventory"));
        modified.put("status", UserSyncData.getJSONObject("status"));
        JSONObject result = new JSONObject(true);
        playerDataDelta.put("modified", modified);
        playerDataDelta.put("deleted", new JSONObject(true));
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/workshopSynthesis"}, produces={"application/json;charset=UTF-8"})
    public JSONObject workshopSynthesis(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /building/workshopSynthesis");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        String formulaId = JsonBody.getString("formulaId");
        int workCount = JsonBody.getIntValue("times");
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
        JSONObject workshopFormulas = ArknightsApplication.buildingData.getJSONObject(formulaId);
        JSONArray costs = workshopFormulas.getJSONArray("costs");
        for (int i = 0; i < costs.size(); ++i) {
            String itemId = costs.getJSONObject(i).getString("id");
            int itemCount = costs.getJSONObject(i).getIntValue("count");
            UserSyncData.getJSONObject("inventory").put(itemId, (UserSyncData.getJSONObject("inventory").getIntValue(itemId) - itemCount * workCount));
        }
        UserSyncData.getJSONObject("inventory").put(workshopFormulas.getString("itemId"), (UserSyncData.getJSONObject("inventory").getIntValue(workshopFormulas.getString("itemId")) + workshopFormulas.getIntValue("count") * workCount));
        UserSyncData.getJSONObject("status").put("gold", (UserSyncData.getJSONObject("status").getIntValue("gold") - workshopFormulas.getIntValue("goldCost") * workCount));
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        modified.put("building", UserSyncData.getJSONObject("building"));
        modified.put("event", UserSyncData.getJSONObject("event"));
        modified.put("inventory", UserSyncData.getJSONObject("inventory"));
        modified.put("status", UserSyncData.getJSONObject("status"));
        JSONObject result = new JSONObject(true);
        JSONObject results = new JSONObject(true);
        results.put("type", "MATERIAL");
        results.put("id", workshopFormulas.getString("itemId"));
        results.put("count", workCount);
        playerDataDelta.put("modified", modified);
        playerDataDelta.put("deleted", new JSONObject(true));
        result.put("playerDataDelta", playerDataDelta);
        result.put("results", results);
        return result;
    }

    @PostMapping(value={"/upgradeSpecialization"}, produces={"application/json;charset=UTF-8"})
    public JSONObject upgradeSpecialization(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /building/upgradeSpecialization");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        int skillIndex = JsonBody.getIntValue("skillIndex");
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
        int charInstId = UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject("slot_13").getJSONObject("trainee").getIntValue("charInstId");
        String charId = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)charInstId)).getString("charId");
        JSONArray levelUpCost = ArknightsApplication.characterJson.getJSONObject(charId).getJSONArray("skills").getJSONObject(skillIndex).getJSONArray("levelUpCostCond").getJSONObject(UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)charInstId)).getJSONArray("skills").getJSONObject(skillIndex).getIntValue("specializeLevel")).getJSONArray("levelUpCost");
        for (int i = 0; i < levelUpCost.size(); ++i) {
            String id = levelUpCost.getJSONObject(i).getString("id");
            int count = levelUpCost.getJSONObject(i).getIntValue("count");
            UserSyncData.getJSONObject("inventory").put(id, (UserSyncData.getJSONObject("inventory").getIntValue(id) - count));
        }
        UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject("slot_13").getJSONObject("trainee").put("state", 2);
        UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject("slot_13").getJSONObject("trainee").put("targetSkill", skillIndex);
        UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject("slot_13").getJSONObject("trainer").put("state", 2);
        UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject("slot_13").put("lastUpdateTime", (new Date().getTime() / 1000L));
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        JSONObject troop = new JSONObject(true);
        JSONObject chars = new JSONObject(true);
        chars.put(String.valueOf((int)charInstId), UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)charInstId)));
        troop.put("chars", chars);
        modified.put("building", UserSyncData.getJSONObject("building"));
        modified.put("event", UserSyncData.getJSONObject("event"));
        modified.put("troop", troop);
        modified.put("inventory", UserSyncData.getJSONObject("inventory"));
        JSONObject result = new JSONObject(true);
        playerDataDelta.put("modified", modified);
        playerDataDelta.put("deleted", new JSONObject(true));
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/completeUpgradeSpecialization"}, produces={"application/json;charset=UTF-8"})
    public JSONObject completeUpgradeSpecialization(@RequestHeader(value="secret") String secret, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /building/completeUpgradeSpecialization");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
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
        int charInstId = UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject("slot_13").getJSONObject("trainee").getIntValue("charInstId");
        int targetSkill = UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject("slot_13").getJSONObject("trainee").getIntValue("targetSkill");
        int specializeLevel = UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)charInstId)).getJSONArray("skills").getJSONObject(targetSkill).getIntValue("specializeLevel");
        UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)charInstId)).getJSONArray("skills").getJSONObject(targetSkill).put("specializeLevel", (specializeLevel + 1));
        UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject("slot_13").getJSONObject("trainee").put("state", 3);
        UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject("slot_13").getJSONObject("trainee").put("targetSkill", -1);
        UserSyncData.getJSONObject("building").getJSONObject("rooms").getJSONObject("TRAINING").getJSONObject("slot_13").getJSONObject("trainer").put("state", 3);
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        JSONObject troop = new JSONObject(true);
        JSONObject chars = new JSONObject(true);
        chars.put(String.valueOf((int)charInstId), UserSyncData.getJSONObject("troop").getJSONObject("chars").getJSONObject(String.valueOf((int)charInstId)));
        troop.put("chars", chars);
        modified.put("building", UserSyncData.getJSONObject("building"));
        modified.put("event", UserSyncData.getJSONObject("event"));
        modified.put("troop", troop);
        modified.put("inventory", UserSyncData.getJSONObject("inventory"));
        JSONObject result = new JSONObject(true);
        playerDataDelta.put("modified", modified);
        playerDataDelta.put("deleted", new JSONObject(true));
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/deliveryOrder"}, produces={"application/json;charset=UTF-8"})
    public JSONObject deliveryOrder(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /building/deliveryOrder");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        String slotId = JsonBody.getString("slotId");
        int orderId = JsonBody.getIntValue("orderId");
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
        if (slotId.equals("slot_24")) {
            UserSyncData.getJSONObject("inventory").put("3003", (UserSyncData.getJSONObject("inventory").getIntValue("3003") - 2));
            UserSyncData.getJSONObject("status").put("gold", (UserSyncData.getJSONObject("status").getIntValue("gold") + 1000));
        }
        if (slotId.equals("slot_14")) {
            UserSyncData.getJSONObject("inventory").put("3003", (UserSyncData.getJSONObject("inventory").getIntValue("3003") - 4));
            UserSyncData.getJSONObject("status").put("gold", (UserSyncData.getJSONObject("status").getIntValue("gold") + 2000));
        }
        if (slotId.equals("slot_5")) {
            UserSyncData.getJSONObject("inventory").put("3003", (UserSyncData.getJSONObject("inventory").getIntValue("3003") - 6));
            UserSyncData.getJSONObject("status").put("gold", (UserSyncData.getJSONObject("status").getIntValue("gold") + 3000));
        }
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        modified.put("building", UserSyncData.getJSONObject("building"));
        modified.put("inventory", UserSyncData.getJSONObject("inventory"));
        modified.put("status", UserSyncData.getJSONObject("status"));
        JSONObject result = new JSONObject(true);
        playerDataDelta.put("modified", modified);
        playerDataDelta.put("deleted", new JSONObject(true));
        result.put("playerDataDelta", playerDataDelta);
        return result;
    }

    @PostMapping(value={"/deliveryBatchOrder"}, produces={"application/json;charset=UTF-8"})
    public JSONObject deliveryBatchOrder(@RequestHeader(value="secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        ArknightsApplication.LOGGER.info("[/" + clientIp + "] /building/deliveryBatchOrder");
        if (!ArknightsApplication.enableServer) {
            response.setStatus(400);
            JSONObject result = new JSONObject(true);
            result.put("statusCode", 400);
            result.put("error", "Bad Request");
            result.put("message", "server is close");
            return result;
        }
        JSONArray slotList = JsonBody.getJSONArray("slotList");
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
        JSONObject delivered = new JSONObject(true);
        for (int i = 0; i < slotList.size(); ++i) {
            String slotId = slotList.getString(i);
            int count = 0;
            if (slotId.equals("slot_24")) {
                count = 1000;
                UserSyncData.getJSONObject("inventory").put("3003", (UserSyncData.getJSONObject("inventory").getIntValue("3003") - 2));
                UserSyncData.getJSONObject("status").put("gold", (UserSyncData.getJSONObject("status").getIntValue("gold") + count));
            }
            if (slotId.equals("slot_14")) {
                count = 2000;
                UserSyncData.getJSONObject("inventory").put("3003", (UserSyncData.getJSONObject("inventory").getIntValue("3003") - 4));
                UserSyncData.getJSONObject("status").put("gold", (UserSyncData.getJSONObject("status").getIntValue("gold") + count));
            }
            if (slotId.equals("slot_5")) {
                count = 3000;
                UserSyncData.getJSONObject("inventory").put("3003", (UserSyncData.getJSONObject("inventory").getIntValue("3003") - 6));
                UserSyncData.getJSONObject("status").put("gold", (UserSyncData.getJSONObject("status").getIntValue("gold") + count));
            }
            JSONArray itemGet = new JSONArray();
            JSONObject GOLD = new JSONObject(true);
            GOLD.put("type", "GOLD");
            GOLD.put("id", "4001");
            GOLD.put("count", count);
            itemGet.add(GOLD);
            delivered.put(slotId, itemGet);
        }
        userDao.setUserData((Long)uid, (JSONObject)UserSyncData);
        JSONObject playerDataDelta = new JSONObject(true);
        JSONObject modified = new JSONObject(true);
        modified.put("inventory", UserSyncData.getJSONObject("inventory"));
        modified.put("status", UserSyncData.getJSONObject("status"));
        JSONObject result = new JSONObject(true);
        playerDataDelta.put("modified", modified);
        playerDataDelta.put("deleted", new JSONObject(true));
        result.put("playerDataDelta", playerDataDelta);
        result.put("delivered", delivered);
        return result;
    }
}

