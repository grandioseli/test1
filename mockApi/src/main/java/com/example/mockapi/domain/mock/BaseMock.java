package com.example.mockapi.domain.mock;

import lombok.Data;

/**
 * 用于填入列表的基础桩
 * <p>
 * type 异常类型，取值crash服务宕机,sql数据库保存失败,timeout超时网络抖动
 * msg 信息
 * position 位置放在操作前还是操作后，取值front或者rear
 * invokePosition 模拟异常执行的位置，取值CALL_SERVICE（服务调用端）,INVOKE_SERVICE（服务执行端）
 * timeout 超时时间
 * key 用作打桩key，mdd模式是由传入的参数拼接而成，其他模式则是直接获取
 */
@Data
public class BaseMock {
    protected String type;
    protected String msg;
    protected String position;
    protected String invokePosition;
    protected Integer timeout;
    protected String mode;
    protected String key;
}
