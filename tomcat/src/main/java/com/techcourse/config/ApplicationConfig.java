package com.techcourse.config;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.exception.GlobalExceptionHandler;
import java.util.List;
import org.apache.catalina.config.DefaultTomcatConfig;
import org.apache.catalina.controller.ExceptionHandler;
import org.apache.catalina.controller.RequestMapping;

public class ApplicationConfig extends DefaultTomcatConfig {

    private static final ExceptionHandler EXCEPTION_HANDLER = new GlobalExceptionHandler();
    private static final RequestMapping REQUEST_MAPPING = RequestMapping.of(
            List.of(
                    new LoginController(),
                    new RegisterController()
            )
    );

    @Override
    public ExceptionHandler exceptionHandler() {
        return EXCEPTION_HANDLER;
    }

    @Override
    public RequestMapping requestMapping() {
        return REQUEST_MAPPING;
    }
}
