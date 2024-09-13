package com.techcourse;

import org.apache.catalina.container.Container;
import org.apache.catalina.server.Server;
import org.apache.catalina.server.ServerProperties;

public class MyServer implements Server {

    private final Container container;
    private final ServerProperties serverProperties;

    public MyServer() {
        this.container = MyContainer.getInstance();
        this.serverProperties =  MyServerProperties.getInstance();
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public ServerProperties getServerProperties() {
        return serverProperties;
    }
}
