package com.example.mockapi.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.mockapi.domain.Mock;
import com.example.mockapi.utils.FileReaderUtil;
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

    /**
     * 向配置文件中添加桩信息,用作增加数据和修改数据接口，如果key相同则为修改数据，如果key不同则为增加数据
     *
     * @param fileName 配置文件路径
     * @param mockobj  用于承接传入的打桩数据
     * @return 如果成功象征性的返回success
     */
    @RequestMapping("addMock")
    @ResponseBody
    public Object addMock(String fileName, Mock mockobj) throws IOException {
        File file = new File(fileName);
        //读取配置文件中的json对象，这里的做法是：每次新增/修改一个打桩项，首先读取原有的json数据并做修改，然后清空配置文件
        //将组成的新的json数据写入文件
        JSONObject jsonobj;
        String jsonStr = FileReaderUtil.readStringFromFile(file);
        //判断文件中的数据是否是json格式
        try {
            jsonobj = JSON.parseObject(jsonStr);
        } catch (JSONException i) {
            return "添加失败：配置文件中的数据不是json格式！";
        }
        //如果顶级目标中没有数据,需要重新创建json对象
        if (jsonobj == null) {
            jsonobj = new JSONObject();
            String ytsMock = "yts.mock";
            JSONObject obj = new JSONObject();
            jsonobj.put(ytsMock, obj);
        }
        //如果顶级目标有json数据但是没有yts.mock节点
        else if (!jsonobj.containsKey("yts.mock")) {
            String ytsMock = "yts.mock";
            JSONObject obj = new JSONObject();
            jsonobj.put(ytsMock, obj);
        }
        //清空文件
        FileReaderUtil.cleanUpFile(file);
        //读取传入的数据并结合原有的json对象，将其写入文件
        FileOutputStream fos = new FileOutputStream(file, true);
        OutputStreamWriter out = new OutputStreamWriter(fos, "utf-8");
        BufferedWriter bw = new BufferedWriter(out);
        String mockKey = "";
        //根据模式设定key
        mockobj.setIdBymodel();
        mockKey = mockobj.getId();
        //新增的数据为JSONObject对象
        JSONObject mock = new JSONObject();
        //value也是JSONObject对象，读取由前端传入的各个值
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
        if (mockobj.getModel() != null) {
            mockException.put("model", mockobj.getModel());
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
        return "success";
    }

    /**
     * 根据配置文件获取配置信息
     *
     * @param fileName 配置文件路径
     * @return 返回对象为list, 值为mock实体类
     */
    @RequestMapping("getMock")
    @ResponseBody
    public Object getMock(String fileName) {
        //用于将json转化为实体类
        ObjectMapper mapper = new ObjectMapper();
        try {
            //读取配置文件并转化为json格式
            File file = new File(fileName);
            String jsonStr = FileReaderUtil.readStringFromFile(file);
            JSONObject jsonobj;
            //判断文件中的数据是否是json格式
            try {
                jsonobj = JSON.parseObject(jsonStr);
            } catch (JSONException i) {
                return "获取失败：配置文件中的数据不是json格式！";
            }
            //配置文件里没有json数据或者有json数据却没有yts.mock,就添加这个节点
            //如果顶级目标中没有数据,需要重新创建json对象
            if (jsonobj == null) {
                jsonobj = new JSONObject();
                String ytsMock = "yts.mock";
                JSONObject obj = new JSONObject();
                jsonobj.put(ytsMock, obj);
            }
            //如果顶级目标有json数据但是没有yts.mock节点
            else if (!jsonobj.containsKey("yts.mock")) {
                String ytsMock = "yts.mock";
                JSONObject obj = new JSONObject();
                jsonobj.put(ytsMock, obj);
            }
            JSONObject mockException = jsonobj.getJSONObject("yts.mock");
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
                try {
                    mock.splitKey();
                }catch(ArrayIndexOutOfBoundsException e)
                {
                    mock.setAction(null);
                }
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
     * @return 如果成功象征性的返回success
     * 实际上，这个方法很可能没用
     */
    @RequestMapping("deleteMock")
    @ResponseBody
    public Object deleteMock(String fileName, Mock mock) {
        try {
            //读取配置文件并转化为json格式
            File file = new File(fileName);
            String jsonStr = FileReaderUtil.readStringFromFile(file);
            JSONObject jsonobj;
            try {
                jsonobj = JSON.parseObject(jsonStr);
            } catch (JSONException i) {
                return "删除失败：配置文件中的数据不是json格式";
            }
            if (jsonobj == null) {
                return "删除失败：配置文件中没有数据";
            }
            if (!jsonobj.containsKey("yts.mock")) {
                return "删除失败：json数据中未找到yts.mock";
            }
            JSONObject mockException = jsonobj.getJSONObject("yts.mock");
            if (!mockException.containsKey(mock.getId())) {
                return "删除失败：yts.mock中没有找到key";
            }
            mockException.remove(mock.getId());
            //清空文件
            FileReaderUtil.cleanUpFile(file);
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
            return "success";
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}