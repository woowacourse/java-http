package org.apache.catalina.core;

import java.net.Socket;

public interface Container {
    void execute(Socket connection);
}
