package org.apache.coyote.http11.hadler;

import org.apache.coyote.http11.data.Request;
import org.apache.coyote.http11.data.Response;

public interface RequestHandler {
        Response handle(Request request);
        boolean canHandle(Request request);
    }