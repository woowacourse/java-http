package org.apache.coyote.request;

import org.apache.coyote.exception.HttpException;

public class HttpRequestMapper {

    private final HttpRequestHandler requestMapper = new HttpRequestHandler();

    public String handle(HttpRequest request) {
        try {
            return requestMapper.handle(request);
        } catch (HttpException exception) {
            return requestMapper.handle(exception);
        }
    }
}
