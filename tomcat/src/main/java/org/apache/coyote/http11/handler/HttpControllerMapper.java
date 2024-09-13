package org.apache.coyote.http11.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.message.request.HttpRequest;

public class HttpControllerMapper {

    private final Map<String, Controller> mapper;

    public HttpControllerMapper(Map<String, Controller> mapper) {
        this.mapper = new HashMap<>(mapper);
    }

    public Optional<Controller> findController(HttpRequest request) {
        return Optional.ofNullable(mapper.get(request.getUrlPath()));
    }
}
