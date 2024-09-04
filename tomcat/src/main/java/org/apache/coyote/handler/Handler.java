package org.apache.coyote.handler;

import org.apache.coyote.common.Request;
import org.apache.coyote.common.Response;

public interface Handler {
    Response handle(Request request);
}
