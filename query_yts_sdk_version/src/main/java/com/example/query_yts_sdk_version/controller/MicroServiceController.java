package com.example.query_yts_sdk_version.controller;

import com.example.query_yts_sdk_version.domain.MicroServicePo;
import com.example.query_yts_sdk_version.service.impl.MicroServiceV1Impl;
import com.example.query_yts_sdk_version.service.itf.MicroServiceV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import static com.example.query_yts_sdk_version.validate.ErrorInfoEnum.ERROR;

@Controller
@RequestMapping("/governance/yts/")
public class MicroServiceController extends BaseController {
    protected static final Logger LOG = LoggerFactory.getLogger(MicroServiceController.class);
    @Autowired
    MicroServiceV1 microServiceV1;

    @RequestMapping(value = "queryInfo", method = RequestMethod.GET)
    @ResponseBody
    public Object queryInfo() {
        List<MicroServicePo> result = new ArrayList<>();
        try {
            result = microServiceV1.getMicroServiceList();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return buildErrorResult(ERROR);
        }
        return buildSuccess(result);
    }
}
