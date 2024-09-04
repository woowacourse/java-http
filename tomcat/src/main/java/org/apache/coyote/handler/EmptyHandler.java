package org.apache.coyote.handler;

import org.apache.coyote.common.Request;

public class EmptyHandler implements Handler {

    private static final EmptyHandler instance = new EmptyHandler();

    private EmptyHandler() {
    }

    public static EmptyHandler getInstance() {
        return instance;
    }

    @Override
    public void handle(Request request) {
        // do nothing
    }
}
