package org.apache.coyote;

import java.net.Socket;

/**
 * Common interface for processors of all protocols.
 */
public interface Processor {

    /**
     * Process a connection. This is called whenever an event occurs (e.g. more data arrives) that
     * allows processing to continue for a connection that is not currently being processed.
     */
    void process(Socket socket);
}
