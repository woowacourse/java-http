package com.techcourse;

import org.apache.catalina.container.Container;
import org.apache.catalina.server.ApplicationContext;
import org.apache.catalina.server.ApplicationConfig;

public class MyApplicationContext implements ApplicationContext {

    private final Container container;
    private final ApplicationConfig applicationConfig;

    public MyApplicationContext() {
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
