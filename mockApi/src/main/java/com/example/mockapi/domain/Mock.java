package com.example.mockapi.domain;

import lombok.Data;


/**
 * 基础桩
 *
 * groupId 租户id
 * documentType 单据类型
 * act 动作
 * ruleId 打桩规则
 * action MDD框架下execute和cancel操作对应的异常，默认为execute
 * type 异常类型，取值crash服务宕机,sql数据库保存失败,timeout超时网络抖动
 * msg 信息
 * position 位置放在操作前还是操作后，取值front或者rear
 * invokePosition 模拟异常执行的位置，取值CALL_SERVICE（服务调用端）,INVOKE_SERVICE（服务执行端）
 * timeout 超时时间
 * key 用作打桩key，mdd模式是由传入的参数拼接而成，其他模式则是直接获取
 * mode 打桩模式
 */
@Data
public class Mock {
    private String tenantId;
    private String documentType;
    private String act;
    private String ruleId;
    private String action;
    private String type;
    private String msg;
    private String position;
    private String invokePosition;
    private Integer timeout;
    private String key;
    private String mode;

    public void setKeyByMode() {
        switch (this.mode) {
            case "mdd":
                if (action != null && action.equals("cancel")) {
                    key = tenantId + '_' + documentType + '_' + act + '_' + ruleId + '_' + action;
                } else {
                    key = tenantId + '_' + documentType + '_' + act + '_' + ruleId;
                }
                break;
            case "http":
                break;
            case "iris":
                break;
        }
    }

    public void splitKey() {
        switch (this.mode) {
            case "mdd": {
                String[] strARR = key.split("_");
                if (strARR.length == 5) {
                    this.tenantId = strARR[0];
                    this.documentType = strARR[1];
                    this.act = strARR[2];
                    this.ruleId = strARR[3];
                    this.action = strARR[4];
                } else {
                    this.tenantId = strARR[0];
                    this.documentType = strARR[1];
                    this.act = strARR[2];
                    this.ruleId = strARR[3];
                    this.action = "execute";
                }
                break;
            }
            case "http":
                break;
            case "iris":
                break;
        }
    }
}
