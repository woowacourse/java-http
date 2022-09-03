package org.apache.coyote.request;

import org.apache.coyote.exception.HttpException;
import org.apache.coyote.response.HttpResponse;

public class CustomServlet {

    private final HttpRequestHandler requestMapper = new HttpRequestHandler();

    public HttpResponse service(HttpRequest request) {
        try {
            return requestMapper.handle(request);
        } catch (HttpException exception) {
            return requestMapper.handle(exception);
        }
    }
}
