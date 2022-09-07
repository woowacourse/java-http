package org.apache.coyote.http11.handler;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Handler {

    static final Logger log = LoggerFactory.getLogger(Handler.class);

    public Handler() {
    }

    public abstract HandlerResult handle(final HttpRequest request) throws IOException;
}
