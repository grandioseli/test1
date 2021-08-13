package com.example.mockapi.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.mockapi.domain.Mock;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonyou.cloud.middleware.PostMan;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;

import static com.example.mockapi.utils.FileReaderUtil.readFile;

@Controller
@RequestMapping("/yts/")
public class MockController {
    @Value("${yts.http.provider.url}")
    private String mockUrl;
    /**
     * 向指定配置文件请求打桩信息
     *
     * @param mock 实体类，主要需要的是其中的tenantId
     * @param msCode 微服务编码
     * @param version 配置文件版本
     * @param envId 环境id
     * @param file 配置文件名称
     * @return 返回实体类list即[mock,mock,……]
     * @throws IOException
     */
    @RequestMapping("getMock")
    @ResponseBody
    public Object getMock(Mock mock,String msCode, String version,String envId,String file) throws IOException {
        //根据参数获取文件内容(JSONObject)
        ObjectMapper mapper = new ObjectMapper();
        String url = mockUrl;
        String fileString = (String) readFile(url,mock,msCode,version,envId,file);
        JSONObject fileJSON;
        try {
            fileJSON = JSON.parseObject(fileString);
        } catch (JSONException i) {
            return "获取失败：配置文件中的数据不是json格式！";
        }
        //如果顶级目标中没有数据,需要重新创建json对象
        if (fileJSON == null) {
            System.out.println("文件为空，正在创建顶层节点");
            fileJSON = new JSONObject();
            String ytsMock = "yts.mock";
            JSONObject obj = new JSONObject();
            fileJSON.put(ytsMock, obj);
        }
        //如果有json数据但是顶层没有yts.mock节点，则添加yts.mock节点
        else if (!fileJSON.containsKey("yts.mock")) {
            System.out.println("顶层节点没有yts.mock");
            String ytsMock = "yts.mock";
            JSONObject obj = new JSONObject();
            fileJSON.put(ytsMock, obj);
        }
        JSONObject mockException = fileJSON.getJSONObject("yts.mock");
        //这里的做法是yts.mock中的每一项都是一个打桩数据（key:mockKey,value:打桩数据，仍然是一个json对象），将它的keyset提取出来并遍历
        //由于key可能是拼接而成的，因此不对外层的json做转化，只对内层的value做转化，将key进行手赋值
        Iterator<String> its = mockException.keySet().iterator();
        List<Mock> list = new ArrayList<>();
        while (its.hasNext()) {
            //获取到key，并将它赋给mock对象的id，注意这里迭代器已经指向下一个数据了
            String key = its.next();
            Mock mockobj;
            JSONObject temp = mockException.getJSONObject(key);
            String temp1 = temp.toJSONString();
            mock = mapper.readValue(temp1, Mock.class);//Json对象转为实体对象
            mock.setKey(key);
            mock.splitKey();
            list.add(mock);
        }
        return list;
    }

