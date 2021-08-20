package com.example.mockapi.utils;

import com.example.mockapi.domain.mock.Mock;
import com.yonyou.cloud.middleware.PostMan;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.*;

/**
 * 对文件的各个操作
 */
public class FileReader {
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
        Request request = PostMan.getAuthedBuilder("TvDTf0rUs0l5n8rA", "uIu0YdD4ZflTD5WYZQVALLfFp9SkQh", url)
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
     * @return
     * @throws IOException
     */
    public static String postFile(String bodyString, String url) throws IOException, NullPointerException {
        okhttp3.RequestBody body = okhttp3.RequestBody.create(MediaType.parse("application/json; charset=utf-8"), bodyString);
        Request request = PostMan.getAuthedBuilder("TvDTf0rUs0l5n8rA", "uIu0YdD4ZflTD5WYZQVALLfFp9SkQh", url)
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
     * @param env        环境编码
     * @return
     */
    public static String getCheckValid(String providerId, String msCode, String env) {
        if (providerId == null || providerId.trim().equals("") ||
                msCode == null || msCode.trim().equals("") ||
                env == null || env.trim().equals("")) {
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
     * @param env        环境编码
     * @return
     */
    public static String addCheckValid(Mock mock, String providerId, String msCode, String env) {
        if (mock.getMode() == null || mock.getMode().trim().equals("") ||
                providerId == null || providerId.trim().equals("") ||
                msCode == null || msCode.trim().equals("") ||
                env == null || env.trim().equals("")) {
            return "参数不能为空";
        }
        //如果是生产环境和沙箱环境就直接报错
        if (env.equals("online") || env.equals("sandbox") || env.equals("online-ap-sg1") || env.equals("online-cn-ecology")) {
            return "写入失败：该环境不允许修改";
        }
        if (mock.getMode().equals("mdd")) {
            if (mock.getTenantId() == null || mock.getTenantId().trim().equals("") ||
                    mock.getRuleId() == null || mock.getRuleId().trim().equals("") ||
                    mock.getAct() == null || mock.getAct().trim().equals("") ||
                    mock.getDocumentType() == null || mock.getDocumentType().trim().equals("")) {
                return "mdd模式，key的参数不足";
            }
        }
        if (mock.getMode().equals("http")) {
            if (mock.getHttpUrl() == null || mock.getHttpUrl().trim().equals("")) {
                return "http模式，key不能为空";
            }
        }
        if (mock.getMode().equals("iris")) {
            if (mock.getMethodName() == null || mock.getMethodName().trim().equals("") ||
                    mock.getPackageName() == null || mock.getPackageName().trim().equals("") ||
                    mock.getClassName() == null || mock.getClassName().trim().equals("")) {
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
     * @param env        环境编码
     * @return
     */
    public static String removeCheckValid(Mock mock, String providerId, String msCode, String env) {
        if (mock.getKey() == null || mock.getKey().trim().equals("") ||
                providerId == null || providerId.trim().equals("") ||
                msCode == null || msCode.trim().equals("") ||
                env == null || env.trim().equals("")) {
            return "参数不能为空";
        }
        //如果是生产环境和沙箱环境就直接报错
        if (env.equals("online") || env.equals("sandbox") || env.equals("online-ap-sg1") || env.equals("online-cn-ecology")) {
            return "删除失败：该环境不允许修改";
        }
        return "success";
    }

    public static String getProviderId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String providerId = "";
        for (Cookie cookie : cookies) {
            if ("providerId".equals(cookie.getName())) {
                try {
                    providerId = java.net.URLDecoder.decode(
                            cookie.getValue(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return providerId;
    }
}
