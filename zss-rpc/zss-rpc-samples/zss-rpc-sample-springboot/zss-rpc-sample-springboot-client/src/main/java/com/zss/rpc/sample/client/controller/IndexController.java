package com.zss.rpc.sample.client.controller;

import com.zss.rpc.core.remoting.invoker.annotation.RpcReference;
import com.zss.rpc.sample.api.DemoService;
import com.zss.rpc.sample.api.dto.UserDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
public class IndexController {

    @RpcReference
    private DemoService demoService;

    @PostMapping("/sayHello")
    public UserDTO sayHello(@RequestBody String name) {
        try {
            return demoService.sayHello(name);
        } catch (Exception e) {
            e.printStackTrace();
            return new UserDTO(null, e.getMessage());
        }
    }
}
