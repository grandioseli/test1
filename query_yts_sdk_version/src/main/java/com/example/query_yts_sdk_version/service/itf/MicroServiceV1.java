package com.example.query_yts_sdk_version.service.itf;


import com.example.query_yts_sdk_version.domain.MicroServicePo;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface MicroServiceV1 {
	MicroServicePo getInfoByServiceName(@Param("serviceName") String serviceName);
	
	MicroServicePo getInfoByEnv(@Param("env") String env);
	
	List<MicroServicePo> getMicroServiceList();

}
