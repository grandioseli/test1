package com.example.query_yts_sdk_version.domain;

import lombok.Data;

@Data
public class MicroServicePo {
    String id;
    String serviceName;
    String env;
    String versionNum;
    String useMark;
    String useTime;
    String createTime;
    String updateTime;
}
