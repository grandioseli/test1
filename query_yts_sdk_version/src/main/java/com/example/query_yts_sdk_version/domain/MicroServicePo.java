package com.example.query_yts_sdk_version.domain;

import lombok.Data;

/**
 * yts_sdk_version表的信息
 *
 */
@Data
public class MicroServicePo {
    String id;
    String service_name;
    String env;
    String version_num;
    String use_mark;
    String use_time;
    String create_time;
    String update_time;
}
