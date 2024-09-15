package org.apache.catalina.server;

import org.apache.catalina.container.Container;

public interface ApplicationContext {

    Container getContainer();

    ApplicationConfig getServerProperties();
}