    /**
     * 向指定配置文件中写入数据
     * @param mock 实体类用于承接桩信息
     * @param msCode 微服务编码
     * @param version 配置文件版本
     * @param envId 环境id
     * @param file 配置文件名称
     * @return
     * @throws IOException
     */
    @RequestMapping("addMock")
    @ResponseBody
    public Object addMock(Mock mock,String msCode,String version,String envId,String file) throws IOException {
        //首先获取文件内容（JSONObject对象）
        String fileString = (String) readFile(mockUrl,mock,msCode,version,envId,file);
        JSONObject fileJSON;
        try {
            fileJSON = JSON.parseObject(fileString);
        } catch (JSONException i) {
            return "添加失败：配置文件中的数据不是json格式！";
        }
        //如果顶级目标中没有数据,需要重新创建json对象
        if (fileJSON == null) {
            System.out.println("文件为空，正在创建顶层节点");
            fileJSON = new JSONObject();
            String ytsMock = "yts.mock";
            JSONObject obj = new JSONObject();
            fileJSON.put(ytsMock, obj);
        }
        //如果有json数据但是顶层没有yts.mock节点，则添加yts.mock节点
        else if (!fileJSON.containsKey("yts.mock")) {
            System.out.println("顶层节点没有yts.mock");
            String ytsMock = "yts.mock";
            JSONObject obj = new JSONObject();
            fileJSON.put(ytsMock, obj);
        }
        String mockKey = "";
        //根据模式设定key并获取
        mock.setKeyBymode();
        mockKey = mock.getKey();
        //value也是JSONObject对象，读取由前端传入的各个值
        JSONObject mockException = new JSONObject();
        if (mock.getType() != null) {
            mockException.put("type", mock.getType());
        }
        if (mock.getMsg() != null) {
            mockException.put("msg", mock.getMsg());
        }
        if (mock.getPosition() != null) {
            mockException.put("position", mock.getPosition());
        }
        if (mock.getInvokePosition() != null) {
            mockException.put("invokePosition", mock.getInvokePosition());
        }
        if (mock.getTimeout() != null) {
            mockException.put("timeout", mock.getTimeout());
        }
        if (mock.getMode() != null) {
            mockException.put("mode", mock.getMode());
        }
        //原先的yts.mock的value
        JSONObject jsonObject = fileJSON.getJSONObject("yts.mock");
        //新增数据,到此就算把新的content组装好了
        jsonObject.put(mockKey, mockException);
        fileString = fileJSON.toJSONString();
        //下面组装请求的json
        JSONObject requestJson = new JSONObject();
        //微服务编码
        requestJson.put("serviceCode",msCode);
        //租户id
        requestJson.put("providerId",mock.getTenantId());
        JSONArray contentListArr = new JSONArray();
        JSONObject contentListObj = new JSONObject();
        contentListObj.put("version",version);
        contentListObj.put("envId",1);
        contentListObj.put("content",fileString);
        contentListObj.put("fileKey",file);
        contentListArr.add(contentListObj);
        requestJson.put("contentList",contentListArr);
        String requestString = requestJson.toJSONString();
        okhttp3.RequestBody body = okhttp3.RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestString);
        Request request = PostMan.getAuthedBuilder("tOkAcZqKXiwcrZwM", "s0DB2JrXWQwNn46nZetteqcxMr6WOr", "http://dc1.yms.app.yyuap.com//confcenter/api/v1/microservice/update")
                .post(body)
                .build();
        Call call = PostMan.getInstance().newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    /**
     * 根据key删除指定的打桩数据
     *
     * @param mock 实体类，需要的是里面的tenantId和key
     * @param msCode 微服务编码
     * @param version 配置文件版本
     * @param envId 环境id
     * @param file 配置文件名称
     * @return
     * @throws IOException
     */
    @RequestMapping("removeMock")
    @ResponseBody
    public Object removeMock(Mock mock,String msCode,String version,String envId,String file) throws IOException {
        //首先获取文件内容（JSONObject对象）
        String fileString = (String) readFile(mockUrl,mock,msCode,version,envId,file);
        JSONObject fileJSON;
        try {
            fileJSON = JSON.parseObject(fileString);
        } catch (JSONException i) {
            return "删除失败：配置文件中的数据不是json格式！";
        }
        if (fileJSON == null) {
            return "删除失败：配置文件中没有数据";
        }
        if (!fileJSON.containsKey("yts.mock")) {
            return "删除失败：json数据中未找到yts.mock";
        }
        JSONObject mockException = fileJSON.getJSONObject("yts.mock");
        if (!mockException.containsKey(mock.getKey())) {
            return "删除失败：yts.mock中没有找到key";
        }
        mockException.remove(mock.getKey());
        fileString = fileJSON.toJSONString();
        //下面组装请求的json
        JSONObject requestJson = new JSONObject();
        //微服务编码
        requestJson.put("serviceCode",msCode);
        //租户id
        requestJson.put("providerId",mock.getTenantId());
        JSONArray contentListArr = new JSONArray();
        JSONObject contentListObj = new JSONObject();
        contentListObj.put("version",version);
        contentListObj.put("envId",1);
        contentListObj.put("content",fileString);
        contentListObj.put("fileKey",file);
        contentListArr.add(contentListObj);
        requestJson.put("contentList",contentListArr);
        String requestString = requestJson.toJSONString();
        okhttp3.RequestBody body = okhttp3.RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestString);
        Request request = PostMan.getAuthedBuilder("tOkAcZqKXiwcrZwM", "s0DB2JrXWQwNn46nZetteqcxMr6WOr", "http://dc1.yms.app.yyuap.com//confcenter/api/v1/microservice/update")
                .post(body)
                .build();
        Call call = PostMan.getInstance().newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    /**
     * 清空配置文件内容
     *
     * @param mock 实体类，需要的是里面的租户id
     * @param msCode  微服务编码
     * @param version  配置文件版本
     * @param envId  环境id
     * @param file  配置文件名称
     * @return
     * @throws IOException
     */
    @RequestMapping("clearMock")
    @ResponseBody
    public Object clearMock(Mock mock,String msCode,String version,String envId,String file) throws IOException {
        //下面组装请求的json
        JSONObject requestJson = new JSONObject();
        //微服务编码
        requestJson.put("serviceCode",msCode);
        //租户id
        requestJson.put("providerId",mock.getTenantId());
        JSONArray contentListArr = new JSONArray();
        JSONObject contentListObj = new JSONObject();
        contentListObj.put("version",version);
        contentListObj.put("envId",1);
        contentListObj.put("content","");
        contentListObj.put("fileKey",file);
        contentListArr.add(contentListObj);
        requestJson.put("contentList",contentListArr);
        String requestString = requestJson.toJSONString();
        okhttp3.RequestBody body = okhttp3.RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestString);
        Request request = PostMan.getAuthedBuilder("tOkAcZqKXiwcrZwM", "s0DB2JrXWQwNn46nZetteqcxMr6WOr", "http://dc1.yms.app.yyuap.com//confcenter/api/v1/microservice/update")
                .post(body)
                .build();
        Call call = PostMan.getInstance().newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    /**
     * 该方法用于向配置文件中添加任意形式的数据，测试用，测完删除
     *
     * @return
     * @throws IOException
     */
    @RequestMapping("addAnything")
    @ResponseBody
    public Object addAnything() throws IOException {
                String json = "{\n" +
                "    \"serviceCode\":\"rpc-provider-531\",\n" +
                "    \"providerId\": \"c87e2267-1001-4c70-bb2a-ab41f3b81aa3\",\n" +
                "    \"contentList\":[\n" +
                "        {\n" +
                "            \"version\":\"1.0.0\",\n" +
                "            \"envId\": 1,\n" +
                "            \"content\": \"\",\n" +
                "            \"fileKey\": \"mwclient.json\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        JSONObject kk = new JSONObject();
        kk.put("123",123);
        String kkk = kk.toJSONString();
        JSONObject temp = JSON.parseObject(json);
        JSONArray temp1 =temp.getJSONArray("contentList");
        temp1.getJSONObject(0).put("content",kkk);
        String json1 = temp.toJSONString();
        System.out.println(json1);
        okhttp3.RequestBody body = okhttp3.RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json1);
        Request request = PostMan.getAuthedBuilder("tOkAcZqKXiwcrZwM", "s0DB2JrXWQwNn46nZetteqcxMr6WOr", "http://dc1.yms.app.yyuap.com//confcenter/api/v1/microservice/update")
                .post(body)
                .build();
        Call call = PostMan.getInstance().newCall(request);
        Response response = call.execute();
        return response.body().string();
    }
}