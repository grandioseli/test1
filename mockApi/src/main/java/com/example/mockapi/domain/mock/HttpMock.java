package com.example.mockapi.domain.mock;

import lombok.Data;

/**
 * http类型的桩
 *
 * httpUrl http模式下：url地址
 * action MDD模式/http模式下：execute和cancel操作对应的异常，默认为execute
 */
@Data
public class HttpMock extends BaseMock {
    private String httpUrl;
    private String action;
}
