package com.example.mockapi.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.mockapi.domain.mock.*;
import com.example.mockapi.domain.ienum.EnvEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

import static com.example.mockapi.utils.FileReader.*;

@Controller
@RequestMapping("/governance/yts/")
public class MockController {
    //查询接口
    @Value("${yts.http.query.url}")
    private String queryUrl;
    //更新接口
    @Value("${yts.http.update.url}")
    private String updateUrl;
    //accessKey
    @Value("${access.key}")
    private String accessKey;
    //accessSecret
    @Value("${access.secret}")
    private String accessSecret;

    /**
     * 向指定配置文件请求打桩信息
     *
     * @param request 用于租户id/微服务命名空间
     * @param msCode  微服务编码
     * @param version 配置文件版本
     * @param env     环境名称
     * @return 返回实体类list即[BaseMock, BaseMock,……]
     */
    @RequestMapping("getMock")
    @ResponseBody
    public Object getMock(HttpServletRequest request, String msCode, String version, String env) throws IOException {
        //从cookie中获取providerId
        String providerId = getProviderId(request);
        //根据参数获取文件内容(JSONObject)
        //检查完整性,version和file有默认值
        if (getCheckValid(providerId, msCode, env).equals("success")) {
            if (version == null || version.trim().equals("")) {
                version = "1.0.0";
            }
            String file = "mwclient.json";
            ObjectMapper mapper = new ObjectMapper();
            String fileString;
            //尝试读取文件
            try {
                fileString = (String) readFile(queryUrl, providerId, msCode, version, env, file, accessKey, accessSecret);
            } catch (NullPointerException e) {
                return "获取失败：无效的租户id";
            }
            String filePath = "/mwclient.json";
            JSONArray array = JSON.parseArray(fileString);
            for (int i = 0; i < array.size(); i++) {
                String path = array.getJSONObject(i).getString("path");
                if (path != null && path.equals(filePath)) {
                    fileString = array.getJSONObject(i).getString("content");
                }
            }
            JSONObject fileJSON;
            //查看是否是JSON格式
            try {
                fileJSON = JSON.parseObject(fileString);
            } catch (JSONException i) {
                return "获取失败：配置文件中的数据不是json格式！";
            }
            //如果顶级目标中没有数据,需要重新创建json对象
            if (fileJSON == null) {
                fileJSON = new JSONObject();
                String ytsMock = "yts.mock";
                JSONObject obj = new JSONObject();
                fileJSON.put(ytsMock, obj);
            }
            //防止权限错误
            else if (fileJSON.containsKey("error_message") && fileJSON.get("error_message").equals("该配置文件不是公开配置文件，不能被其他租户读取")) {
                return "获取失败:该配置文件不是公开配置文件，不能被其他租户读取";
            }
            //如果有json数据但是顶层没有yts.mock节点，则添加yts.mock节点
            else if (!fileJSON.containsKey("yts.mock")) {
                String ytsMock = "yts.mock";
                JSONObject obj = new JSONObject();
                fileJSON.put(ytsMock, obj);
            }
            JSONObject ytsMock = fileJSON.getJSONObject("yts.mock");
            //这里的做法是yts.mock中的每一项都是一个打桩数据（key:mockKey,value:mockException，仍然是一个json对象），将它的keyset提取出来并遍历
            Iterator<String> its = ytsMock.keySet().iterator();
            List<BaseMock> list = new ArrayList<>();
            while (its.hasNext()) {
                //获取到key，并将它赋给mock对象的id，注意这里迭代器已经指向下一个数据了
                String key = its.next();
                //读取每个key代表队mockException
                JSONObject mockException = ytsMock.getJSONObject(key);
                //读取mockException中的basicData（头部数据）
                JSONObject basicData = ytsMock.getJSONObject(key).getJSONObject("basicData");
                String sbasicData = basicData.toJSONString();
                //根据模式的不同选择不同的mock
                switch (mockException.getString("mode")) {
                    case "mdd": {
                        MddMock mock;
                        mock = mapper.readValue(sbasicData, MddMock.class);//Json对象转为实体对象
                        mock.setType(mockException.getString("type"));
                        mock.setMode(mockException.getString("mode"));
                        mock.setMsg(mockException.getString("msg"));
                        mock.setPosition(mockException.getString("position"));
                        mock.setInvokePosition(mockException.getString("invokePosition"));
                        mock.setTimeout(mockException.getInteger("timeout"));
                        mock.setKey(key);
                        list.add(mock);
                        break;
                    }
                    case "iris": {
                        IrisMock mock;
                        mock = mapper.readValue(sbasicData, IrisMock.class);//Json对象转为实体对象
                        mock.setType(mockException.getString("type"));
                        mock.setMode(mockException.getString("mode"));
                        mock.setMsg(mockException.getString("msg"));
                        mock.setPosition(mockException.getString("position"));
                        mock.setInvokePosition(mockException.getString("invokePosition"));
                        mock.setTimeout(mockException.getInteger("timeout"));
                        mock.setKey(key);
                        list.add(mock);
                        break;
                    }
                    case "http": {
                        HttpMock mock;
                        mock = mapper.readValue(sbasicData, HttpMock.class);//Json对象转为实体对象
                        mock.setType(mockException.getString("type"));
                        mock.setMode(mockException.getString("mode"));
                        mock.setMsg(mockException.getString("msg"));
                        mock.setPosition(mockException.getString("position"));
                        mock.setInvokePosition(mockException.getString("invokePosition"));
                        mock.setTimeout(mockException.getInteger("timeout"));
                        mock.setKey(key);
                        list.add(mock);
                        break;
                    }
                }
            }
            return list;
        } else {
            return getCheckValid(providerId, msCode, env);
        }
    }

