package com.example.mockapi.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *      model 模式
 *      tenant 租户id
 *      documentType 单据类型
 *      act 动作
 *      ruleId 打桩规则
 *      action 不知道怎么解释
 *      type 类型
 *      msg 信息
 *      position 打桩位置？
 *      invokePosition ?
 *      timeout 超时时间
 */
@Data
public class mock implements Serializable {
    private static final long serialVersionUID = 1L;
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
    private List<mock> trainTypeList = new ArrayList<mock>();
    public void setId(String model)
    {
        switch(model){
            case "mdd":
                id = tenant+'_'+documentType+'_'+act+'_'+ruleId+'_'+action;
                id.replace("_null","");
        }
    }
    public String getId()
    {

        return id;
    }
}
