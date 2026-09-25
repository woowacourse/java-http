package org.apache.coyote;

import java.net.Socket;

@FunctionalInterface
public interface ProcessorFactory {

    Runnable create(Socket connection);
}
