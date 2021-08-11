package com.example.mockapi.domain;

import lombok.Data;


/**
 * tenant 租户id
 * documentType 单据类型
 * act 动作
 * ruleId 打桩规则
 * action 不知道怎么解释
 * type 类型
 * msg 信息
 * position 打桩位置？
 * invokePosition ?
 * timeout 超时时间
 * id 用作打桩key，mdd模式是由传入的参数拼接而成，其他模式则是直接获取
 * model 桩的模式
 * 123
 */
@Data
public class Mock {
    private String tenant;
    private String documentType;
    private String act;
    private String ruleId;
    private String action;
    private String type;
    private String msg;
    private String position;
    private String invokePosition;
    private Integer timeout;
    private String id;
    private String model;

    public void setIdBymodel() {
        switch (this.model) {
            case "mdd":
                id = tenant + '_' + documentType + '_' + act + '_' + ruleId + '_' + action;
//                id = id.replace("_null", "");
                break;
            case "http":
                break;
            case "iris":
                break;
        }
    }

    public void splitKey() {
        switch (this.model) {
            case "mdd": {
                String[] strARR = id.split("_");
                this.tenant = strARR[0];
                this.documentType = strARR[1];
                this.act = strARR[2];
                this.ruleId = strARR[3];
                this.action = strARR[4];
                break;
            }
            case "http":
                break;
            case "iris":
                break;
        }
    }
}
