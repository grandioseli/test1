package com.example.mockapi.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mockapi.domain.mock;
import com.example.mockapi.utils.urlUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/yts/")
public class MockController {
    @Value("${yts.mock.url}")
    private String ytsMockUrl;
    //    @RequestMapping("MockApiForJson")
//    public Object returnLink(@RequestBody JSONObject jsonObject) throws UnsupportedEncodingException {
//        String mockUrl = jsonObject.toJSONString();
//        mockUrl = URLEncoder.encode(mockUrl,"utf-8");
//        return mockUrl;
//    }
//    @RequestMapping("MockApiForJsonString")
//    public Object returnLink(String jsonString) throws UnsupportedEncodingException {
//        JSONObject jsonobj = JSON.parseObject(jsonString);
//        String mockUrl = jsonobj.toJSONString();
//        return URLEncoder.encode(jsonString,"utf-8");
//    }
//    @RequestMapping("JsonObject")
//    public Object returnJson(String jsonString) {
//        return JSON.parseObject(jsonString);
//    }
//    @CrossOrigin(origins = "*", maxAge = 3600)
//    @RequestMapping("setMock")
//    @ResponseBody
//    public Object setMock(String tenant, String documentType, String act, String ruleId, String action, String type, String msg, String position, String invokePosition, Integer timeout) throws UnsupportedEncodingException {
//        String mockKey = "";
//        mockKey = tenant + "_" + documentType + "_" + act + "_" + ruleId + "_" + action;
//        mockKey = mockKey.replace("_null","");
//        System.out.println(mockKey);
//        JSONObject mock = new JSONObject();
//        JSONObject mockException = new JSONObject();
//        if(type!=null) {
//            mockException.put("type", type);
//        }
//        if(msg!=null) {
//            mockException.put("msg", msg);
//        }
//        if(position!=null) {
//            mockException.put("position", position);
//        }
//        if(invokePosition!=null) {
//            mockException.put("invokePosition", invokePosition);
//        }
//        if(timeout!=null) {
//            mockException.put("timeout", timeout);
//        }
//        mock.put(mockKey, mockException);
//        String mockUrl = mock.toJSONString();
//        mockUrl = URLEncoder.encode(mockUrl,"utf-8");
//        String url = ytsMockUrl+"set_mock?mock="+mockUrl;
//        String result = urlUtil.sendGet(url);
//        JSONObject jsonobj = JSON.parseObject(result);
//        return result;
//        System.out.println(mock);
//        return mock;
//    }
//    @CrossOrigin(origins = "*", maxAge = 3600)
//    @RequestMapping("setMocks")
//    @ResponseBody
//    public Object setMocks(@RequestParam(value = "tenant[]", required = false) String[] tenant, @RequestParam(value = "documentType[]", required = false) String[] documentType,
//                              @RequestParam(value = "act[]", required = false) String[] act, @RequestParam(value = "ruleId[]", required = false) String[] ruleId, @RequestParam(value = "action[]", required = false) String[] action,
//                              @RequestParam(value = "type[]", required = false) String[] type, @RequestParam(value = "msg[]", required = false) String[] msg,
//                              @RequestParam(value = "position[]" ,required = false) String[] position, @RequestParam(value = "invokePosition[]", required = false) String[] invokePosition, @RequestParam(value = "timeout[]", required = false) Integer[] timeout) throws UnsupportedEncodingException {
//        int number = tenant.length;
//        JSONObject mock = new JSONObject();
//        for (int i = 0; i < number; i++) {
//            String mockKey = "";
//            mockKey = tenant[i] + "_" + documentType[i] + "_" + act[i] + "_" + ruleId[i] + "_" + action[i];
//            JSONObject mockException = new JSONObject();
//            if(type!=null) {
//                mockException.put("type", type[i]);
//            }
//            if(msg!=null) {
//                mockException.put("msg", msg[i]);
//            }
//            if(position!=null) {
//                mockException.put("position", position[i]);
//            }
//            if(invokePosition!=null) {
//                mockException.put("invokePosition", invokePosition[i]);
//            }
//            if (timeout != null) {
//                mockException.put("timeout", timeout[i]);
//            }
//            mock.put(mockKey, mockException);
//        }
//        return mock;
//        String mockUrl = mock.toJSONString();
//        mockUrl = URLEncoder.encode(mockUrl, "utf-8");
//        String url = ytsMockUrl + "set_mock?mock="+mockUrl;
//        String result = urlUtil.sendGet(url);
//        return result;
//    }
//    @CrossOrigin(origins = "*", maxAge = 3600)
//    @RequestMapping("clearMock")
//    @ResponseBody
//    public Object clearMock() {
//        String url = ytsMockUrl + "clear_mock";
//        String result = urlUtil.sendGet(url);
//        return result;
//    }
    /**
    创建配置文件，格式为json，key为“yts.mock”，value为空
    若不需要创建文件则删除本方法
     */
    @RequestMapping("createMockFile")
    @ResponseBody
    public Object createMockFile(String fileName) throws IOException {
        //
        File file =new File(fileName);
        if(!file.exists())
        {
            System.out.println("不存在");
            System.out.println(file.createNewFile());
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file,true);
        OutputStreamWriter out =new OutputStreamWriter(fos,"utf-8");
        BufferedWriter bw = new BufferedWriter(out);
        JSONObject ytsMockObj = new JSONObject();
        String ytsMock = "yts.mock";
        JSONObject obj= new JSONObject();
        ytsMockObj.put(ytsMock,obj);
        String mockUrl = ytsMockObj.toJSONString();
        bw.write(mockUrl);
        bw.flush();
        bw.close();
        return "success";
    }
    /**
    向配置文件中添加桩信息
     */
    @RequestMapping("addMock")
    @ResponseBody
    public Object addMock(String fileName,String model,mock mockobj) throws IOException {
        File file = new File(fileName);
        //读取配置文件中的json对象
        JSONObject jsonobj;
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            String jsonStr = sb.toString();
            jsonobj = JSON.parseObject(jsonStr);
//            System.out.println(jsonobj);
            //清空文件
            File file2 =new File(fileName);
            try {
                if(!file2.exists()) {
                    file2.createNewFile();
                }
                FileWriter fileWriter =new FileWriter(file2);
                fileWriter.write("");
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        //读取传入的数据并结合原有的json对象，将其写入文件
        FileOutputStream fos = new FileOutputStream(file,true);
        OutputStreamWriter out =new OutputStreamWriter(fos,"utf-8");
        BufferedWriter bw = new BufferedWriter(out);
        JSONObject ytsMockObj = new JSONObject();
        String mockKey = "";
        mockobj.setId(model);
        mockKey = mockobj.getId();
//        System.out.println(mockKey);
        JSONObject mock = new JSONObject();
        JSONObject mockException = new JSONObject();
        if(mockobj.getType()!=null) {
            mockException.put("type", mockobj.getType());
        }
        if(mockobj.getMsg()!=null) {
            mockException.put("msg", mockobj.getMsg());
        }
        if(mockobj.getPosition()!=null) {
            mockException.put("position", mockobj.getPosition());
        }
        if(mockobj.getInvokePosition()!=null) {
            mockException.put("invokePosition", mockobj.getInvokePosition());
        }
        if(mockobj.getTimeout()!=null) {
            mockException.put("timeout", mockobj.getTimeout());
        }
        mock.put(mockKey, mockException);
        JSONObject jsonObject = jsonobj.getJSONObject("yts.mock");
        System.out.println(jsonObject);
        jsonObject.put(mockKey,mockException);
        String mockUrl = jsonobj.toJSONString();
        bw.write(mockUrl);
        bw.write("\r\n");
        bw.flush();
        bw.close();
        System.out.println("写入成功！");
        return "success";
    }
    @RequestMapping("getMock")
    @ResponseBody
    public Object getMock(String fileName) {
        File file = new File(fileName);
        mock mock;
        ObjectMapper mapper = new ObjectMapper();
        try {
            //读取配置文件并转化为json格式
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            String jsonStr = sb.toString();
            JSONObject jsonobj = JSON.parseObject(jsonStr);
            JSONObject mockException = jsonobj.getJSONObject("yts.mock");
            String temp = mockException.toJSONString();
            mock = mapper.readValue(temp,mock.class);//Json对象转为实体对象
            List<mock> trainTypeList = mock.getTrainTypeList();
//            List<List<String>> mlist = new LinkedList<>();
            //将json存放到list中
//            List<JSONObject> mlist = new LinkedList<>();
            return trainTypeList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}