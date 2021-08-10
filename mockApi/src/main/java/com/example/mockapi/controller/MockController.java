package com.example.mockapi.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mockapi.domain.Mock;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/yts/")
public class MockController {
//    /**
//     * 创建配置文件，格式为json，key为“yts.mock”，value为空
//     *
//     * @param fileName 配置文件名，如果当前目录没有该文件则创建
//     *                 若不需要创建文件则删除本方法
//     */
//    @RequestMapping("createMockFile")
//    @ResponseBody
//    public Object createMockFile(String fileName) throws IOException {
//        //
//        File file = new File(fileName);
//        //如果文件不存在则创建文件并在文件里面预设好yts.mock，value为空
//        if (!file.exists()) {
//            file.createNewFile();
//            try {
//                FileOutputStream fos = new FileOutputStream(file, true);
//                OutputStreamWriter out = new OutputStreamWriter(fos, "utf-8");
//                BufferedWriter bw = new BufferedWriter(out);
//                JSONObject ytsMockObj = new JSONObject();
//                String ytsMock = "yts.mock";
//                JSONObject obj = new JSONObject();
//                ytsMockObj.put(ytsMock, obj);
//                String mockUrl = ytsMockObj.toJSONString();
//                bw.write(mockUrl);
//                bw.flush();
//                bw.close();
//                return "success";
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return "该文件已经存在";
//    }

    /**
     * 向配置文件中添加桩信息,用作增加数据和修改数据接口，如果key相同则为修改数据，如果key不同则为增加数据
     *
     * @param fileName 配置文件
     * @param mockobj  用于承接传入的打桩数据
     */
    @RequestMapping("addMock")
    @ResponseBody
    public Object addMock(String fileName, Mock mockobj) throws IOException {
        File file = new File(fileName);
        //读取配置文件中的json对象，这里的做法是：每次新增/修改一个打桩项，首先读取原有的json数据并做修改，然后清空配置文件
        //将组成的新的json数据写入文件
        JSONObject jsonobj;
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            String jsonStr = sb.toString();
            jsonobj = JSON.parseObject(jsonStr);
            //清空文件
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter fileWriter = new FileWriter(file);
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
        FileOutputStream fos = new FileOutputStream(file, true);
        OutputStreamWriter out = new OutputStreamWriter(fos, "utf-8");
        BufferedWriter bw = new BufferedWriter(out);
        String mockKey = "";
        //根据模式设定key
        mockobj.setIdBymodel();
        mockKey = mockobj.getId();
        //新增的数据为JSONObject对象，上面key有了，下面组装value
        JSONObject mock = new JSONObject();
        //value也是JSONObject对象，它的各个值都是由前端传入
        JSONObject mockException = new JSONObject();
        if (mockobj.getType() != null) {
            mockException.put("type", mockobj.getType());
        }
        if (mockobj.getMsg() != null) {
            mockException.put("msg", mockobj.getMsg());
        }
        if (mockobj.getPosition() != null) {
            mockException.put("position", mockobj.getPosition());
        }
        if (mockobj.getInvokePosition() != null) {
            mockException.put("invokePosition", mockobj.getInvokePosition());
        }
        if (mockobj.getTimeout() != null) {
            mockException.put("timeout", mockobj.getTimeout());
        }
        if(mockobj.getModel()!=null)
        {
            mockException.put("model",mockobj.getModel());
        }
        //组装
        mock.put(mockKey, mockException);
        //原先的yts.mock的value
        JSONObject jsonObject = jsonobj.getJSONObject("yts.mock");
        //新增数据
        jsonObject.put(mockKey, mockException);
        //写入数据并关闭流
        String mockUrl = jsonobj.toJSONString();
        bw.write(mockUrl);
        bw.write("\r\n");
        bw.flush();
        bw.close();
        System.out.println("写入成功！");
        return "success";
    }

