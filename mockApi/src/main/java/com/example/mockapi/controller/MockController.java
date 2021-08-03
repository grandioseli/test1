package com.example.mockapi.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

@RestController
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
//    @RequestMapping("returnJson")
//    public Object returnJson(String tenant,String documentType,String action,String ruleId,String type,String msg, String position,String invokePostion,String timeout) {
//        String mockKey = tenant+"_"+documentType+"_"+action+"_"+ruleId;
//        JSONObject mock=new JSONObject();
//        JSONObject mockException=new JSONObject();
//        mockException.put("type",type);
//        mockException.put("msg",msg);
//        mockException.put("position",position);
//        mockException.put("invokePosition",invokePostion);
//        mockException.put("timeout",timeout);
//        mock.put(mockKey,mockException);
//        return mock;
//    }
    @RequestMapping("returnJsons")
    public Object returnJsons(@RequestParam(value = "tenant[]",defaultValue = "null") String[] tenant,@RequestParam(value = "documentType[]",defaultValue = "null")String[] documentType,
                              @RequestParam(value = "action[]",defaultValue = "null")String[] action,@RequestParam(value = "ruleId[]",defaultValue = "null")String[] ruleId,@RequestParam(value = "dir[]",defaultValue = "null")String[] dir,
                              @RequestParam(value = "type[]",defaultValue = "null")String[] type,@RequestParam(value = "msg[]",defaultValue = "null")String[] msg,
                              @RequestParam(value = "position[]",defaultValue = "null")String[] position,@RequestParam(value = "invokePosition[]",defaultValue = "null")String[] invokePosition,@RequestParam(value = "timeout[]",defaultValue = "null")String[] timeout) {
        int number = tenant.length;
        JSONObject mock = new JSONObject();
        for(int i = 0;i<number;i++) {
            String mockKey;
            if(dir[i].equals("null"))
            {mockKey = tenant[i] + "_" + documentType[i] + "_" + action[i] + "_" + ruleId[i];}
            else
            {mockKey = tenant[i] + "_" + documentType[i] + "_" + action[i] + "_" + ruleId[i]+"_"+dir[i];}
            JSONObject mockException = new JSONObject();
            mockException.put("type", type[i]);
            mockException.put("msg", msg[i]);
            mockException.put("position", position[i]);
            mockException.put("invokePosition", invokePosition[i]);
            mockException.put("timeout", timeout[i]);
            mock.put(mockKey, mockException);
        }
        return mock;
    }
//    @RequestMapping("returnJsonss")
//    public Object returnJsonss(@RequestParam(value = "tenant[]",defaultValue = "") String[] tenant,@RequestParam(value = "documentType[]",defaultValue = "")String[] documentType,
//                              @RequestParam(value = "action[]",defaultValue = "")String[] action,@RequestParam(value = "ruleId[]",defaultValue = "")String[] ruleId,
//                              @RequestParam(value = "type[]",defaultValue = "")String[] type,@RequestParam(value = "msg[]",defaultValue = "")String[] msg,
//                              @RequestParam(value = "position[]",defaultValue = "")String[] position,@RequestParam(value = "invokePosition[]",defaultValue = "")String[] invokePosition,@RequestParam(value = "timeout[]",defaultValue = "")String[] timeout) {
//        int number = tenant.length;
//        JSONObject mock = new JSONObject();
//        for(int i = 0;i<number;i++) {
//            String mockKey = tenant[i] + "_" + documentType[i] + "_" + action[i] + "_" + ruleId[i];
//            JSONObject mockException = new JSONObject();
//            mockException.put("type", type[i]);
//            mockException.put("msg", msg[i]);
//            mockException.put("position", position[i]);
//            mockException.put("invokePosition", invokePosition[i]);
//            mockException.put("timeout", timeout[i]);
//            mock.put(mockKey, mockException);
//        }
//        return mock;
//    }
}