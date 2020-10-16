package com.zss.rpc.core.util;

public class ServiceKeyBuilder {

    /**
     * 建造serviceKey
     * @param className
     * @param version
     * @return
     */
    public static String buildServiceKey(String className,String version){
        if(version != null && version.trim().length() > 0){
            className = className + "#" + version;
        }
        return className;
    }
}
