package com.example.mockapi.domain.mock;

import lombok.Data;

/**
 * mdd类型的桩
 *
 * tenantId mdd模式下：租户id
 * documentType mdd模式下：单据类型
 * act mdd模式下：动作
 * ruleId mdd模式下：打桩规则
 * action MDD模式/http模式下：execute和cancel操作对应的异常，默认为execute
 */
@Data
public class MddMock extends BaseMock {
    private String tenantId;
    private String documentType;
    private String act;
    private String ruleId;
    private String action;
}
