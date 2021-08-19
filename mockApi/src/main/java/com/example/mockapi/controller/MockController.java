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

import static com.example.mockapi.utils.FileReaderUtil.*;

@Controller
@RequestMapping("/yts/")
public class MockController {
    //查询接口
    @Value("${yts.http.query.url}")
    private String queryUrl;
    //更新接口
    @Value("${yts.http.update.url}")
    private String updateUrl;

    /**
     * 向指定配置文件请求打桩信息
     *
     * @param request 用于租户id/微服务命名空间
     * @param msCode  微服务编码
     * @param version 配置文件版本
     * @param env     环境名称
     * @param file    配置文件名称
     * @return 返回实体类list即[BaseMock, BaseMock,……]
     */
    @RequestMapping("getMock")
    @ResponseBody
    public Object getMock(HttpServletRequest request, String msCode, String version, String env, String file) throws IOException {
        //从cookie中获取providerId
        String providerId = getProviderId(request);
        //根据参数获取文件内容(JSONObject)
        //检查完整性,version和file有默认值
        if (getCheckValid(providerId, msCode, env).equals("success")) {
            if (version == null || version.trim().equals("")) {
                version = "1.0.0";
            }
            if (file == null || file.trim().equals("")) {
                file = "mwclient.json";
            }
            ObjectMapper mapper = new ObjectMapper();
            String fileString;
            //尝试读取文件
            try {
                fileString = (String) readFile(queryUrl, providerId, msCode, version, env, file);
            } catch (NullPointerException e) {
                return "获取失败：无效的租户id";
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
            //由于key可能是拼接而成的，因此不对外层的json做转化，只对内层的value做转化，将key进行手赋值
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
     * 向指定配置文件中写入数据
     *
     * @param mock    实体类用于承接桩信息
     * @param request 租户id
     * @param msCode  微服务编码
     * @param version 配置文件版本
     * @param env     环境名称
     * @param file    配置文件名称
     * @return 返回添加信息
     */
    @RequestMapping("addMock")
    @ResponseBody
    public Object addMock(Mock mock, HttpServletRequest request, String msCode, String version, String env, String file) throws IOException {
        String providerId = getProviderId(request);
        //首先获取文件内容（JSONObject对象）
        if (addCheckValid(mock, providerId, msCode, env).equals("success")) {
            if (version == null || version.trim().equals("")) {
                version = "1.0.0";
            }
            if (file == null || file.trim().equals("")) {
                file = "mwclient.json";
            }
            String fileString;
            try {
                fileString = (String) readFile(queryUrl, providerId, msCode, version, env, file);
            } catch (NullPointerException e) {
                return "写入失败：无效的租户id";
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
            return postFile(requestString, updateUrl);
        } else {
            return addCheckValid(mock, providerId, msCode, env);
        }
    }

    /**
     * 根据key删除指定的打桩数据
     *
     * @param mock    实体类，需要的是里面的key
     * @param request 用于获取租户id
     * @param msCode  微服务编码
     * @param version 配置文件版本
     * @param env     环境名称
     * @param file    配置文件名称
     */
    @RequestMapping("removeMock")
    @ResponseBody
    public Object removeMock(Mock mock, HttpServletRequest request, String msCode, String version, String env, String file) throws IOException {
        String providerId = getProviderId(request);
        //首先获取文件内容（JSONObject对象）
        if (removeCheckValid(mock, providerId, msCode, env).equals("success")) {
            if (version == null || version.trim().equals("")) {
                version = "1.0.0";
            }
            if (file == null || file.trim().equals("")) {
                file = "mwclient.json";
            }
            String fileString;
            try {
                fileString = (String) readFile(queryUrl, providerId, msCode, version, env, file);
            } catch (NullPointerException e) {
                return "删除失败：无效的租户id";
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
            return postFile(requestString, updateUrl);
        } else {
            return removeCheckValid(mock, providerId, msCode, env);
        }
    }

    /**
     * 修改桩信息
     *
     * @param mock    桩信息实体类
     * @param request 用于获取微服务租户id
     * @param msCode  微服务编码
     * @param version 微服务版本
     * @param env     环境id
     * @param file    配置文件名
     * @return
     * @throws IOException
     */
    @RequestMapping("changeMock")
    @ResponseBody
    public Object changeMock(Mock mock, HttpServletRequest request, String msCode, String version, String env, String file) throws IOException {
        removeMock(mock, request, msCode, version, env, file);
        return addMock(mock, request, msCode, version, env, file);
    }

    /**
     * 清空配置文件内容
     *
     * @param request 用于获取租户id
     * @param msCode  微服务编码
     * @param version 配置文件版本
     * @param env     环境名称
     * @param file    配置文件名称
     */
    @RequestMapping("clearFile")
    @ResponseBody
    public Object clearFile(HttpServletRequest request, String msCode, String version, String env, String file) throws IOException {
        String providerId = getProviderId(request);
        //下面组装请求的json
        if (getCheckValid(providerId, msCode, env).equals("success")) {
            if (version == null || version.trim().equals("")) {
                version = "1.0.0";
            }
            if (file == null || file.trim().equals("")) {
                file = "mwclient.json";
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
            result = postFile(requestString, updateUrl);
            return result;
        } else {
            return getCheckValid(providerId, msCode, env);
        }
    }

    /**
     * 仅删除mock内容
     *
     * @param request 获取providerId
     * @param msCode  微服务编码
     * @param version 配置文件版本
     * @param env     环境名称
     * @param file    配置文件名称
     */
    @RequestMapping("clearMock")
    @ResponseBody
    public Object clearMock(HttpServletRequest request, String msCode, String version, String env, String file) throws IOException {
        String providerId = getProviderId(request);
        //首先获取文件内容（JSONObject对象）
        if (getCheckValid(providerId, msCode, env).equals("success")) {
            String fileString;
            if (version == null || version.trim().equals("")) {
                version = "1.0.0";
            }
            if (file == null || file.trim().equals("")) {
                file = "mwclient.json";
            }
            try {
                fileString = (String) readFile(queryUrl, providerId, msCode, version, env, file);
            } catch (NullPointerException e) {
                return "删除失败：无效的租户id";
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
            return postFile(requestString, updateUrl);
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
     * @param file    配置文件名称
     */
    @RequestMapping("getFile")
    @ResponseBody
    public Object getFile(HttpServletRequest request, String msCode, String version, String env, String file) throws IOException {
        //参数完整性
        String providerId = getProviderId(request);
        if (getCheckValid(providerId, msCode, env).equals("success")) {
            if (version == null || version.trim().equals("")) {
                version = "1.0.0";
            }
            if (file == null || file.trim().equals("")) {
                file = "mwclient.json";
            }
            String fileString;
            //验证租户id
            try {
                fileString = (String) readFile(queryUrl, providerId, msCode, version, env, file);
            } catch (NullPointerException e) {
                return "获取失败：无效的租户id";
            }
            return fileString;
        } else {
            return getCheckValid(providerId, msCode, env);
        }
    }
}