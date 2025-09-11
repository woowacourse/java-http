package org.apache.coyote.http11.dispatcher;

import com.techcourse.controller.HelloController;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.request.HttpRequest;

public class HandlerMapping {

    private static final Map<String, Object> mappings = new ConcurrentHashMap<>();

    public HandlerMapping() {
        init();
    }

    public static void init() {
        mappings.put("/", new HelloController());
    }

    public Object getHandler(HttpRequest httpRequest) {
        return mappings.get(httpRequest.getMappingLine().getPath());
    }
}
