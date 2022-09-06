package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Handler {

    static final Logger log = LoggerFactory.getLogger(Handler.class);

    Handler() {
    }

    public abstract void handle(final HttpRequest request);
}