    /**
     * 向指定配置文件通过mode区分打桩信息
     *
     * @param request 用于租户id/微服务命名空间
     * @param msCode  微服务编码
     * @param version 配置文件版本
     * @param env     环境名称
     * @param mode    打桩的类型
     * @return 返回实体类list即[BaseMock, BaseMock,……]
     */
    @RequestMapping("getMockByMode")
    @ResponseBody
    public Object getMockByMode(HttpServletRequest request, String msCode, String version, String env, String mode) throws IOException {
        //从cookie中获取providerId
        String providerId = getProviderId(request);
        //根据参数获取文件内容(JSONObject)
        //检查完整性,version和file有默认值
        if (mode == null || mode.trim().equals("")) {
            return "mode不能为空";
        }
        if (getCheckValid(providerId, msCode, env).equals("success")) {
            if (version == null || version.trim().equals("")) {
                version = "1.0.0";
            }
            String file = "mwclient.json";
            ObjectMapper mapper = new ObjectMapper();
            String fileString;
            //尝试读取文件
            try {
                fileString = (String) readFile(queryUrl, providerId, msCode, version, env, file, accessKey, accessSecret);
            } catch (NullPointerException e) {
                return "获取失败：无效的租户id";
            }
            String filePath = "/mwclient.json";
            JSONArray array = JSON.parseArray(fileString);
            for (int i = 0; i < array.size(); i++) {
                String path = array.getJSONObject(i).getString("path");
                if (path != null && path.equals(filePath)) {
                    fileString = array.getJSONObject(i).getString("content");
                }
            }
            JSONObject fileJSON;
            //查看是否是JSON格式
            try {
                fileJSON = JSON.parseObject(fileString);
            } catch (JSONException i) {
                return "获取失败：配置文件中的数据不是json格式！";
            }
            //如果顶级目标中没有数据,需要重新创建json对象
            if (fileJSON == null) {
                fileJSON = new JSONObject();
                String ytsMock = "yts.mock";
                JSONObject obj = new JSONObject();
                fileJSON.put(ytsMock, obj);
            }
            //防止权限错误
            else if (fileJSON.containsKey("error_message") && fileJSON.get("error_message").equals("该配置文件不是公开配置文件，不能被其他租户读取")) {
                return "获取失败:该配置文件不是公开配置文件，不能被其他租户读取";
            }
            //如果有json数据但是顶层没有yts.mock节点，则添加yts.mock节点
            else if (!fileJSON.containsKey("yts.mock")) {
                String ytsMock = "yts.mock";
                JSONObject obj = new JSONObject();
                fileJSON.put(ytsMock, obj);
            }
            JSONObject ytsMock = fileJSON.getJSONObject("yts.mock");
            //这里的做法是yts.mock中的每一项都是一个打桩数据（key:mockKey,value:mockException，仍然是一个json对象），将它的keyset提取出来并遍历
            Iterator<String> its = ytsMock.keySet().iterator();
            List<BaseMock> list = new ArrayList<>();
            while (its.hasNext()) {
                //获取到key，并将它赋给mock对象的id，注意这里迭代器已经指向下一个数据了
                String key = its.next();
                //读取每个key代表队mockException
                JSONObject mockException = ytsMock.getJSONObject(key);
                //读取mockException中的basicData（头部数据）
                JSONObject basicData = ytsMock.getJSONObject(key).getJSONObject("basicData");
                String sbasicData = basicData.toJSONString();
                //根据模式的不同选择不同的mock
                if (mode.equals(mockException.getString("mode"))) {
                    switch (mockException.getString("mode")) {
                        case "mdd": {
                            MddMock mock;
                            mock = mapper.readValue(sbasicData, MddMock.class);//Json对象转为实体对象
                            mock.setType(mockException.getString("type"));
                            mock.setMode(mockException.getString("mode"));
                            mock.setMsg(mockException.getString("msg"));
                            mock.setPosition(mockException.getString("position"));
                            mock.setInvokePosition(mockException.getString("invokePosition"));
                            mock.setTimeout(mockException.getInteger("timeout"));
                            mock.setKey(key);
                            list.add(mock);
                            break;
                        }
                        case "iris": {
                            IrisMock mock;
                            mock = mapper.readValue(sbasicData, IrisMock.class);//Json对象转为实体对象
                            mock.setType(mockException.getString("type"));
                            mock.setMode(mockException.getString("mode"));
                            mock.setMsg(mockException.getString("msg"));
                            mock.setPosition(mockException.getString("position"));
                            mock.setInvokePosition(mockException.getString("invokePosition"));
                            mock.setTimeout(mockException.getInteger("timeout"));
                            mock.setKey(key);
                            list.add(mock);
                            break;
                        }
                        case "http": {
                            HttpMock mock;
                            mock = mapper.readValue(sbasicData, HttpMock.class);//Json对象转为实体对象
                            mock.setType(mockException.getString("type"));
                            mock.setMode(mockException.getString("mode"));
                            mock.setMsg(mockException.getString("msg"));
                            mock.setPosition(mockException.getString("position"));
                            mock.setInvokePosition(mockException.getString("invokePosition"));
                            mock.setTimeout(mockException.getInteger("timeout"));
                            mock.setKey(key);
                            list.add(mock);
                            break;
                        }
                    }
                }
            }
            return list;
        } else {
            return getCheckValid(providerId, msCode, env);
        }
    }

