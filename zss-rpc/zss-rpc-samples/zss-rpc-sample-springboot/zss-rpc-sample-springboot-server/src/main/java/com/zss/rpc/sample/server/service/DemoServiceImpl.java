package com.zss.rpc.sample.server.service;
import com.zss.rpc.core.remoting.provider.annotation.RpcService;
import com.zss.rpc.sample.api.DemoService;
import com.zss.rpc.sample.api.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@RpcService
@Service
public class DemoServiceImpl implements DemoService {
    private static final Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);

    @Override
    public UserDTO sayHello(String name) {

        String word = MessageFormat.format("Hi {0}, from {1} as {2}",
                name, DemoServiceImpl.class.getName(), String.valueOf(System.currentTimeMillis()));

        if ("error".equalsIgnoreCase(name)){
            throw new RuntimeException("test exception.");
        }

        UserDTO userDTO = new UserDTO(name, word);
        logger.info(userDTO.toString());

        return userDTO;
    }
}
