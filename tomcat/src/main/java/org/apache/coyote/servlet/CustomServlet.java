package org.apache.coyote.servlet;

import org.apache.coyote.exception.HttpException;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.HttpRequestHandler;
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