    /**
     * 向指定配置文件中写入数据
     *
     * @return 返回添加信息
     */
    @RequestMapping("addMock")
    @ResponseBody
    public Object addMock(@RequestBody String mockjson, HttpServletRequest request) throws IOException {
        JSONObject mockobj = JSON.parseObject(mockjson);
        String msCode = mockobj.getString("msCode");
        String version = mockobj.getString("version");
        String env = mockobj.getString("env");
        Mock mock = new Mock();
        mock.setType(mockobj.getString("type"));
        mock.setMsg(mockobj.getString("msg"));
        mock.setPosition(mockobj.getString("position"));
        mock.setInvokePosition(mockobj.getString("invokePosition"));
        mock.setTimeout(mockobj.getInteger("timeout"));
        mock.setMode(mockobj.getString("mode"));
        mock.setKey(mockobj.getString("key"));
        mock.setTenantId(mockobj.getString("tenantId"));
        mock.setDocumentType(mockobj.getString("documentType"));
        mock.setAct(mockobj.getString("act"));
        mock.setRuleId(mockobj.getString("ruleId"));
        mock.setAction(mockobj.getString("action"));
        mock.setPackageName(mockobj.getString("packageName"));
        mock.setClassName(mockobj.getString("className"));
        mock.setMethodName(mockobj.getString("methodName"));
        mock.setParamTypeList((List<String>) mockobj.get("paramTypeList"));
        mock.setHttpUrl(mockobj.getString("httpUrl"));
        return addMock1(mock, request, msCode, version, env);
    }