    /**
     * @param fileName 配置文件名，根据配置文件名查询内容。
     * @return 返回对象为list, 值为mock实体类
     */
    @RequestMapping("getMock")
    @ResponseBody
    public Object getMock(String fileName) {
        //用于将json转化为实体类
        ObjectMapper mapper = new ObjectMapper();
        try {
            //读取配置文件并转化为json格式
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            String jsonStr = sb.toString();
            JSONObject jsonobj = JSON.parseObject(jsonStr);
            //说明配置文件里没有json数据
            if(jsonobj == null)
            {
                System.out.println("文件里没有json格式的数据");
                FileOutputStream fos = new FileOutputStream(jsonFile, true);
                OutputStreamWriter out = new OutputStreamWriter(fos, "utf-8");
                BufferedWriter bw = new BufferedWriter(out);
                JSONObject ytsMockObj = new JSONObject();
                String ytsMock = "yts.mock";
                JSONObject obj = new JSONObject();
                ytsMockObj.put(ytsMock, obj);
                String mockUrl = ytsMockObj.toJSONString();
                bw.write(mockUrl);
                bw.flush();
                bw.close();
            }
            //说明配置文件里有json数据，但是没有"yts.mock"
            else if(!jsonobj.containsKey("yts.mock"))
            {
                System.out.println("json里没找到'yts.mock'");
                FileOutputStream fos = new FileOutputStream(jsonFile, true);
                OutputStreamWriter out = new OutputStreamWriter(fos, "utf-8");
                BufferedWriter bw = new BufferedWriter(out);
                JSONObject ytsMockObj = new JSONObject();
                String ytsMock = "yts.mock";
                JSONObject obj = new JSONObject();
                ytsMockObj.put(ytsMock, obj);
                String mockUrl = ytsMockObj.toJSONString();
                bw.write(mockUrl);
                bw.flush();
                bw.close();
            }
            //读取配置文件并转化为json格式
            File jsonFile2 = new File(fileName);
            FileReader fileReader2 = new FileReader(jsonFile2);
            Reader reader2 = new InputStreamReader(new FileInputStream(jsonFile2), "utf-8");
            int ch2 = 0;
            StringBuffer sb2 = new StringBuffer();
            while ((ch2 = reader2.read()) != -1) {
                sb2.append((char) ch2);
            }
            fileReader2.close();
            reader2.close();
            String jsonStr2 = sb2.toString();
            JSONObject jsonobj2 = JSON.parseObject(jsonStr2);
            JSONObject mockException = jsonobj2.getJSONObject("yts.mock");
            //这里的做法是yts.mock中的每一项都是一个打桩数据（key:mockKey,value:打桩数据，仍然是一个json对象），将它的keyset提取出来并遍历
            //由于key可能是拼接而成的，因此不对外层的json做转化，只对内层的value做转化，将key进行手赋值
            Iterator<String> its = mockException.keySet().iterator();
            List<Mock> list = new ArrayList<>();
            while (its.hasNext()) {
                //获取到key，并将它赋给mock对象的id，注意这里迭代器已经指向下一个数据了
                String key = its.next();
                Mock mock = new Mock();
                JSONObject temp = mockException.getJSONObject(key);
                String temp1 = temp.toJSONString();
                mock = mapper.readValue(temp1, Mock.class);//Json对象转为实体对象
                mock.setId(key);
                mock.splitKey();
                list.add(mock);
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param fileName 配置文件名称
     * @param mock     桩实体类，实际上删除只需要提供id
     * @return 无返回，这里象征性的返回success
     * 实际上，这个方法很可能没用
     */
    @RequestMapping("deleteMock")
    @ResponseBody
    public Object deleteMock(String fileName, Mock mock) {
        try {
            //读取配置文件并转化为json格式
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
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
            mockException.remove(mock.getId());
            //清空文件
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
            //读取传入的数据并结合原有的json对象，将其写入文件
            FileOutputStream fos = new FileOutputStream(file, true);
            OutputStreamWriter out = new OutputStreamWriter(fos, "utf-8");
            BufferedWriter bw = new BufferedWriter(out);
            //写入数据并关闭流
            String mockUrl = jsonobj.toJSONString();
            bw.write(mockUrl);
            bw.write("\r\n");
            bw.flush();
            bw.close();
            System.out.println("删除成功！");
            return "success";
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}