package com.example.query_yts_sdk_version.validate;

public enum ErrorInfoEnum {

	// 按条件查询分页 条件不合格
	ERROR_PAGE_CONDITION("1501", "当前用户不符合查询条件!"),
	// 按条件查询分页 获取不到结果
	ERROR_PAGE_QUERY("1502", "分页获取错误事务信息失败!"),
	// 根据条件批量查询管理员需要忽略的数据信息条件不合格
	ERROR_LISTIGNORES_CONDITION("1503", "根据条件批量查询管理员需要忽略的数据信息条件不合格!"),
	// 根据条件批量查询管理员需要忽略的数据信息查询过程出错
	ERROR_LISTIGNORES("1504", "根据条件批量查询管理员需要忽略的数据信息查询过程出错!"),
	
	// 更新日志事务状态时 条件不合格
	ERROR_UPDATE_CONDITION("1505", "更新日志事务状态时条件不合格!"),
	// 更新日志事务状态过程中出错
	ERROR_UPDATE("1506", "更新日志事务状态过程中出错!"),
	
	// 批量忽略，应对大规模报错的事务日志批量处理场景时查询条件不合格
	ERROR_BATCH_IGNORE_CON("1507", "批量忽略，应对大规模报错的事务日志批量处理场景时查询条件不合格!"),
	// 批量忽略，应对大规模报错的事务日志批量处理场景时过程出错
	ERROR_BATCH_IGNORE("1508", "批量忽略，应对大规模报错的事务日志批量处理场景时过程出错!"),

	// sdk上传事务日志，sdk端上报的异常日志、重试成功和重试失败的日志条件出错
	UERROR_UPLOAD_CONDITION("1601", "sdk上传事务日志参数出错!"),
	// sdk上传事务日志，sdk端上报的异常日志、重试成功和重试失败的日志过程出错
	ERROR_UPLOAD("1602", "sdk上传事务日志出错!"),
	
	// sdk下载事务日志参数条件出错
	ERROR_DOWN_CONDITION("1603", "sdk下载事务日志查询条件出错!"),
	// sdk下载事务日志过程出现错误
	ERROR_DOWNLOAD("1604", "sdk下载事务日志过程出现错误!"),
	
	// sdk更新云端日志状态过程出现错误
	ERROR_SYNC("1605", "sdk更新云端日志状态过程出现错误，请检查数据状态和格式是否合法!"),
	
	SUCCESS("success", "success"),
	ERROR("error", "error"),
	OK("ok", "ok");
	
	private String code;
	private String name;

	private ErrorInfoEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static ErrorInfoEnum getEnum(String code) {
		for (ErrorInfoEnum e : ErrorInfoEnum.values()) {
			if (code.equals(e.getCode())) {
				return e;
			}
		}
		return null;
	}

}
