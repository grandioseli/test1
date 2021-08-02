package com.example.mockapi.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@RestController
@RequestMapping("/yts/")
public class MockController {
    @RequestMapping("MockApi")
    public Object returnLink(@RequestBody JSONObject s) throws UnsupportedEncodingException {
        String mockUrl = s.toJSONString();
        mockUrl = URLEncoder.encode(mockUrl,"utf-8");
        return mockUrl;
    }
}