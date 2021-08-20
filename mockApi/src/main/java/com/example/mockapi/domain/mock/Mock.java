package com.example.mockapi.domain.mock;

import lombok.Data;

import java.util.List;


/**
 * 桩信息实体类
 * <p>
 * <p>
 * type 异常类型，取值crash服务宕机,sql数据库保存失败,timeout超时网络抖动
 * msg 信息
 * position 位置放在操作前还是操作后，取值front或者rear
 * invokePosition 模拟异常执行的位置，取值CALL_SERVICE（服务调用端）,INVOKE_SERVICE（服务执行端）
 * timeout 超时时间
 * key 用作打桩key，mdd模式是由传入的参数拼接而成，其他模式则是直接获取
 * mode 打桩模式
 * tenantId mdd模式下：租户id
 * documentType mdd模式下：单据类型
 * act mdd模式下：动作
 * ruleId mdd模式下：打桩规则
 * action MDD模式/http模式下：execute和cancel操作对应的异常，默认为execute
 * packageName iris模式下：包名
 * className iris模式下：类名
 * methodName iris模式下：方法名
 * paramTypeList iris模式下：方法参数列表形如["java.lang.String","java.lang.String"]
 * httpUrl http模式下：url地址
 */
@Data
public class Mock {
    private String type;
    private String msg;
    private String position;
    private String invokePosition;
    private Integer timeout;
    private String mode;
    private String key;
    private String tenantId;
    private String documentType;
    private String act;
    private String ruleId;
    private String action;
    private String packageName;
    private String className;
    private String methodName;
    private List<String> paramTypeList;
    private String httpUrl;

    //action默认为execute
    public String getAction() {
        if (this.action == null || this.action.trim().equals("")) {
            return "execute";
        } else
            return this.action;
    }

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
                if (action != null && action.equals("cancel")) {
                    key = action + '_' + httpUrl;
                } else {
                    key = httpUrl;
                }
                break;
            case "iris":
                if (paramTypeList != null) {
                    StringBuilder params = new StringBuilder();
                    for (String param : paramTypeList) {
                        params.append(param).append(',');
                    }
                    if (params.length() > 0) {
                        params = new StringBuilder(params.substring(0, params.length() - 1));
                    }
                    key = packageName + '.' + className + '.' + methodName + '(' + params + ')';
                } else {
                    key = packageName + '.' + className + '.' + methodName + "()";
                }
                break;
        }
    }
}
