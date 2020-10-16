package com.zss.rpc.core.test.serialize;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TestReflact {

    private TestCompantScan compantScan;

    private String str;

    public TestCompantScan getCompantScan() {
        return compantScan;
    }

    public String getStr() {
        return str;
    }

    public void setCompantScan(TestCompantScan compantScan) {
        this.compantScan = compantScan;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public static void main(String[] args) {
        Class clazz = TestReflact.class;
        System.out.println(clazz.getName());
        Field[] fields = clazz.getDeclaredFields();
        for(Field field: fields){
            String className = field.getDeclaringClass().getName();
            System.out.println("field所属的类名："+ className);
        }

        Method[] methods = clazz.getDeclaredMethods();
        for(Method method : methods){
            String className = method.getDeclaringClass().getName();
            System.out.println("method所属的类名："+ className);
            System.out.println("方法名："+ method.getName());

            //方法参数类型
            Class<?>[] paramterTypes = method.getParameterTypes();
            for(Class cls:paramterTypes){
                System.out.println("参数类型"+cls.getName());
            }
        }
    }
}
