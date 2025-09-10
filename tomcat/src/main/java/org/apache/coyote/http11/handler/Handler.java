package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.dto.HttpRequest;

public interface Handler {

    HandlerResult doHandle(HttpRequest request);
}
