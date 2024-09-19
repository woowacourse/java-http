package com.techcourse;

import org.apache.catalina.container.Container;
import org.apache.catalina.ExceptionHandler;
import org.apache.catalina.HandlerMapping;

import com.techcourse.controller.ControllerMapping;
import com.techcourse.controller.exception.ExceptionMapping;

public class MyContainer implements Container {

    private static final MyContainer INSTANCE = new MyContainer();

    private MyContainer() {
    }

    public static MyContainer getInstance() {
        return INSTANCE;
    }

    @Override
    public HandlerMapping getHandlerMapping() {
        return ControllerMapping.getInstance();
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return ExceptionMapping.getInstance();
    }
}
