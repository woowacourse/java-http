package org.apache.catalina.connector;

import java.net.Socket;

public interface ConnectionListener {
    void onConnection(Socket connection);
}
