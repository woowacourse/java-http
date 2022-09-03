package org.apache.coyote.request;

import org.apache.coyote.exception.HttpException;

public class CustomServlet {

    private final HttpRequestHandler requestMapper = new HttpRequestHandler();

    public String service(HttpRequest request) {
        try {
            return requestMapper.handle(request);
        } catch (HttpException exception) {
            return requestMapper.handle(exception);
        }
    }
}
