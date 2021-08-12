package com.example.mockapi.utils;

import com.alibaba.fastjson.JSONObject;

import java.io.*;

/**
 * 对文件的各个操作
 */
public class FileReaderUtil {
    //向指定文件内添加yts.mock节点
    public static void addYtsMock(File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file, true);
        OutputStreamWriter out = new OutputStreamWriter(fos, "utf-8");
        BufferedWriter bw = new BufferedWriter(out);
        JSONObject ytsMockObj = new JSONObject();
        String ytsMock = "yts.mock";
        JSONObject obj = new JSONObject();
        ytsMockObj.put(ytsMock, obj);
        String mockUrl = ytsMockObj.toJSONString();
        bw.write(mockUrl);
        bw.flush();
        bw.close();
    }
    //从指定文件中读取字符串
    public static String readStringFromFile(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        Reader reader = new InputStreamReader(new FileInputStream(file), "utf-8");
        int ch = 0;
        StringBuffer sb = new StringBuffer();
        while ((ch = reader.read()) != -1) {
            sb.append((char) ch);
        }
        fileReader.close();
        reader.close();
        return  sb.toString();
    }
    //清空文件内容
    public static void cleanUpFile(File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write("");
        fileWriter.flush();
        fileWriter.close();
    }
}
