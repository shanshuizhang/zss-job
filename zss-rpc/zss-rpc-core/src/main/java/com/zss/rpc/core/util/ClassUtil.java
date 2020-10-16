package com.zss.rpc.core.util;

import java.util.HashMap;
import java.util.Map;

public class ClassUtil {

    private static final Map<String,Class<?>> BASE_TYPE = new HashMap<>();

    static {
        BASE_TYPE.put("boolean", boolean.class);
        BASE_TYPE.put("byte", byte.class);
        BASE_TYPE.put("char", char.class);
        BASE_TYPE.put("short", short.class);
        BASE_TYPE.put("int", int.class);
        BASE_TYPE.put("long", long.class);
        BASE_TYPE.put("float", float.class);
        BASE_TYPE.put("double", double.class);
        BASE_TYPE.put("void", void.class);
    }

    public static Class resolveClass(String className) throws ClassNotFoundException {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            Class clazz = BASE_TYPE.get(className);
            if(clazz == null){
                throw e;
            }
            return clazz;
        }
    }
}
