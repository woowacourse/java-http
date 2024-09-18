package org.apache.catalina.server;

import org.apache.catalina.container.Container;

public interface Context {

    Container getContainer();

    ApplicationConfig getApplicationConfig();
}
