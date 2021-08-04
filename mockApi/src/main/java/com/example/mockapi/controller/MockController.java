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
    @RequestMapping("returnJson")
    @ResponseBody
    public Object returnJson(String tenant,String documentType,String action,String ruleId,String act,String type,String msg, String position,String invokePostion,String timeout) throws UnsupportedEncodingException {
        String mockKey = tenant+"_"+documentType+"_"+action+"_"+ruleId+"_"+act;
        JSONObject mock=new JSONObject();
        JSONObject mockException=new JSONObject();
        mockException.put("type",type);
        mockException.put("msg",msg);
        mockException.put("position",position);
        mockException.put("invokePosition",invokePostion);
        mockException.put("timeout",timeout);
        mock.put(mockKey,mockException);
        String mockUrl = mock.toJSONString();
        mockUrl = URLEncoder.encode(mockUrl,"utf-8");
        String url = "http://yts-demo-pay.daily.app.yyuap.com/yts/test/api/mock/set_mock?mock="+mockUrl;
        String result = urlUtil.sendGet(url);
        return result;
//        return mock;
    }
    @RequestMapping("setMock")
    @ResponseBody
    public Object returnJsons(@RequestParam(value = "tenant[]",defaultValue = "null") String[] tenant,@RequestParam(value = "documentType[]",defaultValue = "null")String[] documentType,
                              @RequestParam(value = "action[]",defaultValue = "null")String[] action,@RequestParam(value = "ruleId[]",defaultValue = "null")String[] ruleId,@RequestParam(value = "act[]",defaultValue = "null")String[] act,
                              @RequestParam(value = "type[]",defaultValue = "null")String[] type,@RequestParam(value = "msg[]",defaultValue = "null")String[] msg,
                              @RequestParam(value = "position[]",defaultValue = "null")String[] position,@RequestParam(value = "invokePosition[]",defaultValue = "null")String[] invokePosition,@RequestParam(value = "timeout[]",defaultValue = "null")String[] timeout) throws UnsupportedEncodingException {
        int number = tenant.length;
        JSONObject mock = new JSONObject();
        for(int i = 0;i<number;i++) {
            String mockKey;
            if(act[i].equals("cancel"))
            {mockKey = tenant[i] + "_" + documentType[i] + "_" + action[i] + "_" + ruleId[i]+"_"+act[i];}
            else
            {mockKey = tenant[i] + "_" + documentType[i] + "_" + action[i] + "_" + ruleId[i];}
            JSONObject mockException = new JSONObject();
            mockException.put("type", type[i]);
            mockException.put("msg", msg[i]);
            mockException.put("position", position[i]);
            mockException.put("invokePosition", invokePosition[i]);
            mockException.put("timeout", timeout[i]);
            mock.put(mockKey, mockException);
        }
//        return mock;
        String mockUrl = mock.toJSONString();
        mockUrl = URLEncoder.encode(mockUrl,"utf-8");
        String url = "http://yts-demo-pay.daily.app.yyuap.com/yts/test/api/mock/set_mock?mock="+mockUrl;
        String result = urlUtil.sendGet(url);
        return result;
    }

}