    public Object addMock1(Mock mock, HttpServletRequest request, String msCode, String version, String env) throws IOException {
        String providerId = getProviderId(request);
        //首先获取文件内容（JSONObject对象）
        if (addCheckValid(mock, providerId, msCode, env).equals("success")) {
            if (version == null || version.trim().equals("")) {
                version = "1.0.0";
            }
            String file = "mwclient.json";
            String fileString;
            try {
                fileString = (String) readFile(queryUrl, providerId, msCode, version, env, file, accessKey, accessSecret);
            } catch (NullPointerException e) {
                return "写入失败：无效的租户id";
            }
            String filePath = "/mwclient.json";
            JSONArray array = JSON.parseArray(fileString);
            for (int i = 0; i < array.size(); i++) {
                String path = array.getJSONObject(i).getString("path");
                if (path != null && path.equals(filePath)) {
                    fileString = array.getJSONObject(i).getString("content");
                }
            }
            JSONObject fileJSON;
            try {
                fileJSON = JSON.parseObject(fileString);
            } catch (JSONException i) {
                return "写入失败：配置文件中的数据不是json格式！";
            }
            //如果顶级目标中没有数据,需要重新创建json对象
            if (fileJSON == null) {
                fileJSON = new JSONObject();
                String ytsMock = "yts.mock";
                JSONObject obj = new JSONObject();
                fileJSON.put(ytsMock, obj);
            } else if (fileJSON.containsKey("error_message") && fileJSON.get("error_message").equals("该配置文件不是公开配置文件，不能被其他租户读取")) {
                return "写入失败:该配置文件不是公开配置文件，不能被其他租户读取";
            }
            //如果有json数据但是顶层没有yts.mock节点，则添加yts.mock节点
            else if (!fileJSON.containsKey("yts.mock")) {
                String ytsMock = "yts.mock";
                JSONObject obj = new JSONObject();
                fileJSON.put(ytsMock, obj);
            }
            String mockKey;
            //下面向yts.mock节点增加数据
            //根据模式设定key并获取
            mock.setKeyByMode();
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
            //如果是mdd模式
            assert mock.getMode() != null;
            switch (mock.getMode()) {
                case "mdd": {
                    JSONObject temp = new JSONObject();
                    temp.put("tenantId", mock.getTenantId());
                    temp.put("documentType", mock.getDocumentType());
                    temp.put("act", mock.getAct());
                    temp.put("ruleId", mock.getRuleId());
                    temp.put("action", mock.getAction());
                    mockException.put("basicData", temp);
                    break;
                }
                case "iris": {
                    JSONObject temp = new JSONObject();
                    temp.put("packageName", mock.getPackageName());
                    temp.put("className", mock.getClassName());
                    temp.put("methodName", mock.getMethodName());
                    temp.put("paramTypeList", mock.getParamTypeList());
                    temp.put("action", mock.getAction());
                    mockException.put("basicData", temp);
                    break;
                }
                case "http": {
                    JSONObject temp = new JSONObject();
                    temp.put("action", mock.getAction());
                    temp.put("httpUrl", mock.getHttpUrl());
                    mockException.put("basicData", temp);
                    break;
                }
            }
            //原先的yts.mock的value
            JSONObject jsonObject = fileJSON.getJSONObject("yts.mock");
            //新增数据,到此就算把新的content组装好了
            jsonObject.put(mockKey, mockException);
            fileString = fileJSON.toJSONString();
            //下面组装请求的json
            JSONObject requestJson = new JSONObject();
            //微服务编码
            requestJson.put("serviceCode", msCode);
            //租户id
            requestJson.put("providerId", providerId);
            JSONArray contentListArr = new JSONArray();
            JSONObject contentListObj = new JSONObject();
            contentListObj.put("version", version);
            Integer ienv = EnvEnum.getIdByName(env);
            contentListObj.put("envId", ienv);
            contentListObj.put("content", fileString);
            contentListObj.put("fileKey", file);
            contentListArr.add(contentListObj);
            requestJson.put("contentList", contentListArr);
            String requestString = requestJson.toJSONString();
            return postFile(requestString, updateUrl, accessKey, accessSecret);
        } else {
            return addCheckValid(mock, providerId, msCode, env);
        }
    }

