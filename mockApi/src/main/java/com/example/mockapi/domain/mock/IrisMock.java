package com.example.mockapi.domain.mock;

import lombok.Data;

import java.util.List;

/**
 * iris类型的桩
 * <p>
 * packageName iris模式下：包名
 * className iris模式下：类名
 * methodName iris模式下：方法名
 * paramTypeList iris模式下：方法参数列表形如["java.lang.String","java.lang.String"]
 * action iris模式下:阶段
 */
@Data
public class IrisMock extends BaseMock {
    private String packageName;
    private String className;
    private String methodName;
    private List<String> paramTypeList;
    private String action;

}
