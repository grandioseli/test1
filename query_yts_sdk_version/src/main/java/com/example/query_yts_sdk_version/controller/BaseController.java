package com.example.query_yts_sdk_version.controller;

import com.example.query_yts_sdk_version.utils.ParamUtils;
import com.example.query_yts_sdk_version.validate.ErrorInfoEnum;
import com.yonyou.iuap.mvc.serializer.RestFulResponse;
import com.yonyou.iuap.mvc.type.JsonResponse;

public class BaseController {

	/*
	 * public JsonErrorResponse buildCommonErrorResult(String errorMessage) {
	 * JsonErrorResponse errorResponse = new JsonErrorResponse();
	 * errorResponse.setError_code(JsonErrorResponse.DEFAULT_ERROE_CODE);
	 * errorResponse.setError_message(errorMessage); return errorResponse; }
	 * 
	 * public JsonErrorResponse buildCommonErrorResult(String errorCode,String
	 * errorMessage) { JsonErrorResponse errorResponse = new JsonErrorResponse();
	 * errorResponse.setError_code(errorCode);
	 * errorResponse.setError_message(errorMessage); return errorResponse; }
	 * 
	 * public <T> JsonResponse buildSuccess(Object value) { JsonResponse response =
	 * new JsonResponse(); response.getDetailMsg().put("data", value); return
	 * response; }
	 * 
	 * public <T> JsonResponse buildSuccess() { JsonResponse response = new
	 * JsonResponse(); return response; }
	 * 
	 * public JsonResponse buildMapSuccess(Map msgMap) { JsonResponse response = new
	 * JsonResponse(); response.setDetailMsg(msgMap); return response; }
	 */
	public <T> JsonResponse buildSuccess(Object value) {
		JsonResponse response = new JsonResponse();
		value = ParamUtils.escapeForObject(value);
		response.getDetailMsg().put("data", value);
		return response;
	}

	public <T> RestFulResponse buildResult(Object value) {
		RestFulResponse response = new RestFulResponse();
		value = ParamUtils.escapeForObject(value);
		response.setData(value);
		response.setFlag(RestFulResponse.FLAG_SUCCESS);
		response.setMessage("操作成功!");
		return response;
	}

	public <T> RestFulResponse buildResult(String message, Object value) {
		RestFulResponse response = new RestFulResponse();
		value = ParamUtils.escapeForObject(value);
		response.setData(value);
		response.setFlag(RestFulResponse.FLAG_SUCCESS);
		message = ParamUtils.escapeForObject(message);
		response.setMessage(message);
		return response;
	}

	public <T> RestFulResponse buildErrorResult() {
		RestFulResponse response = new RestFulResponse();
		response.setFlag(RestFulResponse.FLAG_FAIL);
		response.setMessage("操作失败!");
		response.setData(500);
		return response;
	}

	public <T> RestFulResponse buildErrorResult(String errMsg, String errCode) {
		RestFulResponse response = new RestFulResponse();
		response.setFlag(RestFulResponse.FLAG_FAIL);
		errMsg = ParamUtils.escapeForObject(errMsg);
		response.setMessage(errMsg);
		response.setData(errCode);
		return response;
	}

	public <T> RestFulResponse buildErrorResult(ErrorInfoEnum err) {
		RestFulResponse response = new RestFulResponse();
		response.setFlag(RestFulResponse.FLAG_FAIL);
		response.setMessage(err.getName());
		response.setData(err.getCode());
		return response;
	}

	public <T> JsonResponse buildError(String msg) {
		JsonResponse response = new JsonResponse();
		response.setMessage(msg);
		return response;
	}
}
