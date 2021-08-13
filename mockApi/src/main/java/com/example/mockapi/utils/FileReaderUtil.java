package com.example.mockapi.utils;

import com.alibaba.fastjson.JSONObject;
import com.example.mockapi.domain.Mock;
import com.yonyou.cloud.middleware.PostMan;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

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
    /**
     * 向指定微服务配置文件请求文件内容
     *
     * @param mock 实体类包含了租户id/微服务命名空间
     * @param msCode 微服务编码
     * @param version 配置文件版本
     * @param envId 环境编码
     * @param file 文件名称，分布式事务可指定mwclient.json
     * @return 返回JSON字符串
     * @throws IOException
     */
    public static Object readFile(String mockUrl, Mock mock, String msCode, String version, String envId, String file) throws IOException {
        String url = mockUrl + "?groupid="+mock.getTenantId()+"&app="+msCode+"&version="+version+"&env="+envId+"&key="+file;
//        "http://dc1.yms.app.yyuap.com/co" +
//                "nfcenter/api/config/file?groupid=c87e2267-1001-4c70-bb2a-ab41f3b81aa3&app=rpc-provider-531&version=1.0.0&env=dev&key=mwclient.json"
        Request request = PostMan.getAuthedBuilder("TvDTf0rUs0l5n8rA", "uIu0YdD4ZflTD5WYZQVALLfFp9SkQh", url)
                .get()
                .build();
        Call call = PostMan.getInstance().newCall(request);
        Response response = call.execute();
        return response.body().string();
    }
}
