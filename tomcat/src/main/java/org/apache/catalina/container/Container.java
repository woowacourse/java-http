package org.apache.catalina.container;

import java.util.Map;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.RequestHandlerMapper;

public class Container {

    private final RequestHandlerMapper mapper;

    public Container() {
        this.mapper = new RequestHandlerMapper();
    }

    public void addMapping(Map<String, Object> mapping) {
        mapping.forEach((path, requestHandler) -> mapper.addController((RequestHandler) requestHandler, path));
    }

    public RequestHandlerMapper getRequestHandlerMapper() {
        return mapper;
    }
}
