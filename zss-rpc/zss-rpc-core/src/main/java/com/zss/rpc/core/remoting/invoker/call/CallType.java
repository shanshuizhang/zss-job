package com.zss.rpc.core.remoting.invoker.call;

/**
 * 调用方式类型枚举
 */
public enum CallType {
    /**
     * 同步方式
     */
    SYNC,

    /**
     * 异步future方式
     */
    FUTURE,

    /**
     * 回调方式
     */
    CALLBACK,

    /**
     * 单向调用
     */
    ONEWAY;

    public static CallType match(String name,CallType defaultCallType){
        for(CallType callType : CallType.values()){
            if(callType.name().equals(name)){
                return callType;
            }
        }
        return defaultCallType;
    }
}
