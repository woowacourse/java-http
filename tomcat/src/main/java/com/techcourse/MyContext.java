package com.techcourse;

import org.apache.catalina.container.Container;
import org.apache.catalina.server.Context;
import org.apache.catalina.server.ApplicationConfig;

public class MyContext implements Context {

    private final Container container;
    private final ApplicationConfig applicationConfig;

    public MyContext() {
        this.container = MyContainer.getInstance();
        this.applicationConfig =  MyApplicationConfig.getInstance();
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public ApplicationConfig getServerProperties() {
        return applicationConfig;
    }
}
