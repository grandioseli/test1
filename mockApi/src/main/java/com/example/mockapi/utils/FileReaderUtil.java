package com.example.mockapi.utils;

import com.example.mockapi.domain.mock.Mock;
import com.yonyou.cloud.middleware.PostMan;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;

import static com.example.mockapi.domain.ienum.AuthEnum.getAccessKeyByProviderId;
import static com.example.mockapi.domain.ienum.AuthEnum.getAccessSecretByProviderId;

/**
 * 对文件的各个操作
 */
public class FileReaderUtil {
    /**
     * 向指定微服务配置文件请求文件内容
     *
     * @param mockUrl    请求url
     * @param providerId 租户id/微服务命名空间
     * @param msCode     微服务编码
     * @param version    配置文件版本
     * @param env        环境编码
     * @param file       文件名称，分布式事务可指定mwclient.json
     * @return 返回JSON字符串
     * @throws IOException
     */
    public static Object readFile(String mockUrl, String providerId, String msCode, String version, String env, String file) throws NullPointerException, IOException {
        String url = mockUrl + "?groupid=" + providerId + "&app=" + msCode + "&version=" + version + "&env=" + env + "&key=" + file;
        String accessKey = getAccessKeyByProviderId(providerId);
        String accessSecret = getAccessSecretByProviderId(providerId);
        if (accessKey == null || accessSecret == null) {
            throw new NullPointerException("无效id");
        }
        Request request = PostMan.getAuthedBuilder(accessKey, accessSecret, url)
                .get()
                .build();
        Call call = PostMan.getInstance().newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    /**
     * post方法
     *
     * @param bodyString 请求的字符串
     * @param url        请求的url
     * @param providerId 微服务租户id
     * @return
     * @throws IOException
     */
    public static String postFile(String bodyString, String url, String providerId) throws IOException, NullPointerException {
        okhttp3.RequestBody body = okhttp3.RequestBody.create(MediaType.parse("application/json; charset=utf-8"), bodyString);
        String accessKey = getAccessKeyByProviderId(providerId);
        String accessSecret = getAccessSecretByProviderId(providerId);
        if (accessKey == null || accessSecret == null) {
            throw new NullPointerException("无效id");
        }
        Request request = PostMan.getAuthedBuilder(accessKey, accessSecret, url)
                .post(body)
                .build();
        Call call = PostMan.getInstance().newCall(request);
        Response response = call.execute();
        assert response.body() != null;
        return response.body().string();
    }

    /**
     * 检查获取参数完整性
     *
     * @param providerId 租户id/微服务命名空间
     * @param msCode     微服务编码
     * @param version    配置文件版本
     * @param env        环境编码
     * @param file       配置文件名
     * @return
     */
    public static String getCheckValid(String providerId, String msCode, String version, String env, String file) {
        if (providerId == null || msCode == null || version == null || env == null || file == null) {
            return "参数不能为空";
        }
        return "success";
    }

    /**
     * 检查增加参数完整性
     *
     * @param mock       实体类，包含了打桩信息
     * @param providerId 租户id/微服务命名空间
     * @param msCode     微服务编码
     * @param version    配置文件版本
     * @param env        环境编码
     * @param file       配置文件名
     * @return
     */
    public static String addCheckValid(Mock mock, String providerId, String msCode, String version, String env, String file) {
        if (mock.getMode() == null || providerId == null || msCode == null || version == null || env == null || file == null) {
            return "参数不能为空";
        }
        if (mock.getMode().equals("mdd")) {
            if (mock.getTenantId() == null || mock.getRuleId() == null || mock.getAct() == null || mock.getDocumentType() == null) {
                return "mdd模式，key的参数不足";
            }
        }
        if (mock.getMode().equals("http")) {
            if (mock.getHttpUrl() == null||mock.getAction()==null) {
                return "http模式，key不能为空";
            }
        }
        if (mock.getMode().equals("iris")) {
            if (mock.getMethodName() == null||mock.getParamTypeList()==null||mock.getPackageName()==null||mock.getClassName()==null) {
                return "iris模式，key不能为空";
            }
        }
        return "success";
    }

    /**
     * 检查删除参数完整性
     *
     * @param mock       实体类，包含了打桩信息，这里需要的仅仅是key
     * @param providerId 租户id/微服务命名空间
     * @param msCode     微服务编码
     * @param version    配置文件版本
     * @param env        环境编码
     * @param file       配置文件名
     * @return
     */
    public static String removeCheckValid(Mock mock, String providerId, String msCode, String version, String env, String file) {
        if (mock.getKey() == null || providerId == null || msCode == null || version == null || env == null || file == null) {
            return "参数不能为空";
        }
        return "success";
    }
}
