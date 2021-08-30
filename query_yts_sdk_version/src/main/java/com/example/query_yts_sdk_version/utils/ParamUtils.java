package com.example.query_yts_sdk_version.utils;

import com.yonyou.iuap.context.InvocationInfoProxy;

public class ParamUtils {

    private static final String LOGKEY = "log.temp.key";
    private static final String STRINGKEY = "tempstr.key";
    private static final String LOGKEYOBJ = "log.temp.key.obj";

    public static String escapeForLog(String strs) {
        pushForLog(strs);
        return popLog();
    }

    private static void pushForLog(String strs) {
        Object value = strs.replace('\t', '_').replace('\n', '_').replace('\r', '_');
        InvocationInfoProxy.setExtendAttribute(LOGKEY, value);
    }

    private static String popLog() {
        return (String) InvocationInfoProxy.getExtendAttribute(LOGKEY);
    }

    public static String escapeForString(String strs) {
        pushForString(strs);
        return popString();
    }

    private static void pushForString(String strs) {
        InvocationInfoProxy.setExtendAttribute(STRINGKEY, strs);
    }

    private static String popString() {
        return (String) InvocationInfoProxy.getExtendAttribute(STRINGKEY);
    }

    private static void pushForObject(Object obj) {
        InvocationInfoProxy.setExtendAttribute(LOGKEYOBJ, obj);
    }

    public static <T> T escapeForObject(T obj) {
        pushForObject(obj);
        return (T) popObj();
    }

    private static Object popObj() {
        return InvocationInfoProxy.getExtendAttribute(LOGKEYOBJ);
    }
}
