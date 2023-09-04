package org.apache.front;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;

public interface FrontController {

    Response process(Request request);
}
