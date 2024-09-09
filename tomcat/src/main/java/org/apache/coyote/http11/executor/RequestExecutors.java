package org.apache.coyote.http11.executor;

import com.techcourse.controller.Controller;
import com.techcourse.executor.ResourceController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestMapper;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.Map;
import java.util.Optional;

public class RequestExecutors {
    private final RequestMapper mapper;
    private final ResourceController resourceController;

    public RequestExecutors(final Map<String, Controller> controllers) {
        this.mapper = new RequestMapper(controllers);
        this.resourceController = new ResourceController();
    }

    public HttpResponse execute(final HttpRequest req) {
        return Optional.ofNullable(mapper.mappingWithController(req))
                .map(controller -> controller.service(req))
                .orElseGet(() -> resourceController.service(req));
    }
}

