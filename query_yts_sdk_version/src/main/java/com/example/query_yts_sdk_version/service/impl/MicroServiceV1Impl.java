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
	public MicroServicePo getInfoByServiceName(String serviceName) {
		MicroServicePo microServicePo =  microServiceMapper.getInfoByServiceName(serviceName);
		microServicePo = ParamUtils.escapeForObject(microServicePo);
		return microServicePo;
	}

	@Override
	public MicroServicePo getInfoByEnv(String env) {
		return microServiceMapper.getInfoByEnv(env);
	}

	@Override
	public List<MicroServicePo> getMicroServiceList() {
		return microServiceMapper.getMicroServiceList();
	}

}
