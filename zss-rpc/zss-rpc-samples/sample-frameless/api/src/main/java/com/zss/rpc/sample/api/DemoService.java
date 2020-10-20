package com.zss.rpc.sample.api;

import com.zss.rpc.sample.api.dto.UserDTO;

public interface DemoService {
    UserDTO sayHello(String name);
}
