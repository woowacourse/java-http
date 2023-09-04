package org.apache.front;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;
import org.apache.coyote.response.StaticResponse;

public class StaticController implements FrontController {

    @Override
    public Response process(Request request) {
        return new StaticResponse(request.getFileType(), request.getPath());
    }
}
