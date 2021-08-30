package com.example.query_yts_sdk_version.mapper;

import com.example.query_yts_sdk_version.domain.MicroServicePo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Mapper
public interface MicroServiceMapper {
	
	@Select("SELECT * FROM yts_sdk_version WHERE service_name = #{serviceName}")
	MicroServicePo getInfoByServiceName(@Param("serviceName") String serviceName);
	
	@Select("SELECT * FROM yts_sdk_version WHERE env = #{env}")
	MicroServicePo getInfoByEnv(@Param("env") String env);
	
	@Select("SELECT * FROM yts_sdk_version")
	List<MicroServicePo> getMicroServiceList();

}
