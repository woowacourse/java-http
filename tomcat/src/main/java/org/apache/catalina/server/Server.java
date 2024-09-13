package org.apache.catalina.server;

import org.apache.catalina.container.Container;

public interface Server {

    Container getContainer();

    ServerProperties getServerProperties();
}
