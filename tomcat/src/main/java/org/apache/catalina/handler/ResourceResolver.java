package org.apache.catalina.handler;

import java.io.IOException;
import java.util.List;
import org.apache.coyote.Adapter;
import org.apache.coyote.http.HttpServletRequest;
import org.apache.coyote.http.HttpServletResponse;

public class ResourceResolver implements Adapter {

    private final List<ResourceHandler> handlers;

    public ResourceResolver(List<ResourceHandler> handlers) {
        this.handlers = List.copyOf(handlers);
    }

    @Override
    public HttpServletResponse service(HttpServletRequest request) throws IOException {
        return handlers.stream()
                .filter(handler -> handler.canHandle(request))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("처리할 핸들러가 없습니다."))
                .handle(request);
    }
}
