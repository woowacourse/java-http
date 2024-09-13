package com.techcourse;

import org.apache.catalina.server.ServerProperties;

public class MyServerProperties extends ServerProperties {
    private static final MyServerProperties INSTANCE = new MyServerProperties();

    private MyServerProperties() {
    }

    public static MyServerProperties getInstance() {
        return INSTANCE;
    }

    public MyServerProperties(int port, int acceptCount, int maxThreads) {
        setPort(port);
        setAcceptCount(acceptCount);
        setMaxThreads(maxThreads);
    }
}
