package com.example.mockapi.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mockapi.utils.urlUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

@Controller
@RequestMapping("/yts/")
public class MockController {
    //    @RequestMapping("MockApiForJson")
//    public Object returnLink(@RequestBody JSONObject jsonObject) throws UnsupportedEncodingException {
//        String mockUrl = jsonObject.toJSONString();
//        mockUrl = URLEncoder.encode(mockUrl,"utf-8");
//        return mockUrl;
//    }
//    @RequestMapping("MockApiForJsonString")
//    public Object returnLink(String jsonString) throws UnsupportedEncodingException {
////        JSONObject jsonobj = JSON.parseObject(jsonString);
////        String mockUrl = jsonobj.toJSONString();
//        return URLEncoder.encode(jsonString,"utf-8");
//    }
//    @RequestMapping("JsonObject")
//    public Object returnJson(String jsonString) {
//        return JSON.parseObject(jsonString);
//    }
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping("returnJson")
    @ResponseBody
    public Object returnJson(String tenant, String documentType, String action, String ruleId, String act, String type, String msg, String position, String invokePosition, Integer timeout) throws UnsupportedEncodingException {
        String mockKey = "";
        mockKey = tenant + "_" + documentType + "_" + action + "_" + ruleId + "_" + act;
        JSONObject mock = new JSONObject();
        JSONObject mockException = new JSONObject();
        if(type!=null) {
            mockException.put("type", type);
        }
        if(msg!=null) {
            mockException.put("msg", msg);
        }
        if(position!=null) {
            mockException.put("position", position);
        }
        if(invokePosition!=null) {
            mockException.put("invokePosition", invokePosition);
        }
        if(timeout!=null) {
            mockException.put("timeout", timeout);
        }
        mock.put(mockKey, mockException);
//        String mockUrl = mock.toJSONString();
//        mockUrl = URLEncoder.encode(mockUrl,"utf-8");
//        String url = "http://yts-demo-pay.daily.app.yyuap.com/yts/test/api/mock/set_mock?mock="+mockUrl;
//        String result = urlUtil.sendGet(url);
//        return result;
        System.out.println(mock);
        return mock;
    }
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping("setMock")
    @ResponseBody
    public Object setMock(@RequestParam(value = "tenant[]", required = false) String[] tenant, @RequestParam(value = "documentType[]", required = false) String[] documentType,
                              @RequestParam(value = "action[]", required = false) String[] action, @RequestParam(value = "ruleId[]", required = false) String[] ruleId, @RequestParam(value = "act[]", required = false) String[] act,
                              @RequestParam(value = "type[]", required = false) String[] type, @RequestParam(value = "msg[]", required = false) String[] msg,
                              @RequestParam(value = "position[]" ,required = false) String[] position, @RequestParam(value = "invokePosition[]", required = false) String[] invokePosition, @RequestParam(value = "timeout[]", required = false) Integer[] timeout) throws UnsupportedEncodingException {
        int number = tenant.length;
        JSONObject mock = new JSONObject();
        for (int i = 0; i < number; i++) {
            String mockKey = "";
            mockKey = tenant[i] + "_" + documentType[i] + "_" + action[i] + "_" + ruleId[i] + "_" + act[i];
            JSONObject mockException = new JSONObject();
            if(type!=null) {
                mockException.put("type", type[i]);
            }
            if(msg!=null) {
                mockException.put("msg", msg[i]);
            }
            if(position!=null) {
                mockException.put("position", position[i]);
            }
            if(invokePosition!=null) {
                mockException.put("invokePosition", invokePosition[i]);
            }
            if (timeout != null) {
                mockException.put("timeout", timeout[i]);
            }
            mock.put(mockKey, mockException);
        }
//        return mock;
        String mockUrl = mock.toJSONString();
        mockUrl = URLEncoder.encode(mockUrl, "utf-8");
        String url = "http://yts-demo-pay.daily.app.yyuap.com/yts/test/api/mock/set_mock?mock=" + mockUrl;
        String result = urlUtil.sendGet(url);
        return result;
    }
}