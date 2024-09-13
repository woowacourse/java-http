package org.apache.catalina.connector;

import org.apache.catalina.Manager;
import org.apache.coyote.http11.Http11Processor;

import java.net.Socket;

class CoyoteProcessorFactory {

    public Http11Processor getHttp11Processor(Socket connection, Manager manager) {
        return new Http11Processor(connection, manager);
    }
}
