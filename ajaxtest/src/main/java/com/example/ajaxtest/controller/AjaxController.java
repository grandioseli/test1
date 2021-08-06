package com.example.ajaxtest.controller;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

//import com.alibaba.fastjson.JSONObject;

@Controller
public class AjaxController {
    @CrossOrigin(origins = "*",maxAge = 3600)
    @RequestMapping(value="/ajaxString")
    @ResponseBody
    public Object a(String[] sa, String sb) {
        int number = sa.length;
        String result ="";
        for(int i = 0;i<number;i++)
        {
            result = result +'_'+ sa[i];
        }
        result = result +'_'+sb;
        return result;
    }
    @CrossOrigin(origins = "*",maxAge = 3600)
    @RequestMapping(value="/ajaxInteger")
    public void b(@RequestParam(value="ia[]") Integer[] ia, Integer ib, HttpServletRequest request, HttpServletResponse response)
            throws IOException{
        response.getWriter().println("ia: " + ia[1] + ", ib: " + ib);
    }

    //@RequestBody JSONObject json 把ajax提交的josn参数绑定到JSONObject类型的josn中，可以用来接受List，Map，Date等格式
    //然后通过JSONObject的方法进行类型转换
//    @RequestMapping(value="/ajaxList")
//    public void d(@RequestBody JSONObject json, HttpServletRequest request, HttpServletResponse response)
//            throws IOException{
//        String gid = json.getString("gid");
//        String myList = json.getString("myList");
//        //转换成List类型
//        List<String> myList1 = json.getObject("myList", List.class);
//        System.out.println(myList1.size());
//
//        //转换成Map类型
//        Map<String, String> myMap = json.getObject("myMap", Map.class);
//        Set<String> myMapKeySet = myMap.keySet();
//        for(Iterator<String> iter = myMapKeySet.iterator(); iter.hasNext(); ){
//            String index = iter.next();
//            System.out.println("key: " + index + " value: " + myMap.get(index));
//        }
//
//        System.out.println(myList);
////        response.getWriter().println("gid: " + gid + ", db: " + myList);
//        response.getWriter().print(json);
//    }
}
