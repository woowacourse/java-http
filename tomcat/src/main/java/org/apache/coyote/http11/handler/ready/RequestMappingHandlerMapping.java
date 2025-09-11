package org.apache.coyote.http11.handler.ready;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.dto.HttpRequest;

public class RequestMappingHandlerMapping implements HandlerMapping {

    private final Map<String, Controller> mappings = new HashMap<>();

    @Override
    public int getOrder() {
        return 1;
    }

    public void addMapping(String path, Controller controller) {
        mappings.put(path, controller);
    }

    @Override
    public  Controller getHandler(HttpRequest request) {
        return mappings.get(request.path());
    }
}
