/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSON
 *  com.alibaba.fastjson.JSONObject
 *  com.alibaba.fastjson.parser.Feature
 *  com.alibaba.fastjson.serializer.SerializerFeature
 *  com.hypergryph.arknights.core.file.IOTools
 *  java.io.File
 *  java.io.FileInputStream
 *  java.io.FileOutputStream
 *  java.io.FileReader
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.InputStreamReader
 *  java.io.OutputStream
 *  java.io.OutputStreamWriter
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuffer
 */
package com.hypergryph.arknights.core.file;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class IOTools {
    public static String ReadNormalFile(String FilePath) {
        try {
            int ReadChar;
            File jsonFile = new File(FilePath);
            FileReader fileReader = new FileReader(jsonFile);
            InputStreamReader reader = new InputStreamReader((InputStream)new FileInputStream(jsonFile), "UTF-8");
            StringBuffer Buffer = new StringBuffer();
            while ((ReadChar = reader.read()) != -1) {
                Buffer.append((char)ReadChar);
            }
            fileReader.close();
            reader.close();
            return Buffer.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject ReadJsonFile(String JsonFilePath) {
        try {
            int ReadChar;
            File jsonFile = new File(JsonFilePath);
            FileReader fileReader = new FileReader(jsonFile);
            InputStreamReader reader = new InputStreamReader((InputStream)new FileInputStream(jsonFile), "UTF-8");
            StringBuffer Buffer = new StringBuffer();
            while ((ReadChar = reader.read()) != -1) {
                Buffer.append((char)ReadChar);
            }
            fileReader.close();
            reader.close();
            return JSONObject.parseObject((String)Buffer.toString(), (Feature[])new Feature[]{Feature.OrderedField});
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean SaveJsonFile(String JsonFilePath, JSONObject JsonData) {
        try {
            OutputStreamWriter osw = new OutputStreamWriter((OutputStream)new FileOutputStream(JsonFilePath), "UTF-8");
            osw.write(JSON.toJSONString(JsonData, (SerializerFeature[])new SerializerFeature[]{SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue}));
            osw.flush();
            osw.close();
            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

