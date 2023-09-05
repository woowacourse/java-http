package org.apache.coyote.http11.common;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public interface HandlerMethod {

    Response handle(Request request);
    
}
