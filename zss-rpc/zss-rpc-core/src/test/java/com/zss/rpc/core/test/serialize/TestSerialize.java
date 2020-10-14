package com.zss.rpc.core.test.serialize;

import com.zss.rpc.core.serialize.impl.Hessian2Serializer;

public class TestSerialize {
    public static void main(String[] args) {
        User user = new User();
        user.setName("张三");
        user.setAge(20);
        user.setAddress("深圳市龙岗区Mall3栋1001");

        System.out.println(user.toString());
        Hessian2Serializer hessian2Serializer = new Hessian2Serializer();
        byte[] users = hessian2Serializer.serialize(user);

        User user1 = hessian2Serializer.deserialize(users);
        System.out.println(user1.toString());
    }
}
