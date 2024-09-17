package com.techcourse;

import org.apache.catalina.server.ApplicationConfig;

public class MyApplicationConfig extends ApplicationConfig {

    private static final MyApplicationConfig INSTANCE = new MyApplicationConfig();

    private MyApplicationConfig() {
    }

    public static MyApplicationConfig getInstance() {
        return INSTANCE;
    }

    public MyApplicationConfig(int port, int acceptCount, int maxThreads) {
        setPort(port);
        setAcceptCount(acceptCount);
        setMaxThreads(maxThreads);
    }
}
