package com.example.query_yts_sdk_version.service.impl;

import com.example.query_yts_sdk_version.domain.MicroServicePo;
import com.example.query_yts_sdk_version.mapper.MicroServiceMapper;
import com.example.query_yts_sdk_version.service.itf.MicroServiceV1;
import com.example.query_yts_sdk_version.utils.ParamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service("microServiceV1")
public class MicroServiceV1Impl implements MicroServiceV1 {

	protected static final Logger LOG = LoggerFactory.getLogger(MicroServiceV1Impl.class);
	@Resource
	MicroServiceMapper microServiceMapper;

	@Override
	public List<MicroServicePo> getInfoByServiceNameAndEnv(String serviceName,String env) {
		return  microServiceMapper.getInfoByServiceNameAndEnv(serviceName,env);
	}
	@Override
	public List<MicroServicePo> getInfoByServiceName(String serviceName) {
		return  microServiceMapper.getInfoByServiceName(serviceName);
	}

	@Override
	public List<MicroServicePo> getInfoByEnv(String env) {
		return microServiceMapper.getInfoByEnv(env);
	}

	@Override
	public List<MicroServicePo> getMicroServiceList() {
		return microServiceMapper.getMicroServiceList();
	}

}
