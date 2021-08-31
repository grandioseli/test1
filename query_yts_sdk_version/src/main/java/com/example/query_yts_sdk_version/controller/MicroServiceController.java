package com.example.query_yts_sdk_version.controller;

import com.example.query_yts_sdk_version.domain.MicroServicePo;
import com.example.query_yts_sdk_version.service.itf.MicroServiceV1;
import org.apache.commons.lang3.StringUtils;
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

/**
 * 查询yts_sdk_version库信息，可以传入serviceName和env进行筛选
 */
@Controller
@RequestMapping("/governance/yts/")
public class MicroServiceController extends BaseController {
    protected static final Logger LOG = LoggerFactory.getLogger(MicroServiceController.class);
    @Autowired
    MicroServiceV1 microServiceV1;

    /**
     * 根据筛选条件查询，如果不传则不筛选
     * @param serviceName
     * @param env
     * @return
     */
    @RequestMapping(value = "queryInfo", method = RequestMethod.GET)
    @ResponseBody
    public Object queryInfo(String serviceName, String env) {
        List<MicroServicePo> result = new ArrayList<>();
        if (StringUtils.isNotBlank(serviceName) && StringUtils.isNotBlank(env)) {
            try {
                result = microServiceV1.getInfoByServiceNameAndEnv(serviceName,env);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                return buildErrorResult(ERROR);
            }
            return buildSuccess(result);
        }else if(StringUtils.isNotBlank(serviceName))
        {
            try {
                result = microServiceV1.getInfoByServiceName(serviceName);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                return buildErrorResult(ERROR);
            }
            return buildSuccess(result);
        }else if(StringUtils.isNotBlank(env))
        {
            try {
                result = microServiceV1.getInfoByEnv(env);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                return buildErrorResult(ERROR);
            }
            return buildSuccess(result);
        }
        else {
            try {
                result = microServiceV1.getMicroServiceList();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                return buildErrorResult(ERROR);
            }
            return buildSuccess(result);
        }
    }
}