    /**
     * 根据key删除指定的打桩数据
     */
    @RequestMapping("removeMock")
    @ResponseBody
    public Object removeMock(@RequestBody String mockjson, HttpServletRequest request) throws IOException {
        JSONObject mockobj = JSON.parseObject(mockjson);
        String msCode = mockobj.getString("msCode");
        String version = mockobj.getString("version");
        String env = mockobj.getString("env");
        Mock mock = new Mock();
        mock.setType(mockobj.getString("type"));
        mock.setMsg(mockobj.getString("msg"));
        mock.setPosition(mockobj.getString("position"));
        mock.setInvokePosition(mockobj.getString("invokePosition"));
        mock.setTimeout(mockobj.getInteger("timeout"));
        mock.setMode(mockobj.getString("mode"));
        mock.setKey(mockobj.getString("key"));
        mock.setTenantId(mockobj.getString("tenantId"));
        mock.setDocumentType(mockobj.getString("documentType"));
        mock.setAct(mockobj.getString("act"));
        mock.setRuleId(mockobj.getString("ruleId"));
        mock.setAction(mockobj.getString("action"));
        mock.setPackageName(mockobj.getString("packageName"));
        mock.setClassName(mockobj.getString("className"));
        mock.setMethodName(mockobj.getString("methodName"));
        mock.setParamTypeList((List<String>) mockobj.get("paramTypeList"));
        mock.setHttpUrl(mockobj.getString("httpUrl"));
        return removeMock1(mock, request, msCode, version, env);
    }

