/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.http.HttpUtil
 *  com.alibaba.fastjson.JSONArray
 *  com.hypergryph.arknights.ArknightsApplication
 *  com.hypergryph.arknights.asset.official
 *  java.io.ByteArrayOutputStream
 *  java.io.File
 *  java.io.FileOutputStream
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.Boolean
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
 *  java.net.HttpURLConnection
 *  java.net.URL
 *  java.util.Date
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.springframework.core.io.FileSystemResource
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.MediaType
 *  org.springframework.http.ResponseEntity
 *  org.springframework.http.ResponseEntity$BodyBuilder
 *  org.springframework.web.bind.annotation.PathVariable
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.hypergryph.arknights.asset;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.hypergryph.arknights.ArknightsApplication;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Exception performing whole class analysis ignored.
 */
@RestController
@RequestMapping(value={"/assetbundle/official/{os}/assets"})
public class official {
    private static final Logger LOGGER = LogManager.getLogger();

    @RequestMapping(value={"/{assetsHash}/{fileName}"})
    public ResponseEntity<FileSystemResource> getFile(@PathVariable(value="os") String os, @PathVariable(value="assetsHash") String assetsHash, @PathVariable(value="fileName") String fileName, HttpServletResponse response, HttpServletRequest request) throws IOException {
        File file;
        String clientIp = ArknightsApplication.getIpAddr((HttpServletRequest)request);
        Boolean redirect = ArknightsApplication.serverConfig.getJSONObject("assets").getBooleanValue("enableRedirect");
        String redirectUrl = ArknightsApplication.serverConfig.getJSONObject("assets").getString("redirectUrl");
        String filePath = System.getProperty((String)"user.dir") + "/assets/" + assetsHash + "/direct/";
        if (redirect.booleanValue()) {
            filePath = System.getProperty((String)"user.dir") + "/assets/" + assetsHash + "/redirect/";
            JSONArray localFiles = ArknightsApplication.serverConfig.getJSONObject("assets").getJSONArray("localFiles");
            if (!localFiles.contains(fileName)) {
                response.sendRedirect(redirectUrl + "/" + fileName);
                return null;
            }
        }
        if ((file = new File(filePath, fileName)).exists()) {
            return this.export(file);
        }
        LOGGER.warn("正在下载 " + assetsHash + "/" + fileName);
        HttpUtil.downloadFile((String)(redirectUrl + "/" + fileName), (String)(filePath + fileName));
        file = new File(filePath, fileName);
        if (file.exists()) {
            LOGGER.info("[/" + clientIp + "] /" + assetsHash + "/" + fileName);
            return this.export(file);
        }
        return null;
    }

    public static String downLoadFromUrl(String urlStr, String fileName, String savePath) {
        try {
            File dir;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            InputStream inputStream = conn.getInputStream();
            byte[] getData = official.readInputStream((InputStream)inputStream);
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdir();
            }
            if (!(dir = new File(saveDir + File.separator)).exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);
            if (fos != null) {
                fos.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            return saveDir + File.separator + fileName;
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public ResponseEntity<FileSystemResource> export(File file) {
        if (file == null) {
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "attachment; filename=" + file.getName());
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Last-Modified", new Date().toString());
        headers.add("ETag", String.valueOf((long)System.currentTimeMillis()));
        return ((ResponseEntity.BodyBuilder)ResponseEntity.ok().headers(headers)).contentLength(file.length()).contentType(MediaType.parseMediaType((String)"application/octet-stream")).body(new FileSystemResource(file));
    }
}

