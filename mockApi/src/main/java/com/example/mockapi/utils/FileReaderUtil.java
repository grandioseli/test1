package com.example.mockapi.utils;

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
    /**
     * 向指定微服务配置文件请求文件内容
     *
     * @param mockUrl 请求url
     * @param mock    实体类包含了租户id/微服务命名空间
     * @param msCode  微服务编码
     * @param version 配置文件版本
     * @param envId   环境编码
     * @param file    文件名称，分布式事务可指定mwclient.json
     * @return 返回JSON字符串
     * @throws IOException
     */
    public static Object readFile(String mockUrl, Mock mock, String msCode, String version, String envId, String file) throws IOException {
        String url = mockUrl + "?groupid=" + mock.getTenantId() + "&app=" + msCode + "&version=" + version + "&env=" + envId + "&key=" + file;
        Request request = PostMan.getAuthedBuilder("TvDTf0rUs0l5n8rA", "uIu0YdD4ZflTD5WYZQVALLfFp9SkQh", url)
                .get()
                .build();
        Call call = PostMan.getInstance().newCall(request);
        Response response = call.execute();
        return response.body().string();
    }
}
