package com.techcourse.config;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.ThreadTestController;
import org.apache.catalina.controller.RequestMapper;

public class RequestMapperConfig {

    private final RequestMapper requestMapper;

    public RequestMapperConfig() {
        this.requestMapper = RequestMapper.getInstance();
    }

    public void configure() {
        requestMapper.addMapping("/login", new LoginController());
        requestMapper.addMapping("/register", new RegisterController());
        requestMapper.addMapping("/test", new ThreadTestController());
    }
}