    public Object removeMock1(Mock mock, HttpServletRequest request, String msCode, String version, String env) throws IOException {
        String providerId = getProviderId(request);
        //首先获取文件内容（JSONObject对象）
        if (removeCheckValid(mock, providerId, msCode, env).equals("success")) {
            if (version == null || version.trim().equals("")) {
                version = "1.0.0";
            }
            String file = "mwclient.json";
            String fileString;
            try {
                fileString = (String) readFile(queryUrl, providerId, msCode, version, env, file, accessKey, accessSecret);
            } catch (NullPointerException e) {
                return "删除失败：无效的租户id";
            }
            String filePath = "/mwclient.json";
            JSONArray array = JSON.parseArray(fileString);
            for (int i = 0; i < array.size(); i++) {
                String path = array.getJSONObject(i).getString("path");
                if (path != null && path.equals(filePath)) {
                    fileString = array.getJSONObject(i).getString("content");
                }
            }
            JSONObject fileJSON;
            try {
                fileJSON = JSON.parseObject(fileString);
            } catch (JSONException i) {
                return "删除失败：配置文件中的数据不是json格式！";
            }
            if (fileJSON == null) {
                return "删除失败：配置文件中没有数据";
            }
            if (fileJSON.containsKey("error_message") && fileJSON.get("error_message").equals("该配置文件不是公开配置文件，不能被其他租户读取")) {
                return "删除失败:该配置文件不是公开配置文件，不能被其他租户读取";
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
            requestJson.put("serviceCode", msCode);
            //租户id
            requestJson.put("providerId", providerId);
            JSONArray contentListArr = new JSONArray();
            JSONObject contentListObj = new JSONObject();
            contentListObj.put("version", version);
            Integer ienv = EnvEnum.getIdByName(env);
            contentListObj.put("envId", ienv);
            contentListObj.put("content", fileString);
            contentListObj.put("fileKey", file);
            contentListArr.add(contentListObj);
            requestJson.put("contentList", contentListArr);
            String requestString = requestJson.toJSONString();
            return postFile(requestString, updateUrl, accessKey, accessSecret);
        } else {
            return removeCheckValid(mock, providerId, msCode, env);
        }
    }

    /**
     * 修改桩信息
     */
    @RequestMapping("changeMock")
    @ResponseBody
    public Object changeMock(@RequestBody String mockJson, HttpServletRequest request) throws IOException {
        if (JSON.parseObject((String) removeMock(mockJson, request)).get("success").equals("true"))
            return addMock(mockJson, request);
        else
            return "编辑错误";
    }

    /**
     * 清空配置文件内容
     */
    @RequestMapping("clearFile")
    @ResponseBody
    public Object clearFile(@RequestBody String mockjson, HttpServletRequest request) throws IOException {
        JSONObject mockobj = JSON.parseObject(mockjson);
        String msCode = mockobj.getString("msCode");
        String version = mockobj.getString("version");
        String env = mockobj.getString("env");
        return clearFile1(request, msCode, version, env);
    }

    public Object clearFile1(HttpServletRequest request, String msCode, String version, String env) throws IOException {
        String providerId = getProviderId(request);
        //下面组装请求的json
        if (getCheckValid(providerId, msCode, env).equals("success")) {
            if (version == null || version.trim().equals("")) {
                version = "1.0.0";
            }
            String file = "mwclient.json";
            //如果是生产环境和沙箱环境就直接报错
            if (env.equals("online") || env.equals("sandbox") || env.equals("online-ap-sg1") || env.equals("online-cn-ecology")) {
                return "修改失败：该环境不允许修改";
            }
            JSONObject requestJson = new JSONObject();
            //微服务编码
            requestJson.put("serviceCode", msCode);
            //租户id
            requestJson.put("providerId", providerId);
            JSONArray contentListArr = new JSONArray();
            JSONObject contentListObj = new JSONObject();
            contentListObj.put("version", version);
            Integer ienv = EnvEnum.getIdByName(env);
            contentListObj.put("envId", ienv);
            contentListObj.put("content", "");
            contentListObj.put("fileKey", file);
            contentListArr.add(contentListObj);
            requestJson.put("contentList", contentListArr);
            String requestString = requestJson.toJSONString();
            String result;
            result = postFile(requestString, updateUrl, accessKey, accessSecret);
            return result;
        } else {
            return getCheckValid(providerId, msCode, env);
        }
    }

    /**
     * 仅删除mock内容
     */
    @RequestMapping("clearMock")
    @ResponseBody
    public Object clearMock(@RequestBody String mockjson, HttpServletRequest request) throws IOException {
        JSONObject mockobj = JSON.parseObject(mockjson);
        String msCode = mockobj.getString("msCode");
        String version = mockobj.getString("version");
        String env = mockobj.getString("env");
        return clearMock1(request, msCode, version, env);
    }

    public Object clearMock1(HttpServletRequest request, String msCode, String version, String env) throws IOException {
        String providerId = getProviderId(request);
        //首先获取文件内容（JSONObject对象）
        if (getCheckValid(providerId, msCode, env).equals("success")) {
            String fileString;
            if (version == null || version.trim().equals("")) {
                version = "1.0.0";
            }
            String file = "mwclient.json";
            if (env.equals("online") || env.equals("sandbox") || env.equals("online-ap-sg1") || env.equals("online-cn-ecology")) {
                return "删除失败：该环境不允许修改";
            }
            try {
                fileString = (String) readFile(queryUrl, providerId, msCode, version, env, file, accessKey, accessSecret);
            } catch (NullPointerException e) {
                return "删除失败：无效的租户id";
            }
            String filePath = "/mwclient.json";
            JSONArray array = JSON.parseArray(fileString);
            for (int i = 0; i < array.size(); i++) {
                String path = array.getJSONObject(i).getString("path");
                if (path != null && path.equals(filePath)) {
                    fileString = array.getJSONObject(i).getString("content");
                }
            }
            JSONObject fileJSON;
            try {
                fileJSON = JSON.parseObject(fileString);
            } catch (JSONException i) {
                return "删除失败：配置文件中的数据不是json格式！";
            }
            if (fileJSON == null) {
                return "删除失败：配置文件中没有数据";
            }
            if (fileJSON.containsKey("error_message") && fileJSON.get("error_message").equals("该配置文件不是公开配置文件，不能被其他租户读取")) {
                return "删除失败:该配置文件不是公开配置文件，不能被其他租户读取";
            }
            if (!fileJSON.containsKey("yts.mock")) {
                return "删除失败：json数据中未找到yts.mock";
            }
            fileJSON.put("yts.mock", new JSONObject());
            //下面组装请求的json
            JSONObject requestJson = new JSONObject();
            //微服务编码
            requestJson.put("serviceCode", msCode);
            //租户id
            requestJson.put("providerId", providerId);
            JSONArray contentListArr = new JSONArray();
            JSONObject contentListObj = new JSONObject();
            contentListObj.put("version", version);
            Integer ienv = EnvEnum.getIdByName(env);
            contentListObj.put("envId", ienv);
            contentListObj.put("content", fileJSON.toJSONString());
            contentListObj.put("fileKey", file);
            contentListArr.add(contentListObj);
            requestJson.put("contentList", contentListArr);
            String requestString = requestJson.toJSONString();
            return postFile(requestString, updateUrl, accessKey, accessSecret);
        } else {
            return getCheckValid(providerId, msCode, env);
        }
    }

    /**
     * 读取文件内容
     *
     * @param request 读取cookie中的providerId
     * @param msCode  微服务编码
     * @param version 配置文件版本
     * @param env     环境名称
     */
    @RequestMapping("getFile")
    @ResponseBody
    public Object getFile(HttpServletRequest request, String msCode, String version, String env) throws IOException {
        //参数完整性
        String providerId = getProviderId(request);
        if (getCheckValid(providerId, msCode, env).equals("success")) {
            if (version == null || version.trim().equals("")) {
                version = "1.0.0";
            }
            String file = "mwclient.json";
            String fileString;
            //验证租户id
            try {
                fileString = (String) readFile(queryUrl, providerId, msCode, version, env, file, accessKey, accessSecret);
            } catch (NullPointerException e) {
                return "获取失败：无效的租户id";
            }
            String filePath = "/mwclient.json";
            JSONArray array = JSON.parseArray(fileString);
            for (int i = 0; i < array.size(); i++) {
                String path = array.getJSONObject(i).getString("path");
                if (path != null && path.equals(filePath)) {
                    fileString = array.getJSONObject(i).getString("content");
                }
            }
            return fileString;
        } else {
            return getCheckValid(providerId, msCode, env);
        }
    }
}