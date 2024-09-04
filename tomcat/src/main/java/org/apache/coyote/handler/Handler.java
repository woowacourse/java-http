package org.apache.coyote.handler;

import org.apache.coyote.common.Request;

public interface Handler {
    void handle(Request request);
}
