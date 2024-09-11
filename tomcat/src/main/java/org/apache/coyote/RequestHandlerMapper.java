package org.apache.coyote;

import com.techcourse.controller.NotFoundController;
import java.util.HashMap;
import java.util.Map;
import org.apache.ResourceReader;

public class RequestHandlerMapper {

    private final Map<String, RequestHandler> mapper;
    private final RequestHandler staticResourceHandler;


    public RequestHandlerMapper() {
        this.mapper = new HashMap<>();
        this.staticResourceHandler = new StaticResourceRequestHandler();
    }

    public void addController(RequestHandler controller, String path) {
        mapper.put(path, controller);
    }

    public RequestHandler getRequestHandler(HttpRequest httpRequest) {
        if (isStaticResourceRequest(httpRequest)) {
            return staticResourceHandler;
        }
        return mapper.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(httpRequest.getPath()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(new NotFoundController());
    }

    private boolean isStaticResourceRequest(HttpRequest httpRequest) {
        return ResourceReader.canRead(httpRequest.getRequestURI()) && httpRequest.isGet();
    }
}
