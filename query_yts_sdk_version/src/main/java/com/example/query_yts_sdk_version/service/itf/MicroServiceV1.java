package com.example.query_yts_sdk_version.service.itf;


import com.example.query_yts_sdk_version.domain.MicroServicePo;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface MicroServiceV1 {
	List<MicroServicePo> getInfoByServiceNameAndEnv(@Param("env")String env,@Param("serviceName")String serviceName);

	List<MicroServicePo> getInfoByServiceName(@Param("serviceName") String serviceName);
	
	List<MicroServicePo> getInfoByEnv(@Param("env") String env);
	
	List<MicroServicePo> getMicroServiceList();

}
