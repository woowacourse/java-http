package org.apache.coyote.http11.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.HttpRequestInfo;

public class HttpHandlerMapper {

    private final Map<HttpRequestInfo, HttpHandler> mapper;

    public HttpHandlerMapper(Map<HttpRequestInfo, HttpHandler> mapper) {
        this.mapper = new HashMap<>(mapper);
    }

    public Optional<HttpHandler> findHandler(HttpRequest request) {
        return Optional.ofNullable(mapper.get(request.getRequestInfo()));
    }
}
